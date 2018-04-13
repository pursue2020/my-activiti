/**
 * 
 */
package com.tantan.workflow.entity;

import java.util.List;

/**
 * 引擎实例
 * @author tantan
 *
 */
public class EngineInstanceEntity {	
	
	private String instanceId;
	private List<String> urls;
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}	

}
