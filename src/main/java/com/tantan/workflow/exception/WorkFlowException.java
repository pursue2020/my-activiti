/**
 * 
 */
package com.tantan.workflow.exception;

/**
 * @author tantan
 *
 */
public class WorkFlowException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkFlowException() {
	}

	public WorkFlowException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public WorkFlowException(String paramString) {
		super(paramString);
	}

	public WorkFlowException(Throwable paramThrowable) {
		super(paramThrowable);
	}

}
