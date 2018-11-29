package com.bewg.mqtt;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * topic 管理类
 */
@Slf4j
public class TopicManager {

    /**
     * 已经订阅的topic集合
     */
    private static Map<String,String> SUBSCRIB_TOPIC_MAP = new LinkedHashMap();

    /**
     * 发送过的topic集合
     */
    private static Map<String,String> PUSH_TOPIC_MAP = new LinkedHashMap();

}
