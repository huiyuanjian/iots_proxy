package com.bewg.entity.regist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册信息管理类
 */
public class RegistManager {

    /**
     * 采集代理注册的消息
     * String 是mac地址
     */
    public static Map<String,RegistInfo> REGIST_MAP = new HashMap<>();

    /**
     * 本服务有权管理的采集代理
     * String 是mac地址
     */
    public static List<String> MACADDRESS_LIST = new ArrayList<>();
}
