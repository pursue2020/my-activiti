/**
 * 
 */
package com.tantan.workflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tantan.workflow.bo.FlowManageBO;
import com.tantan.workflow.config.WorkFlowConfigurable;
import com.tantan.workflow.util.ExtJSResponse;

/**
 * 流程部署和流程调用接口.
 * @author tantan
 *
 */
@RestController
@RequestMapping("/flow")
public class FlowManageService implements WorkFlowConfigurable{
	@Autowired
	private FlowManageBO flowManageBO;
	
	/**
	 * 
	 * 流程部署.<br>
	 * 1.解析传入的xml
	 * 2.根据processKey获取上一次部署流程定义
	 * 3.获取所有的引擎节点，获取新的引擎实例并添加到节点参数
	 * 4.如果流程之前被下线了，需要先将其激活
	 * 5.流程部署
	 * 6.流程部署成功以后再删除原来的引擎实例 
	 * @param bpmnText
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deploy", method = { RequestMethod.POST})
	public ExtJSResponse deploy(@RequestBody String bpmnText) throws Exception{
		return flowManageBO.deploy(bpmnText);
	}
	
	/**
	 * 
	 * 查询所有定义的流程.<br>
	 *
	 * @author tantan
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/listAllFlows",method = { RequestMethod.GET })
	public ExtJSResponse listAllFlows() throws Exception{
		return flowManageBO.listAllFlows();
	}

}
