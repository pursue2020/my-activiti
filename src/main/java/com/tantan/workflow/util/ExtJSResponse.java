/**
 * 
 */
package com.tantan.workflow.util;

import java.util.HashMap;

/**
 * used to integrates with ExtJS 4.x data stores.<br>
 * In ExtJS, the data store use following format to verify the result:
 * 
 * <pre>
 *  "{"success":false,"data":"","message":"VERBOSE ERROR"}"
 * </pre>
 * @author tantan
 *
 */
public class ExtJSResponse extends HashMap<String, Object> {

	private static final long serialVersionUID = -2791356338016228077L;

	public ExtJSResponse() {
		super();
	}

	public ExtJSResponse(final boolean success) {
		super();
		put("success", success);
	}

	public static ExtJSResponse success() {
		return new ExtJSResponse(true);
	}

	public static ExtJSResponse successRes4Find(final Object data, final Integer total) {
		final ExtJSResponse response = new ExtJSResponse(true);
		response.setData(data);
		response.put("total", total);
		return response;
	}

	public static ExtJSResponse successResWithData(final Object data) {
		final ExtJSResponse response = new ExtJSResponse(true);
		response.setData(data);
		return response;
	}

	public static ExtJSResponse errorRes(final String error) {
		final ExtJSResponse response = new ExtJSResponse(false);
		response.setErrorMsg(error);
		return response;
	}

	public void setData(final Object data) {
		put("data", data);
	}

	public Object getData() {
		return get("data");
	}

	public boolean isSuccess() {
		return (Boolean) get("success");
	}

	public void setSuccess(final boolean success) {
		put("success", success);
	}

	public void setErrorMsg(final String errorMsg) {
		put("error", errorMsg);
	}

	public String getErrorMsg() {
		return (String) get("error");
	}
	
	public void setErrorCode(String errCode){
		put("error_code", errCode);
	}
	
	public String getErrorCode(){
		return (String) get("error_code");
	}

}
