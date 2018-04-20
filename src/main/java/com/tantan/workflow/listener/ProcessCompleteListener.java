/**
 * 
 */
package com.tantan.workflow.listener;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 全局流程执行结束事件. <br>
 * 
 * @author tantan
 *
 */
@Slf4j
@Component
public class ProcessCompleteListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent event) {
		log.info("全局流程事件"+event.getType());
	
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
