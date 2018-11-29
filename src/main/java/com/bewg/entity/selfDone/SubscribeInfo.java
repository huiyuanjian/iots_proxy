package com.bewg.entity.selfDone;

import lombok.Data;

/**
 * @author zcy
 * @date 2018/11/79:54
 * 订阅变量信息 自己用
 */
@Data
public class SubscribeInfo {

    /**
     * 域名
     */
    private String domain;

    /**
     * groupId 组标识
     */
    private String groupId;

    /**
     * 服务id
     */
    private String serviceId;

    /**
     * 服务名称
     */
    private String serviceName;


    /**
     * 变量id --数据库id
     */
    private Integer id;

    /**
     * 变量id varId
     */
    private Integer varId;

    /**
     * 变量名称
     */
    private String name;
    /**
     * 关联设备ID
     */
    private Integer device_id;

    /**
     * 关联设备名
     */
    private String device_name;

    /**
     * 变量说明
     */
    private String info;

    /**
     * 变量错误或者超时是否传输NULL
     */
    private Integer error_as_null;

    /**
     * 变量超时时间（秒）
     */
    private Integer time_out;

    /**
     * 打包类型：0:间隔时间打包；1：按分钟；2：每15分钟；3：每小时；4每天
     */
    private Integer pack_type;

    /**
     * 默认打包间隔时间（秒）
     */
    private Integer time_span;


    @Override
    public String toString() {
        return "VarInfo [id=" + id + ", name=" + name + ", device_id=" + device_id + ", info=" + info
                + ", error_as_null=" + error_as_null + ", time_out=" + time_out + ", pack_type=" + pack_type
                + ", time_span=" + time_span + "]";
    }


}
