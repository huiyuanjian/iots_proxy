package com.bewg.Runnel;

import com.bewg.entity.Constant;
import com.bewg.entity.Message;
import com.bewg.fileIO.FileUtils;
import com.bewg.mqtt.MqttUtils;
import com.bewg.utils.CommontUtils;
import com.bewg.utils.InetAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.MessageFormat;

/**
 * @author zcy
 * @date 2018/11/216:12
 * 订阅topic
 */
@Slf4j
public class MqttTopicRunnel extends Thread {

    private MqttUtils mqttUtils = new MqttUtils();

    /*
     * @Author zcy
     * @Description //c初始订阅 所有主题 若不存在 就创建
     * @Date 9:23 2018/11/5
     * @Param
     * @return
     **/
    @Override
    public void run() {
        //
        //订阅主题格式统一为：fromServerType/toServerType/toMacAddress/functionType
        // fromServerType ：消息发布者的服务类型，参考2.1。
        // toServerType ：消息订阅者的服务类型，参考2.1。
        // toMacAddress：消息订阅者的mac地址，如果存在多个则选用其中一个值较大者，地址内容不应携带符号，且字母为大写，例如：5c:96:9d:71:f5:a3应取5C969D71F5A3。
        // functionType ：除注册和心跳外的功能类型，参考2.2。
        try {
            subscribe();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始订阅 所有主题 出现异常！",e.getMessage());
        }
    }

    private void subscribe() throws Exception {
        FileUtils fileUtils= new FileUtils();
        //判断Mac地址有没加载
        if(Constant.local_mac==null){
            //加载配置文件
            String object = fileUtils.readDataLi(Constant.MACFileName,Constant.MACConfgPath);
            //判断是否null
            if(object!=null){
                Message message= CommontUtils.byte2Object(object.toString(),new Message());
                Constant.local_mac=message.getSource_mac();
            }
        }
        MqttMessage msg = new MqttMessage();
        msg.setQos(1);
        msg.setPayload("init subscribe".getBytes());
        String macaddr= (Constant.local_mac==null?InetAddrUtils.getMACAddress():Constant.local_mac);
        //接收全部
        String allCconfigTopic= "COLLECT/PROXY/"+ macaddr+"/CONFIG";
        mqttUtils.subscribTopic(allCconfigTopic);

        //接收配置
        String partConfigData=MessageFormat.format(Constant.partConfigData,macaddr);
        mqttUtils.subscribTopic(partConfigData);

        //控制 接收
        String pullCtr=MessageFormat.format(Constant.pullCtr,macaddr);
        mqttUtils.subscribTopic(pullCtr);

        //消息回复
        String answer= MessageFormat.format(Constant.answerTopic_pull,macaddr);
        mqttUtils.subscribTopic(answer);
    }
}
