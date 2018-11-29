package com.bewg.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.util.HashMap;
import java.util.Map;

/**
 * mqtt 工具类
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Slf4j
public class MqttUtils {

    public static Map<String,MqttSubscribeThread> SUBSCRIBE_MAP = new HashMap <>();

    private static MqttClient client = MqttSingle.getInstance().getMqttClient();

    /*
     * @Author zcy
     * @Description 判断是否连接成功 mqtt
     * @Date 17:25 2018/11/7
     * @Param
     * @return
     **/

    public boolean isConnect(){
        if (client==null)
            return false;
        return true;
    }

    /**
     * 发布消息 MqttMessage
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public boolean publish(String topic, MqttMessage msg){
        MqttTopic mqttTopic = client.getTopic(topic);
        try {
            mqttTopic.publish(msg);
            log.info("消息发送成功！ topic=={}",topic);
            log.info("消息内容是：{}",new String(msg.getPayload()));
        } catch (MqttException e) {
            try {
                mqttTopic.publish(msg);
                log.info("消息发送成功！");
                log.info("消息内容是：{}",msg.getPayload());
            } catch (MqttException e1) {
                log.error("消息发送失败！");
                log.error("异常信息为：{}",e1.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * 订阅消息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public void subscrib(String topic){
        if (SUBSCRIBE_MAP.get(topic) == null){
            MqttSubscribeThread thread = new MqttSubscribeThread(topic);
            SUBSCRIBE_MAP.put(topic,thread);
        }
    }

    /**
     * 订阅消息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public void subscribTopic(String topic){
        //订阅消息
        int[] Qos  = {1};
        String[] array_topic = {topic};
        try {
            client.subscribe(array_topic, Qos);
        } catch (MqttException e) {
            log.error("订阅消息异常!");
            log.error("异常的topic有：{}",topic.toString());
            log.error("异常信息为：{}",e.getMessage());
        }
    }

    /**
     * 订阅线程
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private class MqttSubscribeThread implements Runnable {

        MqttUtils mqttUtils = new MqttUtils();

        private String topic;

        public MqttSubscribeThread(String topic){
            this.topic = topic;
            new Thread(this).start();
        }

        @Override
        public void run() {
            while (true){
                mqttUtils.subscribTopic(topic);
            }
        }
    }
}
