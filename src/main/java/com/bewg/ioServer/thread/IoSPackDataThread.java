package com.bewg.ioServer.thread;

import com.bewg.entity.Constant;
import com.bewg.entity.MesBody;
import com.bewg.entity.Message;
import com.bewg.fileIO.FileUtils;
import com.bewg.logger.utils.DateUtil;
import com.bewg.mqtt.AutoMqttProperties;
import com.bewg.mqtt.MqttUtils;
import com.bewg.scheduling.ScheduleTask;
import com.bewg.utils.CommontUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zcy
 * @date 2018/11/118:24
 * 本地打包线程 并上传mqtt
 */
@Slf4j
public class IoSPackDataThread implements Runnable {


    private LinkedBlockingDeque<String> packValues;

    private String topic;

    public IoSPackDataThread(LinkedBlockingDeque<String> packValues, String topic) {
        this.packValues = packValues;
        this.topic = topic;
    }

    @Override
    public void run() {
        //上传mqtt
        Gson gson= new Gson();
        String value = gson.toJson(packValues);
        try {
            //连接发送
            boolean isok = connectMqtt(topic,packValues);
            if(!isok){
                //记录到文件中 发送失败的的
                boolean ok = writeData2File(value,"fail");
                if (ok){
                    ScheduleTask.linkQueue1.removeAll(packValues);
                }
                //isok = connectMqtt(topic,value);
            }else{
                //发送mqtt的 数据包写入文件  成功的
                boolean ok = writeData2File(value,"success");
                //将队列清空
                if (ok){
                    log.error("保存采集数据到文件  成功！===");
                    ScheduleTask.linkQueue1.removeAll(packValues);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("保存采集数据到文件过程有异常！==="+e.getMessage());
        }

    }

    /*
     * @Author zcy
     * @Description //将发送mqtt的 数据包写入文件
     * @Date 18:27 2018/11/1
     * @Param
     * @return
     **/
    private boolean writeData2File(String value, String identify) throws IOException {
        FileUtils fileUtils= new FileUtils();// fileName,String path,String value
//        StringBuffer stringBuffer= new StringBuffer();
//        packValues.stream().forEach(string ->stringBuffer.append(string).append("\n"));
        return fileUtils.stallData(identify+"_data_"+ DateUtil.parseDateToStr(new Date(),"yyyyMMddHHmmss")+".txt", Constant.dataFilePath, value);//fileName, path, value
    }

    /*
     * @Author zcy
     * @Description //TODO 发送
     * @Date 14:42 2018/11/13
     * @Param
     * @return
     **/
    private boolean connectMqtt(String topic,LinkedBlockingDeque<String> packValues) throws Exception {
        AutoMqttProperties autoMqttProperties= new AutoMqttProperties();
        MqttUtils mqttUtils = new MqttUtils();
        MqttMessage message = new MqttMessage();
        message = packModel(message,packValues);
        boolean isok  =false;
        message.setQos(1);
        message.setRetained(false);
        isok  = mqttUtils.publish(topic,message);
        //连接mqtt
        log.info((isok?"打包成功了":"打包失败了"));
        log.info("topic---"+topic+".  信息为------"+ Arrays.toString(packValues.toArray()));
        return isok;
    }

    /*
     * @Author zcy
     * @Description //TODO 封装对象
     * @Date 14:42 2018/11/13
     * @Param
     * @return
     **/
    private MqttMessage packModel(MqttMessage message, LinkedBlockingDeque<String> packValues) {
        Message model= Constant.messageModel;
        //协议头
        Message messageValues = new Message();
        messageValues.setCreate_time(System.currentTimeMillis());
        messageValues.setMsg_id(Constant.local_mac + "_" + System.currentTimeMillis());
        messageValues.setSource_mac(Constant.local_mac);
        messageValues.setMsg_type(4);//1 注册消息 2 心跳消息 3 业务消息 4 配置消息  5 服务状态消息 6 应答消息 7 日志消息
        messageValues.setCallback_id(model.getMsg_id());
        messageValues.setSource_type("PROXY");
        messageValues.setLicense("");
        //子信息
        MesBody mesBody= new MesBody();
        mesBody.setContext(Arrays.asList(packValues.toArray(new String[packValues.size()])));
        mesBody.setSub_type(2);//采集代理的配置信息
        messageValues.setBody(mesBody);
        message.setPayload(CommontUtils.Object2Str(messageValues).getBytes());
        return  message;
    }


}
