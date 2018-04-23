/**
 * 
 */
package com.tantan.workflow.listener;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 全局流程事件处理类. <br>
 * 
 * @author tantan
 *
 */
@Slf4j
@Component
public class ProcessCompleteListener implements ActivitiEventListener {
	
    static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy/MM/dd hh:mm:ss").create();
	
	
	/**
	 * 2018-04-23 10:23:47.052  INFO 5700 : 一个节点开始执行事件:ACTIVITY_STARTED
	 * 2018-04-23 10:23:47.061  INFO 5700 : 一个节点成功结束事件:ACTIVITY_COMPLETED
	 * 2018-04-23 10:23:47.063  INFO 5700 : 一个节点开始执行事件:ACTIVITY_STARTED
	 * 2018-04-23 10:23:47.082  INFO 5700 : 任务被分配给了一个人员事件:TASK_ASSIGNED
	 * 2018-04-23 10:23:47.082  INFO 5700 : 创建了新任务事件:TASK_CREATED
	 * 2018-04-23 10:26:21.118  INFO 7576 : 任务被完成了事件:TASK_COMPLETED
	 * 2018-04-23 10:26:21.159  INFO 7576 : 一个节点成功结束事件:ACTIVITY_COMPLETED
	 * 2018-04-23 10:26:21.265  INFO 7576 : 一个节点开始执行事件:ACTIVITY_STARTED
	 * 2018-04-23 10:26:21.278  INFO 7576 : 任务被分配给了一个人员事件:TASK_ASSIGNED
	 * 2018-04-23 10:26:21.279  INFO 7576 : 创建了新任务事件:TASK_CREATED
	 */
	@Override
	public void onEvent(ActivitiEvent event) {
		if(event.getType()==ActivitiEventType.PROCESS_STARTED){
			log.info("流程启动事件:"+event.getType());
		}else if(event.getType()==ActivitiEventType.TASK_CREATED){
			log.info("创建了新任务事件:"+event.getType());
		}else if(event.getType()==ActivitiEventType.TASK_ASSIGNED){
			log.info("任务被分配给了一个人员事件:"+event.getType());
		}else if(event.getType()==ActivitiEventType.TASK_COMPLETED){
			log.info("任务被完成了事件:"+event.getType());
		}else if(event.getType()==ActivitiEventType.ACTIVITY_STARTED){
			log.info("一个节点开始执行事件:"+event.getType());
			ProcessInstance processInstance = Context.getExecutionContext().getProcessInstance();
			ProcessDefinitionEntity processDefinition = Context.getExecutionContext().getProcessDefinition();
			Map<String, Object>  variables=Context.getExecutionContext().getExecution().getVariables();
			
			log.info("流程ActivitiID{}",processInstance.getActivityId());
			log.info("流程名称{}",processDefinition.getName());
			log.info("流程输入参数{}",gson.toJson(variables));
		}else if(event.getType()==ActivitiEventType.ACTIVITY_COMPLETED){
			log.info("一个节点成功结束事件:"+event.getType());
		}
		
	
		//TODO 流程状态发送消息队列进行落地处理
	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

}
