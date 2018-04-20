/**
 * 
 */
package com.tantan.workflow.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tantan.workflow.bo.FlowBO;
import com.tantan.workflow.config.WorkFlowConfigurable;

/**
 * @author tantan
 *
 */
@RestController
@RequestMapping("/flow")
public class FlowService implements WorkFlowConfigurable {
	
	@Autowired
	private FlowBO flowBO;
	
    /**
     * 
     * 查询流程图.<br>
     *
     * @author tantan
     * @param instanceId 流程实例ID
     * @return
     * @throws IOException 
     */
    @RequestMapping(value = "/getFlowImg",method = RequestMethod.GET,produces="image/png")
    public void getFlowImg(String instanceId,HttpServletResponse response){
    	flowBO.getFlowImg(instanceId, response);
    }
	

}
