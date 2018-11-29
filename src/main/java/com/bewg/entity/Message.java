package com.bewg.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Message 协议体
 */
@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     * 格式为：发布者mac地址_时间戳
     */
    private String msg_id;

    /**
     * 应答的目标 消息id
     * 格式为：发布者mac地址_时间戳
     */
    private String callback_id;

    /**
     * msg类型
     * 1 注册消息
     * 2 心跳消息
     * 3功能消息
     */
    private Integer msg_type;

    /**
     * 发布者的mac地址
     */
    private String source_mac;

    /**
     * 发布者类服务类型，可参考《14_mqtt通讯协议》的 2.1款说明
     */
    private String source_type;

    /**
     * 消息生成时的时间戳
     */
    private Long create_time;

    /**
     * 节点授权码（不知道还用不用，原来设计的初衷是留给加密解密用的，现在还没给加密解密的方式）
     */
    private String license;

    /**
     * 消息体 json格式
     */
    private MesBody body;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getCallback_id() {
        return callback_id;
    }

    public void setCallback_id(String callback_id) {
        this.callback_id = callback_id;
    }

    public Integer getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Integer msg_type) {
        this.msg_type = msg_type;
    }

    public String getSource_mac() {
        return source_mac;
    }

    public void setSource_mac(String source_mac) {
        this.source_mac = source_mac;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public MesBody getBody() {
        return body;
    }

    public void setBody(MesBody body) {
        this.body = body;
    }
}
