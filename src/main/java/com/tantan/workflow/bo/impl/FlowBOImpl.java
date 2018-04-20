/**
 * 
 */
package com.tantan.workflow.bo.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tantan.workflow.bo.FlowBO;
import com.tantan.workflow.config.WorkFlowConfigurable;
import com.tantan.workflow.exception.WorkFlowException;

/**
 * @author tantan
 *
 */
@Slf4j
@Component
public class FlowBOImpl implements FlowBO,WorkFlowConfigurable {
	
    static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd hh:mm:ss").create();	
    
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessEngine processEngine;

    
    /**
     * 
     */
	@Override
	public void getFlowImg(String instanceId, HttpServletResponse response) {
		OutputStream out = null;
		InputStream in = null;
    	try {
    		ProcessInstance processInstance = runtimeService.
        			createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
    		//去history查找
			HistoricProcessInstance hisInstance = historyService.
					createHistoricProcessInstanceQuery().processInstanceId(instanceId).finished().singleResult();
    		String processKey = null;
    		if(processInstance!=null){
    			processKey = processInstance.getProcessDefinitionId();
    		}else{
    			if(hisInstance!=null){
    				processKey = hisInstance.getProcessDefinitionId();
    			}else{
    				throw new WorkFlowException("流程："+instanceId+"不存在");
    			}
    		}
    		
        	BpmnModel bpmnModel = repositoryService.getBpmnModel(processKey);
        	
        	List<String> activitityIds = new ArrayList<String>();
        	if(processInstance!=null){
        		activitityIds = runtimeService.getActiveActivityIds(instanceId);
        	}
        	
        	List<String> executedFlows = getExecutedFlows(instanceId,processKey);
        	log.debug("ActivitiIds:{}",activitityIds);
        	log.debug("executedFlows:{}",executedFlows);
        	
        	log.info("========决策流图片获取字体：{}========",processEngine.
        			getProcessEngineConfiguration().getActivityFontName());
        	log.info("========决策流图片获取标签字体：{}========",processEngine.
        			getProcessEngineConfiguration().getLabelFontName());
        	
        	//解决中文乱码问题
        	processEngine.getProcessEngineConfiguration().setActivityFontName("宋体");
        	processEngine.getProcessEngineConfiguration().setLabelFontName("宋体");
        	
        	DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        	in = generator.generateDiagram(bpmnModel, "png", activitityIds,executedFlows, 
        			processEngine.getProcessEngineConfiguration().getActivityFontName(), 
        			processEngine.getProcessEngineConfiguration().getLabelFontName(), 
        			null, 1.0);
        	
        	out = response.getOutputStream();
        	IOUtils.copy(in, out);
        	out.flush();
		} catch (Exception e) {
			log.error("获取流程图片失败",e);
		}finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	
	
	/**
	 * 查询已执行的sequenceFlow.<br>
	 * @param instanceId
	 * @param processDefinitionId
	 * @return
	 */
    private List<String> getExecutedFlows(String instanceId,String processDefinitionId) {
    	List<String> flowIds = new ArrayList<String>();
    	
    	//查询历史已执行的节点
    	List<HistoricActivityInstance> list = historyService 
                .createHistoricActivityInstanceQuery()  .processInstanceId(instanceId).list();
    	if(CollectionUtils.isEmpty(list)){
    		return flowIds;
    	}
		Set<String> executedActivityIds = getExecutedActivityIds(list);
    	
    	//查询流程定义
    	ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
    	if(definition==null){
    		return flowIds;
    	}
    	
    	//所有节点
    	List<ActivityImpl> activities = definition.getActivities();
    	
    	Set<String> executedFlows = new HashSet<String>();
    	for(ActivityImpl activity:activities){
    		if(!executedActivityIds.contains(activity.getId())){
    			continue;
    		}
    		
    		List<PvmTransition> transitions = activity.getOutgoingTransitions();
    		if(!CollectionUtils.isEmpty(transitions)){
    			for(PvmTransition transition:transitions){
    				if(executedActivityIds.contains(transition.getDestination().getId())){
    					executedFlows.add(transition.getId());
    				}
    			}
    		}
    	}
    	
    	flowIds.addAll(executedFlows);
		return flowIds;
	}
    
    /**
     * 获取已执行的Activiti id.<br>
     * @param list
     * @return
     */
	private Set<String> getExecutedActivityIds(List<HistoricActivityInstance> list) {
		Set<String> ids = new HashSet<String>();
		for(HistoricActivityInstance instance:list){
			ids.add(instance.getActivityId());
		}
		return ids;
	}

}
