package com.weds.devmanages.base;


import com.weds.devmanages.config.log.JsonResult;

public class BaseClass {

    public final static Integer FAILED_FLAG = 601;
    public final static Integer SUCCESSED_FLAG = 600;
    public final static String SUCCESSED_MSG = "成功";
    public final static String FAILED_MSG = "失败";

    public BaseClass() {
    }


    protected <T> JsonResult<T> message(Integer code, String msg, T data) {
        JsonResult<T> jsonResult = new JsonResult();
        jsonResult.setCode(code);
        jsonResult.setMsg(msg);
        jsonResult.setData(data);
        return jsonResult;
    }

    protected <T> JsonResult<T> succMsg(String msg, T data) {
        return this.message(SUCCESSED_FLAG, msg, data);
    }

    protected <T> JsonResult<T> succMsg(String msg) {
        return this.succMsg(msg, null);
    }

    protected <T> JsonResult<T> succMsgData(T data) {
        return this.succMsg(SUCCESSED_MSG, data);
    }

    protected <T> JsonResult<T> succMsg() {
        return this.succMsg(SUCCESSED_MSG);
    }

    protected <T> JsonResult<T> failMsg(Integer code, String message, T data) {
        return this.message(code, message, data);
    }

    protected <T> JsonResult<T> failMsg(String message) {
        return this.failMsg(FAILED_FLAG,message, null);
    }

    protected <T> JsonResult<T> failMsg() {
        return this.failMsg(FAILED_MSG);
    }

    protected <T> JsonResult<T> failMsgData(T data) {
        return this.message(FAILED_FLAG, FAILED_MSG, data);
    }

    protected <T> JsonResult<T> failParamMsg() {
        return this.failMsg("参数错误");
    }

    protected String objToStr(Object res) {
        return res.toString();
    }

}
