package com.stylefeng.guns.rest.modular.vo;


import lombok.Data;

/**
 *  此类负责登陆、注册功能的返回部分
 * @param <M>
 */
@Data
public class ResponseVO<M> {

    // 返回的状态码， 0: 成功， 1：失败，999：返回异常
    private int status;
    // 返回的信息：randomKey 和 token
    private String msg;
    // 返回数据实体
    private M data;

    // 图片前缀
    private String imgPre;

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
     * 带图片前缀的 成功 response
     * @param imgPre
     * @param <M>
     * @return
     */
    public static <M> ResponseVO success(String imgPre, M m) {

        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setData(m);
        responseVO.setImgPre(imgPre);

        return responseVO;
    }

    /**
     *  注册成功时，调用的方法
     * @param msg
     * @param <M>
     * @return
     */
    public static <M> ResponseVO success(String msg) {

        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setMsg(msg);

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
        responseVO.setStatus(1);
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
}
