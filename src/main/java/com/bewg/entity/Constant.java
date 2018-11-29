package com.bewg.entity;

import com.bewg.utils.InetAddrUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcy
 * @date 2018/11/216:52
 * 常量类
 */
@Data
public class Constant implements Serializable {

    //@Value("messages.cycle.index")
    public static String index="5";//发送次数

    private static String localPath=System.getenv("LOCALAPPDATA");

    //MAC地址存放路径
    public static String MACConfgPath=localPath+ File.separator+"IOTS"+File.separator+"MAC"+File.separator;

    //MAC文件名
    public  static String MACFileName="IoServer_MAC_FILE_NAME_.conf";

    //采集ioserver 数据存放目录
    public static String dataFilePath=localPath+ File.separator+"IOTS"+File.separator+"data-ioserver"+File.separator;

    //采集ioserver 数据存放目录
    //@Value("${IOServer.configPath}")
    public static String configFilePath=localPath+ File.separator+"IOTS"+File.separator+"config-ioserver"+File.separator;

    //要设备变量配置 存储文件名
    public static String EqVrConfigFileName="IOSERVER_Equipment_Vari_CONFIG_INFO_.conf";

    //配置存储文件名
    public static String configFileName="IOSERVER_CONFIG_INFO_.conf";

    //地址 存放
    public static Message messageModel;

    //响应消息发布者  --成功
    public static String success="SUCCESS";

    //响应消息发布者  --失败
    public static String fail="FAIL";

    //mac Topic
    public static String macTopic="REGIST/PROXY/{0}";

    //CTRL 回复消息发布者
    public static String ctrlTopic="PROXY/COLLECT/{0}/CTRL";

    //CTRL 回复消息发布者
    public static String answeTopic="PROXY/COLLECT/{0}/ANSWER";

    //发送配置
    public static String configtopic_send = "PROXY/COLLECT/{0}/CONFIG";

    //发送采集数据
    public static String dataTopic_send="PROXY/COLLECT/{0}/{1}/DATA";

    //接收配置
    public static String partConfigData="COLLECT/PROXY/{0}/DATA";

    //控制 接收
    public static String pullCtr="COLLECT/PROXY/{0}/CTRL";

    //心跳Topic
    public static String pingTopic="PING/PROXY/{0}";

    //日志Topic
    public static String loggTopic="PROXY/COLLECT/{0}/LOG";

    //answer 回复消息发布者
    public static String answerTopic_pull="COLLECT/PROXY/{0}/ANSWER";

    //本地MAC
    public static String local_mac;

}
