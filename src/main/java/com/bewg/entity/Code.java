package com.bewg.entity;

/**
 * @author 周西栋
 * @ClassName: Code
 * @Description: 返回值编码实体类
 * @date 2018年5月7日
 */
public class Code {

    private String code;//编码

    private String message;//信息

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Code [code=" + code + ", message=" + message + "]";
    }


}
