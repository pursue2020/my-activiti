/**
 * 
 */
package com.tantan.workflow.listener;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

/**
 * 全局流程执行结束事件. <br>
 * @author tantan
 *
 */
@Slf4j
public class ProcessCompleteListener implements ActivitiEventListener {

	@Override
	public void onEvent(ActivitiEvent event) {
			log.info("全局流程执行结束事件");
	}
	
	@Override
	public boolean isFailOnException() {
		return true;
	}



}
