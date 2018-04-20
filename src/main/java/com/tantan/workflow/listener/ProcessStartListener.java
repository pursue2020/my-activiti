/**
 * 
 */
package com.tantan.workflow.listener;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 流程启动监听器
 * @author tantan
 *
 */
@Slf4j	
public class ProcessStartListener implements ExecutionListener {
	
	private static final long serialVersionUID = 1L;
    static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd hh:mm:ss").create();	
    

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		log.info(StringUtils.center("流程启动", 100, "="));
		ProcessDefinitionEntity processDefinitionEntity=Context.getExecutionContext().getProcessDefinition();
		Map<String, Object>  variables=Context.getExecutionContext().getExecution().getVariables();
		String instanceId=Context.getExecutionContext().getExecution().getProcessInstanceId();
        String processKey = processDefinitionEntity.getKey();
        String processName = processDefinitionEntity.getName();
		
		log.info("启动流程参数instanceId:{},processKey：{},processName:{}",instanceId,processKey,processName);
		log.info("variables："+gson.toJson(variables));
		
		//TODO 发送消息队列进行落库
		
	}

}
