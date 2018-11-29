package com.bewg.mqtt;

import com.bewg.Runnel.MqttTopicRunnel;
import com.bewg.entity.Constant;
import com.bewg.entity.MesBody;
import com.bewg.entity.Message;
import com.bewg.entity.ctrl.CtrlInfo;
import com.bewg.ioServer.thread.IoSConfigThread;
import com.bewg.ioServer.thread.IoServerEqvThread;
import com.bewg.mqtt.thread.CtrlMqttThread;
import com.bewg.utils.CommontUtils;
import com.bewg.utils.InetAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.util.StringUtils;

/**
 * 发布消息的回调类
 *
 * 必须实现MqttCallback的接口并实现对应的相关接口方法
 *      ◦CallBack 类将实现 MqttCallBack。每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。在回调中，将它用来标识已经启动了该回调的哪个实例。
 *  ◦必须在回调类中实现三个方法：
 *
 *  public void messageArrived(MqttTopic topic, MqttMessage message)
 *  接收已经预订的发布。
 *
 *  public void connectionLost(Throwable cause)
 *  在断开连接时调用。
 *
 *  public void deliveryComplete(MqttDeliveryToken token))
 *      接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。
 *  ◦由 MqttClient.connect 激活此回调。
 *
 */
@Slf4j
public class PushCallback implements MqttCallbackExtended {

    Object lock= new Object();

    /**
     * 断线重连
     * @param cause
     */
    public void connectionLost(Throwable cause) {
        log.error("连接断开，正在重试连接。。。");
//        MqttSingle single = MqttSingle.getInstance();
//        single.reLink();
//        log.error("已经重新连接。。。");
    }

    /**
     * 订阅消息
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        // subscribe后得到的消息会执行到这里面
        log.info("接收消息主题:{}",topic);
        log.info("接收消息Qos:{}",mqttMessage.getQos());
        log.info("接收消息内容:{}",CommontUtils.Object2Str(CommontUtils.byte2Object(new String(mqttMessage.getPayload()),new Message())));
        //获取要获取所有设备变量的指令
        //接收数据 配置 信息
        synchronized(lock){
            if (topic.indexOf("/CONFIG")!=-1){
                String  value= new String(mqttMessage.getPayload());
                log.info("订阅到的消息====="+value);
                if(value.equals("success")|| StringUtils.isEmpty(value)){
                    return;
                }
                Message message = CommontUtils.byte2Object(value, new Message());
                String macAddr= message.getSource_mac();
                //放入内存
                Constant.messageModel = message;
                if (message.getBody()!=null){//解析
                    MesBody body= message.getBody();
                    switch(body.getSub_type()){
                        case 1:
                            //解析 body中 type字段值 是1（配置） 还是 3（采集数据）
                            new Thread(new IoServerEqvThread(message,topic),"接收设备变量-Thread").start();
                            break;
                        case 2:
                            //接收数据  信息
                            new Thread(new IoSConfigThread(mqttMessage,topic),"接收配置-Thread").start();
                            break;
                    }
                }
                //回应服务
            }else if(topic.indexOf("/CTRL")!=-1){
                //启动可控制线程
                new Thread(new CtrlMqttThread<CtrlInfo>(mqttMessage,topic),"接收控制命令-Thread").start();
            } else if (topic.indexOf("/ANSWER")!=-1){//应答消息
                // 判断 消息内容 CONFIG：配置，CTRL：控制
                String message = CommontUtils.byte2Object(new String(mqttMessage.getPayload()),new Message()).getBody().getContext().get(0);
                if (message.equals("CONFIG")){
                    IoServerEqvThread.accessIs=false;
                }else if (message.equals("CTRL")){
                    CtrlMqttThread.accessIs=false;
                }
            }
        }
    }

    /**
     * 发布消息的回调
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete( IMqttDeliveryToken iMqttDeliveryToken ) {
        // publish后会执行到这里
        log.info("deliveryComplete: {}",iMqttDeliveryToken.isComplete());
    }

    @Override
    public void connectComplete(boolean b, String s) {
        // TODO 先判断系统是否已经启动
        //订阅 topic
        MqttTopicRunnel mqttTopicRunnel= new MqttTopicRunnel();
        mqttTopicRunnel.start();
    }
}
