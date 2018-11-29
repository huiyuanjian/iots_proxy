package com.bewg.utils.templates;

import com.alibaba.fastjson.JSONObject;
import com.bewg.entity.CommonConfig;
import com.bewg.entity.Constant;
import com.bewg.entity.MesBody;
import com.bewg.entity.Message;
import com.bewg.entity.ping.PingInfo;
import com.bewg.entity.regist.RegistInfo;
import com.bewg.entity.status.StatusInfo;
import com.bewg.logger.utils.ServerLog;
import com.bewg.utils.CommontUtils;
import com.bewg.utils.InetAddrUtils;
import com.bewg.utils.MonitorMCUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hyperic.sigar.SigarException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息模版
 */
@Slf4j
public class MessageTemplates {

    /**
     * 获得MqttMessage
     */
    public MqttMessage getMqttMessage(Message message){
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setRetained(true);
        mqttMessage.setQos(1);
        mqttMessage.setPayload((JSONObject.toJSONString(message)).getBytes());
        return mqttMessage;
    }

    /**
     * 获得Message
     */
    public Message getMessage(int type, MesBody body){
        Message messageEntity= new Message();
        messageEntity.setMsg_id(Constant.local_mac+"_"+(new Date()).getTime());//发布者mac地址_时间戳
        messageEntity.setMsg_type(type);// 1 注册消息 2 心跳消息  3功能消息
        messageEntity.setSource_mac(Constant.local_mac);//发布者的mac地址
        messageEntity.setSource_type("PROXY");//发布者类服务类型，可参考《14_mqtt通讯协议》的 2.1款说明
        messageEntity.setCreate_time((new Date()).getTime());//消息生成时的时间戳
        messageEntity.setBody(body);
        return messageEntity;
    }

    /**
     * 获得MesBody
     */
    public MesBody getMesBody(int subType, Object... object){
        List<String> list = new ArrayList<>();
        //索引遍历
        for(int i=0;i<object.length;i++) {
            list.add(JSONObject.toJSONString(object[i]));
        }

        MesBody body = new MesBody();
        body.setSub_type(subType);
        body.setContext(list);
        return body;
    }

    /**
     * 注册信息
     */
    public RegistInfo getRegistInfo(){
        RegistInfo registInfo = new RegistInfo();
        registInfo.setHost("");
        registInfo.setMacAddress(Constant.local_mac);
        registInfo.setRemark("我是采集代理接口服务的注册信息");
        registInfo.setServerName("采集代理接口服务");
        registInfo.setServerType("PROXY");
        return registInfo;
    }

    /**
     * 服务的状态信息
     */
    public StatusInfo getStatusInfo() throws SigarException {
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setHost("");
        statusInfo.setMacAddress(Constant.local_mac);
        statusInfo.setServerType("PROXY");
        statusInfo.setServerName("采集代理");
        statusInfo.setRemark("采集代理服务器状态");
        statusInfo.setCreatetime(System.currentTimeMillis());
        // MonitorMCUtil
        MonitorMCUtil monitorMCUtil= new MonitorMCUtil();
        monitorMCUtil.getCpuCount();

        statusInfo.setNet_usage(1.0f);//网络带宽
        statusInfo.setMemory_used(2);//内存 已使用(单位 M)
        statusInfo.setMemory_free(3);// 内存 空闲的(单位 M)
        statusInfo.setMem_usage(4.0f);//内存 使用率(%)
        statusInfo.setIo_usage(5);//磁盘IO
        statusInfo.setCpu_usage(6);//CPU 使用率(%)
        statusInfo.setCpu_temperature(7);//CPU 温度(摄氏度)
        return statusInfo;
    }

    /**
     * 服务的心跳信息
     */
    public PingInfo getPingInfo(){
        PingInfo pingInfo= new PingInfo();
        pingInfo.setHost(InetAddrUtils.getDomain());
        pingInfo.setMacAddress(Constant.local_mac);
        pingInfo.setServerName("采集代理");
        pingInfo.setRemark("发送心跳");
        pingInfo.setServerType("PROXY");
        return pingInfo;
    }

    /*
     * @Author zcy
     * @Description //TODO serverlog
     * @Date 16:42 2018/11/22
     * @Param
     * @return
     **/
   public ServerLog getServerLog(Map<String,Map<String,String>> map){
       Message messageModel= Constant.messageModel;
       CommonConfig commonConfig = CommontUtils.byte2Object(messageModel.getBody().getContext().get(0),new CommonConfig());
       ServerLog  serverLog= new ServerLog();
       serverLog.setMap(map);
       serverLog.setServer_id(commonConfig.getServer_id());
       serverLog.setServer_mac(commonConfig.getServer_mac());
       serverLog.setServer_name(commonConfig.getServer_name());
       serverLog.setServer_type("PROXY");
       serverLog.setUpload_time(new Date());
       return serverLog;
   }

}
