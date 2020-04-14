package com.ziqingwang.feature.support;

import lombok.Data;

/**
 * @author: Ziven
 * @date: 2018/06/13
 **/
@Data
public class Response {

	/**
	 * 错误代码，当错误码为”0”时，代表业务执行成功；非零值表示业务执行发生错误
	 */
	private int code;
	/**
	 * 返回结果
	 */
	private Object data;
	/**
	 * 如果错误，返回错误信息，否则不返回此参数
	 */
	private String msg;

	public static Response ok(Object result) {
		Response response = new Response();
		response.setCode(ErrorCode.OK.getCode());
		response.setData(result);
		return response;
	}

	public static Response ok() {
		Response response = new Response();
		response.setCode(ErrorCode.OK.getCode());
		return response;
	}

	public static Response error(String msg) {
		return error(ErrorCode.ERROR.getCode(), msg);
	}

	public static Response error() {
		return error(ErrorCode.ERROR.getCode(), ErrorCode.ERROR.getMsg());
	}

	public static Response error(int code, String msg) {
		Response response = new Response();
		response.setCode(code);
		response.setMsg(msg);
		return response;
	}
}
