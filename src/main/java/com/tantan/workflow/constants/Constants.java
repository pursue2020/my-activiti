/**
 * 
 */
package com.tantan.workflow.constants;

/**
 * @author tantan
 *
 */
public interface Constants {
	
	interface ProcessInstanceStatus{
		public static final int RUNNING = 1;								//运行中
		public static final int EXCEPTION = 3;								//异常
		public static final int EXCEPTION_END = 2;							//异常结束(没有结束节点的结束)
		public static final int END = 9;									//正常结束
	}
	
	interface ProcessAuditStatus{
		public static final String IS_AGREE = "批准";							//异常结束(没有结束节点的结束)
		public static final String IS_NOT_AGREE = "驳回";									//正常结束
	}

}
