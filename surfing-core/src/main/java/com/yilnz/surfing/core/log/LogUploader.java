package com.yilnz.surfing.core.log;

public interface LogUploader {
	 String TYPE_PROCESS_OK = "process_ok";
	 String TYPE_PROCESS_OK_EXCEPTION = "process_ok_exception";
	 String TYPE_PROCESS_ERROR = "process_error";
	 String TYPE_PROCESS_ERROR_EXCEPTION = "process_error_exception";
	 String TYPE_REQUEST_EXCEPTION = "request_exception";
	 void uploadLog(String log, Object data, String type);

	/**
	 * 非200和异常情况都认为是错误的日志
	 * @param type
	 * @return
	 */
	 default boolean isErrorLog(String type){
	 	return type.equals(TYPE_PROCESS_ERROR) || type.contains("exception");
	 }
}
