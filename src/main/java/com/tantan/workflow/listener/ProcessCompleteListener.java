/**
 * 
 */
package com.tantan.workflow.listener;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.tantan.workflow.constants.Constants;

/**
 * 全局流程执行结束事件. <br>
 * 
 * @author tantan
 *
 */
@Slf4j
public class ProcessCompleteListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent event) {
		log.info("全局流程执行结束事件");
		ProcessInstance processInstance = Context.getExecutionContext()
				.getProcessInstance();
		ProcessDefinitionEntity processDefinition = Context
				.getExecutionContext().getProcessDefinition();

		int status = Constants.ProcessInstanceStatus.EXCEPTION_END;
		List<ActivityImpl> activities = processDefinition.getActivities();
		if (isEndpoint(processInstance.getActivityId(), activities)) {
			status = Constants.ProcessInstanceStatus.END;
		}

		log.info("流程状态:"+status);
		
		//TODO 流程状态发送消息队列进行落地处理
	}

	private boolean isEndpoint(String activityId, List<ActivityImpl> activities) {
		if(StringUtils.isEmpty(activityId) || CollectionUtils.isEmpty(activities)){
			return false;
		}
		
		ActivityImpl activity = getActvityById(activityId,activities);
		if("endEvent".equals(activity.getProperty("type"))){
			return true;
		}
		
		return false;
	}

	private ActivityImpl getActvityById(String activityId,
			List<ActivityImpl> activities) {
		for(ActivityImpl activity:activities){
			if(activity.getId().equals(activityId)){
				return activity;
			}
		}
		return null;
	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

}
