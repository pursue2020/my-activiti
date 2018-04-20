/**
 * 
 */
package com.tantan.workflow.personal.task;

import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author tantan	
 *
 */
@Slf4j
public class TaskListenerImpl implements TaskListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7173662062696731580L;

	/**
	 * 用来指定任务的办理人
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
        //指定个人任务的办理人，也可以指定组任务的办理人  
        //个人任务：通过类去查询数据库，将下一个任务的办理人查询获取，然后通过setAssignee()的方法指定任务的办理人  
		delegateTask.setAssignee("灭绝师太");
		log.info("指定任务的接收人："+"灭绝师太");
		
	}

}
