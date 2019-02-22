package com.stylefeng.guns.rest.modular.vo;


/**
 *  此类负责登陆功能的返回部分
 * @param <M>
 */
public class ResponseVO<M> {

    // 返回的状态码， 0: 成功， 1：失败，999：返回异常
    private int status;
    // 返回的信息：randomKey 和 token
    private String msg;
    // 返回数据实体
    private M data;


    private ResponseVO() {}


    /**
     * 成功
     * @param m
     * @param <M>
     * @return
     */
    public static <M> ResponseVO success(M m) {


        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setData(m);

        return responseVO;
    }


    /**
     * 业务异常
     * @param msg
     * @param <M>
     * @return
     */
    public static <M> ResponseVO serviceFail(String msg) {


        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setMsg(msg);

        return responseVO;
    }


    /**
     * 系统异常
     * @param msg
     * @param <M>
     * @return
     */
    public static <M> ResponseVO appFail(String msg) {


        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(999);
        responseVO.setMsg(msg);

        return responseVO;
    }





    /*
        getter and setter
     */

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public M getData() {
        return data;
    }

    public void setData(M data) {
        this.data = data;
    }
}
