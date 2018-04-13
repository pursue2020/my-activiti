/**
 * 
 */
package com.tantan.workflow.bo;

import com.tantan.workflow.util.ExtJSResponse;

/**
 * 流程管理业务类 . <br>
 * @author tantan
 *
 */
public interface FlowManageBO {
	
	/**
	 * 
	 * 部署流程.<br>
	 *
	 * @author tantan
	 * @param bpmnText
	 * @return
	 */
	ExtJSResponse deploy(String bpmnText);
	
	/**
	 * 
	 * 查询所有定义的流程.<br>
	 *
	 * @author tantan
	 * @return
	 */
	ExtJSResponse listAllFlows();

}
