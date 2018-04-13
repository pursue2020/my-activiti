/**
 * 
 */
package com.tantan.workflow.bo.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tantan.workflow.bo.FlowManageBO;
import com.tantan.workflow.config.WorkFlowConfigurable;
import com.tantan.workflow.entity.EngineInstanceEntity;
import com.tantan.workflow.exception.WorkFlowException;
import com.tantan.workflow.util.ExtJSResponse;
import com.tantan.workflow.util.XMLUtils;

/**
 * @author tantan
 *
 */
@Slf4j
@Component
public class FlowManageBOImpl implements FlowManageBO,WorkFlowConfigurable{
	
    static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd hh:mm:ss").create();
		
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ManagementService managementService;
	@Autowired
	private RuntimeService runtimeService;

	@Override
	public ExtJSResponse deploy(String bpmnText) {
		if(bpmnText==null || bpmnText.trim().length()==0){
			return ExtJSResponse.errorRes("请求数据为空");
		}
		log.info(bpmnText);
		
		//用于保存已经新建成功的实例，便于在抛出异常的时候删除
		List<EngineInstanceEntity> instances = new ArrayList<EngineInstanceEntity>();
		
		//历史流程实例
		List<EngineInstanceEntity> hisInstances = null;
		Deployment deployment = null;
		try {
			//获取processKey
			Element root = XMLUtils.getRootElement(bpmnText);
			Element process = XMLUtils.getElementByName(root, "process");
			String processKey = XMLUtils.getAtrributeByName(process, "id");
			
			//查询历史部署流程定义
			ProcessDefinition processDefinition = repositoryService
					.createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult();
			
			hisInstances = getHisEngineInstances(processDefinition);
			
			//新部署的流程向引擎申请instanceId
			long t1 = System.currentTimeMillis();
			bpmnText = applyNewInstance(bpmnText,instances);
			long t2 = System.currentTimeMillis();
			
			log.info("新建引擎实例耗时：{}",(t2-t1));
			
			//如果流程已经被下线，需要先将其激活
			List<ProcessDefinition> definitions = repositoryService
					.createProcessDefinitionQuery().processDefinitionKey(processKey).list();
			if(!CollectionUtils.isEmpty(definitions)){
				for(ProcessDefinition definition:definitions){
					if(definition.isSuspended()){
						repositoryService.activateProcessDefinitionById(definition.getId());
					}
				}
			}
			
			//部署流程
			deployment = repositoryService.createDeployment().addString("bpmn1.bpmn20.xml", bpmnText).deploy();
			long t3 = System.currentTimeMillis();
			log.info("部署流程耗时：{}",(t3-t2));
			
			//历史流程有可能正在运行中，不能直接删除流程实例
			//档流程部署成功以后，再删除历史部署流程的引擎实例
			if(processDefinition!=null){
				List<ProcessInstance> processInstances = runtimeService
						.createProcessInstanceQuery().processDefinitionId(processDefinition.getId()).list();
				
				if(CollectionUtils.isEmpty(processInstances)){
					try {
						deleteInstances(hisInstances);
						long t4 = System.currentTimeMillis();
						log.info("删除历史引擎实例耗时：{}",(t4-t3));
					} catch (Exception e) {
						log.error("删除历史引擎失败，尝试再次删除",e);
						deleteInstances(hisInstances);
					}
				}
				else{
					//保存到待删除列表
					saveToDeleteList(processDefinition.getId(),hisInstances);
				}
			}
			
			return ExtJSResponse.successResWithData(deployment.getId());
		} catch (Exception e) {
			//删除已创建成功的引擎实例
			if(!StringUtils.isEmpty(instances)){
				try {
					deleteInstances(instances);
				} catch (Exception e1) {
				}
			}
			//删除Activiti已部署流程
			if(deployment!=null && deployment.getId()!=null){
				repositoryService.deleteDeployment(deployment.getId());
			}
			
			if(e instanceof WorkFlowException){
				return ExtJSResponse.errorRes(e.getLocalizedMessage());
			}
			log.error("部署失败：",e);
			return ExtJSResponse.errorRes("部署失败");
		}
	}

	private void saveToDeleteList(String id,
			List<EngineInstanceEntity> hisInstances) {
		if(CollectionUtils.isEmpty(hisInstances)){
			return;
		}
		//TODO	保存未实现
	}

	private String applyNewInstance(String bpmnText,
			List<EngineInstanceEntity> instances) throws Exception {
		Document doc = XMLUtils.getDocument(bpmnText);
		Element root = doc.getRootElement();
		Element process = XMLUtils.getElementByName(root, "process");
		List<Element> serviceTasks = XMLUtils.getElementsByName(process, "serviceTask");
		
		for(Element serviceTask:serviceTasks){
			Element extensionElements = XMLUtils.getElementByName(serviceTask, "extensionElements");
			
			String instanceId = newInstance(serviceTask,instances);
			
			Element instance = extensionElements.addElement("activiti:field");
			instance.addAttribute("name", "instance_id");
			Element value = instance.addElement("activiti:string");
			value.addCDATA(instanceId);
			
		}
		
		return doc.asXML();
	}

	/**
	 * 创建引擎实例.<br>
	 * @param serviceTask
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	private String newInstance(Element serviceTask,
			List<EngineInstanceEntity> instances) throws Exception{
		String className = XMLUtils.getAtrributeByName(serviceTask, "class");
		if(className==null){
			throw new WorkFlowException("引擎节点class未配置");
		}
		// TODO Auto-generated method stub
		return null;
	}

	private List<EngineInstanceEntity> getHisEngineInstances(
			ProcessDefinition processDefinition) throws WorkFlowException, IOException {
		//历史未部署过
		if(processDefinition==null){
			return null;
		}
		
		InputStream in = repositoryService.getResourceAsStream(
				processDefinition.getDeploymentId(), processDefinition.getResourceName());
		
		List<EngineInstanceEntity> instances = getEngineInstances(in);
		
		return instances;
	}

	private List<EngineInstanceEntity> getEngineInstances(InputStream in) throws WorkFlowException, IOException {
		Element root = null;
		try {
			root = XMLUtils.getRootElement(in);
		} catch (Exception e) {
			log.error("解析历史部署流程XML出错");
			throw new WorkFlowException("解析历史部署流程XML出错");
		} finally{
			in.close();
		}
		
		List<EngineInstanceEntity> instanceEntitys = new ArrayList<EngineInstanceEntity>();
		
		Element process = XMLUtils.getElementByName(root, "process");
		List<Element> serviceTasks = XMLUtils.getElementsByName(process, "serviceTask");
		for(Element serviceTask:serviceTasks){
			Element extensionElements = XMLUtils.getElementByName(serviceTask, "extensionElements");
			List<Element> fields = XMLUtils.getElementsByName(extensionElements, "field");
			
			EngineInstanceEntity instance = new EngineInstanceEntity();
			
			String instanceId = getValueByKey(fields, "instance_id");
			
			List<String> urls = null;
			instance.setInstanceId(instanceId);
			instance.setUrls(urls);
			
			instanceEntitys.add(instance);
		}
		
		return instanceEntitys;
	}
	
	public String getValueByKey(List<Element> fields,String key){
		for(Element field:fields){
			String key1 = XMLUtils.getAtrributeByName(field, "name");
			if(key.equals(key1)){
				String text = XMLUtils.getElementByName(field, "string").getText();
				return text;
			}
		}
		
		return null;
	}

	private void deleteInstances(List<EngineInstanceEntity> instances) throws Exception {
		log.info("删除引擎实例：{}",gson.toJson(instances));
	}

	/**
	 * 获取所有处于激活状态的流程
	 */
	@Override
	public ExtJSResponse listAllFlows() {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		List<ProcessDefinition> processDefinitions = repositoryService
				.createProcessDefinitionQuery().active().latestVersion().list();
		
		if(CollectionUtils.isEmpty(processDefinitions)){
			return ExtJSResponse.successResWithData(results);
		}
		
		for(ProcessDefinition processDefinition:processDefinitions){
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("processKey", processDefinition.getKey());
			result.put("processName", processDefinition.getName());
			result.put("processDefId", processDefinition.getId());
			results.add(result);
		}
		
		return ExtJSResponse.successResWithData(results);
	}

}
