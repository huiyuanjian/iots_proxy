package com.bewg.ioServer.thread;

import com.bewg.entity.*;
import com.bewg.entity.proxy.DeviceInfo;
import com.bewg.entity.proxy.VarInfo;
import com.bewg.ioServer.utils.IoServerSingle;
import com.bewg.ioServer.utils.IoServerUtils;
import com.bewg.mqtt.MqttUtils;
import com.bewg.utils.CommontUtils;
import com.ioserver.bean.Struct_DeviceProperty;
import com.ioserver.bean.Struct_TagProperty;
import com.sun.jna.WString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zcy
 * @date 2018/10/3111:04
 * 采集ioserver设备变量 配置线程
 */
@Slf4j
public class IoServerEqvThread implements Runnable {

    private IoServerSingle ioServerSingle;

    private IoSConfigThread ioSConfigThread =new IoSConfigThread();

    private Message message;

    private String topicc;

    private static MqttUtils mqttUtils = new MqttUtils();;

    private int index = Integer.parseInt(Constant.index);

    //回复消息标示 false是 一直发，ture是不用再次发送了
    public volatile static boolean accessIs=true;

    @Override
    public void run() {

        ioServerSingle=IoServerSingle.getInstance();
        CommonConfig commonConfig=null;
        if (message!=null){
            if (message.getBody()!=null){
                commonConfig=CommontUtils.byte2Object(message.getBody().getContext().get(0),new CommonConfig());
            }
        }
        try {
            //回复 发布者
            ioSConfigThread.messageArrivedAnswer(Constant.success,message,this.topicc);
            IoServerUtils IoServerUtils = new IoServerUtils(ioServerSingle.getIoServer());
            //获取设备信息
            Struct_DeviceProperty[] devices  = IoServerUtils.BrowserDevices( ioServerSingle.getIoServer().getHandle(),  new WString(""));
            List<VarInfo> varInfoLi= new ArrayList<VarInfo>();
            List<DeviceInfo> deviceInfoLi= new ArrayList<DeviceInfo>();
            if (devices!=null && devices.length>0) {
                int i = 0;
                for (Struct_DeviceProperty device : devices) {
                    // int deviceId, String name, String addr, int timeOut, int timeSpan
                    DeviceInfo deviceInfo = new DeviceInfo(device.DeviceID.intValue(), device.DeviceName.toString(), device.DeviceAddrString.toString(),device.MaxRetryTime.intValue(), device.RetryIntervalUnit.intValue());
                    //获取设备下变量信息
                    Struct_TagProperty[] tags = IoServerUtils.BrowserCollectTags(ioServerSingle.getIoServer().getHandle(), device.DeviceName);
                    //int id, int varId, String name, String deviceName, int deviceId, String info, int errorAsNull, int timeOut, int packType, int timeSpan
                    if (tags != null && tags.length > 0) {
                        for (Struct_TagProperty tag : tags) {
                            if (tag.TagName.toString().indexOf(new String("$")) == -1) {
                                log.info("DeviceName=="+device.DeviceName.toString()+"  TagAccessID=="+tag.TagAccessID.intValue()+"  TagName=="+tag.TagName.toString()+"  GroupName=="+tag.GroupName);
                                //封装对象 int id, int var_id,  String name, int deviceId, String info, int time_out, int pack_type, int time_span
                                VarInfo VarInfo = new VarInfo(tag.TagAccessID.intValue(), tag.TagName.toString(),device.DeviceName.toString(), tag.DeviceID.intValue(), tag.Description.toString());
                                varInfoLi.add(VarInfo);
                            }
                        }
                    }
                    deviceInfoLi.add(deviceInfo);
                }
                //协议头
                Message messageValues = new Message();
                messageValues.setCreate_time(System.currentTimeMillis());
                messageValues.setMsg_id(Constant.local_mac + "_" + System.currentTimeMillis());
                messageValues.setSource_mac(Constant.local_mac);
                messageValues.setMsg_type(4);//1 注册消息 2 心跳消息 3 业务消息 4 配置消息  5 服务状态消息 6 应答消息 7 日志消息
                messageValues.setCallback_id(message.getMsg_id());
                messageValues.setSource_type("PROXY");
                messageValues.setLicense("");
                //子信息
                CommonConfig collectionAgentConfig= new CommonConfig();
                collectionAgentConfig.setServer_name(commonConfig.getServer_name());
                collectionAgentConfig.setDomain(commonConfig.getDomain());
                collectionAgentConfig.setServer_mac(commonConfig.getServer_mac());
                collectionAgentConfig.setServer_id(commonConfig.getServer_id());
                collectionAgentConfig.setServer_type(commonConfig.getServer_type());
                collectionAgentConfig.setServer_remark(commonConfig.getServer_remark());
                collectionAgentConfig.setSend_palpitate_interval(commonConfig.getSend_palpitate_interval());
                collectionAgentConfig.setIdentifying_code(commonConfig.getIdentifying_code());
                collectionAgentConfig.setDevice_list(deviceInfoLi);
                collectionAgentConfig.setVar_list(varInfoLi);

                //发送全部的 设备 变量信息
                MqttMessage mqttMessage = new MqttMessage();
                //消息体
                MesBody mesBody= new MesBody();
                List<String> bodyLi= new ArrayList<String>();
                bodyLi.add(CommontUtils.Object2Str(collectionAgentConfig));
                mesBody.setContext(bodyLi);
                mesBody.setSub_type(2);//采集代理的配置信息
                messageValues.setBody(mesBody);
                mqttMessage.setPayload(CommontUtils.Object2Str(messageValues).getBytes());
                mqttMessage.setQos(1);
                //topic
                String topic = topic= MessageFormat.format(Constant.configtopic_send,message.getSource_mac());
                boolean isok = mqttUtils.publish(topic, mqttMessage);
                while (accessIs){
                    if (index>0&&accessIs){
                        mqttUtils.publish(topic, mqttMessage);
                        index--;
                    }else{
                        break;
                    }
                    Thread.sleep(2000);
                }
                if (!isok) {
                    log.info("发送设备变量失败  topic： " + topic + "    发送信息为：" + CommontUtils.Object2Str(mqttMessage));
                }
            }
            //保存到本地
            ioSConfigThread.saveConfigInfo2File(this.message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取设备变量信息出现异常！==="+e.getMessage());
        }
    }

    public IoServerEqvThread(Message message,String topic) {
        this.message =  message;
        this.topicc=topic;
    }


}
