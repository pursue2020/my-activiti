/**
 * 
 */
package com.tantan.workflow.bo;

import javax.servlet.http.HttpServletResponse;

/**
 * 流程业务类
 * @author tantan
 *
 */
public interface FlowBO {
	
	/**
	 * 
	 * 获取流程执行图片.<br>
	 */
	void getFlowImg(String instanceId,HttpServletResponse response);
	

}
