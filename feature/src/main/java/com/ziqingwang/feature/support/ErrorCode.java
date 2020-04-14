package com.ziqingwang.feature.support;

/**
 * @author: Ziven
 * @date: 2018/06/08
 **/
public enum ErrorCode {

	OK(0, "success"),
	ERROR(1, "未知错误"),


	PARAM_INVALID(10000, "参数异常"),
	RECIPE_CODE_INVALID(10001, "食谱事业部编码冲突"),
	RECIPE_TYPE_UNABLE_MODIFY(10002, "食谱无法变更"),
	RECIPE_NOT_EXIST(10003, "食谱不存在"),
	FOOD_NOT_EXIST(10004, "食材不存在"),
	CATE_NOT_EXIST(10005, "品类不存在"),

	ILLEGAL_USER(11000, "非法用户"),
	USER_NO_EXISTS(11001, "用户不存在"),
	PERMIT_NO_EXISTS(11002, "权限不存在"),
	PASSWORD_INVALID(11003, "密码无效"),
	USER_DATA_INVALID(11004, "用户信息格式不正确，请检查账号（英文|数字|_@.），密码（英文|数字|不小于8个字符），邮箱"),
	USER_NAME_EXIST(11005, "账号已经被使用"),
	TOKEN_INVALID(11006, "token无效"),
	TOKEN_TIMEOUT(11007, "token过期"),
	FORBIDDEN(11008, "权限不足"),
	SIGN_NOT_PASS(11009,"sign error"),
	RECIPE_MODEL_NOT_EXIST(11010,"该型号不存在此食谱，请联系相关人员配置"),
	PLATFORM_ALREADY_EXIST(11011,"平台已存在"),
	PLATFORM_NOT_EXIST(11012,"平台不存在"),
	MODELNUMBER_PLATFORM_EXIST(11013,"该设备已经配置平台，请删除重试"),
	COMMAND_VERIFY_ERROR(11014,"<pre>指令格式不正确，请检查指令内容。</pre>" +
			"<pre>单段指令示例(字节间逗号分隔)：aa,24,ea,00,00,......,99</pre>" +
			"<pre>多段指令示例(程序间分号分隔)：aa,24,ea,00,00,......,99;aa,24,ea,00,00,......,99</pre>"),
	APPLIANCE_MODEL_NOT_EXIST(11015,"设备型号不存在"),

	CREATE_FILE_ERROR(12001,"文件创建失败"),
	FILE_FORMAT_ERROR(12002,"文件格式错误"),


	DUPLICATE_DATA(20001,"数据重复"),
	MISSING_DATA(20002,"数据不全"),
	PARENT_ERROR(20003,"操作错误。");


	private int code;
	private String msg;

	ErrorCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
