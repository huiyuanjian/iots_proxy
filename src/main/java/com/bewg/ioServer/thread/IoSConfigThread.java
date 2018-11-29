package com.bewg.ioServer.thread;

import com.bewg.entity.CommonConfig;
import com.bewg.entity.Constant;
import com.bewg.entity.FileConfigStall;
import com.bewg.entity.Message;
import com.bewg.fileIO.FileUtils;
import com.bewg.ioServer.utils.IoSConfigParserUtil;
import com.bewg.mqtt.MqttUtils;
import com.bewg.scheduling.ScheduleTask;
import com.bewg.utils.CommontUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author zcy
 * @date 2018/11/119:05
 * 处理 web要采集的命令
 */
@Slf4j
public class IoSConfigThread implements Runnable {

    @Autowired
    private ScheduleTask scheduleTask= new ScheduleTask();

    private MqttMessage mqttMessage;

    private FileUtils fileUtils= new FileUtils();

    private String topic;
    public IoSConfigThread(MqttMessage mqttMessage,String topic) {
        this.mqttMessage = mqttMessage;
        this.topic=topic;
    }
    public IoSConfigThread() {
    }
    @Override
    public void run() {
        try {
            //解析
            this.parseMqMessages();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("采集的命令有异常！===="+e.getMessage());
        }
    }
    /*
     * @Author zcy
     * @Description //解析发布命令信息
     * @Date 14:27 2018/11/5
     * @Param
     * @return
     **/
    private void parseMqMessages() throws Exception {
        if (mqttMessage!=null){
            String configVal= new String(mqttMessage.getPayload());
            if (configVal.equals("success"))return;
            Message message=(Message) CommontUtils.byte2Object(configVal,new Message());
            //将配置类保存到内存中
            Constant.messageModel=message;
            //要采集的 设备变量
            //ConcurrentHashMap<String,SubscribeInfo> subscribeTags = IoServerListenner.subscribeTags;
            CommonConfig proxy = CommontUtils.byte2Object(message.getBody().getContext().get(0),new CommonConfig());
            if (proxy.getServer_id()==null){//本地配置设置为空
                //将本地覆盖
                saveConfigInfo2File(message);
                IoServerListenner.subscribeTags=null;
                scheduleTask.cancleTask();
                return;
            }
            //启动心跳
            scheduleTask.changeCron(proxy.getSend_palpitate_interval());
            //解析
            IoSConfigParserUtil ioSConfigParserUtil= new IoSConfigParserUtil();
            IoServerListenner.subscribeTags = ioSConfigParserUtil.parseMqMessages( null,message);
            //回应消息发布者
            messageArrivedAnswer(Constant.success,message,this.topic);
            //保存到文件
            saveConfigInfo2File(message);
        }
    }


    /*
     * @Author zcy
     * @Description //响应物联网接口
     * @Date 11:58 2018/11/5
     * @Param
     * @return
     **/
    public void messageArrivedAnswer(String returnStr, Message message,String orgtopic) throws Exception {
        // subscribe后得到的消息会执行到这里面
        MqttMessage mms= new MqttMessage();
        mms.setPayload(returnStr.getBytes());
        mms.setQos(1);
        //topic  fromServerType/toServerType/ fromMacAddress /toMacAddress/functionType
        String answerTopic=MessageFormat.format(Constant.answeTopic,Constant.local_mac);
        MqttUtils mqttUtil= new MqttUtils();
        boolean isok = mqttUtil.publish(answerTopic,mms);
        log.info("配置响应物联网接口 发送 Topic："+answerTopic+"   发送成功了？？----"+isok);
    }


    /*
     * @Author zcy
     * @Description //TODO 保存配置到本地
     * @Date 10:34 2018/11/9
     * @Param
     * @return
     **/
    public void saveConfigInfo2File(Message message) throws IOException {
        FileUtils fileUtils= new FileUtils();
        FileConfigStall fileConfigStall= new FileConfigStall();
        fileConfigStall.setFilePath(Constant.configFilePath);
        fileConfigStall.setFileName(Constant.configFileName);
        fileConfigStall.setValue(CommontUtils.Object2Str(message));
        //保存配置到本地
        boolean isok = fileUtils.stallData(Constant.configFileName,Constant.configFilePath,CommontUtils.Object2Str(fileConfigStall));

    }

    /*
     * @Author zcy
     * @Description //TODO 读取本地配置
     * @Date 11:03 2018/11/22
     * @Param
     * @return
     **/
    public static FileConfigStall loadLocalConfig() throws FileNotFoundException {
        FileUtils fileUtils= new FileUtils();
        String objVal = fileUtils.readDataLi( Constant.configFileName,Constant.configFilePath);
        if (objVal==null){
            log.info("【 监听收取数据 - 本地配置文件为空 】");
            return null;
        }
        FileConfigStall fileConfigStall=CommontUtils.byte2Object(objVal,new FileConfigStall());
        return fileConfigStall;
    }
}
