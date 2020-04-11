package com.new4net.util;


public class AjaxMsg<T> {
	protected String code;
	protected String msg;
	protected T obj;

    public AjaxMsg() {
    }

    public AjaxMsg(String code, String msg){
		this.code=code;
		this.msg=msg;
	}
	public AjaxMsg(String code, String msg, T e){
		this.code=code;
		this.msg=msg;
		this.obj=e;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public Object getObj() {
		return obj;
	}
	public void setObj(T obj) {
		this.obj = obj;
	}

	
}
