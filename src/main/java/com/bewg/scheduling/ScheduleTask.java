package com.bewg.scheduling;

import com.bewg.entity.Constant;
import com.bewg.entity.MesBody;
import com.bewg.entity.Message;
import com.bewg.entity.ping.PingInfo;
import com.bewg.fileIO.FileUtils;
import com.bewg.ioServer.thread.IoSPackDataThread;
import com.bewg.ioServer.thread.IoServerListenner;
import com.bewg.logger.LogUtil;
import com.bewg.mqtt.MqttUtils;
import com.bewg.utils.CommontUtils;
import com.bewg.utils.InetAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zcy
 * @date 2018/10/2510:30
 * 将数据上传mqtt
 */
@Slf4j
@Component
@EnableScheduling
public class ScheduleTask  {

    @Resource
    private LogUtil logUtil;

    private MqttUtils mqttUtils = new MqttUtils();

    public volatile static LinkedBlockingDeque<String> linkQueue1;

    static {
        linkQueue1=new LinkedBlockingDeque<String>();
    }

    Object lock= new Object();
    /*
     * @Author zcy
     * @Description //每秒进行 队列扫描  符合大小的  进行上传mqtt
     * @Date 14:33 2018/11/2
     * @Param
     * @return
     **/
    @Scheduled(cron = "${schedules.scanning}") // 每秒调用一次
    public void runJob_log() {
        try {
            synchronized (lock){
                //topic   前缀  这里默认取的第一个 mac地址
                //查看队列
                ConcurrentHashMap<String, BlockingDeque<String>> hashmap = IoServerListenner.tempCollectVal;
                ConcurrentHashMap<String, Long> tempMap = IoServerListenner.tempTime;
                if (CollectionUtils.isEmpty(hashmap)){
                    //log.error("数据采集 队列空！");
                    return;
                }
                String prixTopic= MessageFormat.format(Constant.dataTopic_send , Constant.local_mac, Constant.messageModel.getSource_mac());
                //根据时间 大小进行判断 查看哪些符合打包发送
                Set<String> keys=  hashmap.keySet();
                for (String key : keys) {
                    if(hashmap.get(key)==null||hashmap.get(key).size()==0)return;
                    //解析
                    Map<String,String> map = parseValues(key);
                    //0:间隔时间打包；1：按分钟；2：每15分钟；3：每小时；4每天
                    Integer type=Integer.parseInt(map.get("pack_type"));
                    int time_span = Integer.parseInt(map.get("time_span"));
                    long time=Long.parseLong(map.get("time"));
                    long inteval=(System.currentTimeMillis()-time)/1000;//秒
                    if(type==0){//0间隔上传
                        //上传mqtt
                        if (inteval>=time_span){
                            linkQueue1.addAll(hashmap.get(key));
                            hashmap.remove(key);
                            tempMap.remove(key.substring(0,key.lastIndexOf("##"))+"&&-VarNamelist");
                        }
                    }else if (type==1){//1：按分钟
                        //判断时间
                        if (inteval>=5*time_span){
                            linkQueue1.addAll(hashmap.get(key));
                            hashmap.remove(key);
                            tempMap.remove(key.substring(0,key.lastIndexOf("##"))+"&&-VarNamelist");
                        }
                    }else if (type==2){//2：每15分钟
                        //判断时间
                        if (inteval>=10*time_span){
                            linkQueue1.addAll(hashmap.get(key));
                            hashmap.remove(key);
                            tempMap.remove(key.substring(0,key.lastIndexOf("##"))+"&&-VarNamelist");
                        }
                    }else if (type==3){//3：每小时
                        //判断时间
                        if (inteval>=60*60*3*time_span){
                            linkQueue1.addAll(hashmap.get(key));
                            hashmap.remove(key);
                            tempMap.remove(key.substring(0,key.lastIndexOf("##"))+"&&-VarNamelist");
                        }
                    }else if (type==4){//4每天
                        //判断时间
                        if (inteval>=60*60*24*time_span){
                            linkQueue1.addAll(hashmap.get(key));
                            hashmap.remove(key);
                            tempMap.remove(key.substring(0,key.lastIndexOf("##"))+"&&-VarNamelist");
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(linkQueue1)&&linkQueue1.size()>0){
                    //开启打包线程
                    new Thread(new IoSPackDataThread(linkQueue1,prixTopic)).start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("打包线程  出现异常！---："+e.getMessage());
        }
    }

    //解析
    private Map<String,String> parseValues(String string){
        String values[]=string.split("##");
        Map<String,String> map= new HashMap<String,String>();
        for (String value:values) {
            String[] val=value.split("=");
            map.put(val[0],val[1]);
        }
        return map;
    }


    /*
     * @Author zcy
     * @Description //每秒扫描上传mqtt失败的数据 进行上传mqtt
     * @Date 14:33 2018/11/2
     * @Param
     * @return
     **/
    @Scheduled(cron = "${schedules.reupload}") // 每秒调用一次
    public void uploadFailData() {
        FileUtils fileUtils= new FileUtils();// fileName,String path,String value
        List<String> listPaths= new ArrayList<String>();
        File file= new File(Constant.dataFilePath);
        listPaths = logUtil.getChild(file,listPaths,"success",true);
        if (!CollectionUtils.isEmpty(listPaths)){
            String prixTopic="COLLECT/PROXY/"+ Constant.local_mac+"/"+Constant.messageModel.getSource_mac() +"/CONFIG";
            for (String fileName:listPaths ) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String content = fileUtils.readDataLi(fileName.substring(fileName.lastIndexOf(File.separator)+1),Constant.dataFilePath);//fileName,String path
                            if (!StringUtils.isEmpty(content)){
                                MqttMessage message= new MqttMessage();
                                message.setQos(1);
                                message.setPayload(content.getBytes());
                                boolean isok = mqttUtils.publish(prixTopic,message);
                                if (isok){
                                    //重命名 文件
                                    logUtil.reanameFile(fileName,"--success");
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            log.error("上传mqtt失败的数据 进行上传mqtt出现异常！---："+e.getMessage());
                        }
                    }
                },"扫描打包失败数据-Thread").start();
            }
        }
    }


    private Timer timer = new Timer();

    //时间间隔
    private volatile static long cron=0;

    /*
     * @Author zcy
     * @Description //TODO 取消定时任务
     * @Date 9:57 2018/11/22
     * @Param
     * @return
     **/
    public void cancleTask() {
        timer.cancel();
    }

    /*
    * 更新时间间隔
     */
    public  void changeCron(long cronn){
        this.cron=cronn;
        timer.schedule(new PeriodicTask(), cron*1000);

    }

    /*
     * @Author zcy
     * @Description ToDo 发送心跳
     * @Date 14:33 2018/11/2
     * @Param
     * @return
     **/
    private class PeriodicTask extends TimerTask {

        @Override
        public void run() {
            try {
                boolean isok = sendPing2Mqtt();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("发送心跳信息有异常==="+e.getMessage());
            }
            timer.schedule(new PeriodicTask(), cron*1000);
        }
    }


    /*
     * @Author zcy
     * @Description //MAC发送
     * @Date 10:49 2018/11/4
     * @Param [strings]
     * @return void
     **/
    private boolean sendPing2Mqtt() throws Exception {
        boolean isok  =false;
        String pingTopic= MessageFormat.format(Constant.pingTopic,Constant.local_mac);
        MqttUtils mqttUtils = new MqttUtils();
        MqttMessage message = new MqttMessage();
        message.setQos(1);
        message.setRetained(false);
        //此处用类封装
        message = packEntityVal(message,Constant.local_mac);
        isok  = mqttUtils.publish(pingTopic,message);
        return isok;
    }

    /*
     * @Author zcy
     * @Description //TODO 心跳信息组装
     * @Date 13:44 2018/11/12
     * @Param
     * @return
     **/
    private MqttMessage packEntityVal(MqttMessage message, String macAddr) {
        Message messageEntity= new Message();
        messageEntity.setMsg_id(macAddr+"_"+(new Date()).getTime());//发布者mac地址_时间戳
        messageEntity.setMsg_type(1);// 1 注册消息 2 心跳消息  3功能消息
        messageEntity.setSource_mac(macAddr);//发布者的mac地址
        messageEntity.setSource_type("PROXY");//发布者类服务类型，可参考《14_mqtt通讯协议》的 2.1款说明
        messageEntity.setCreate_time((new Date()).getTime());//消息生成时的时间戳
        //心跳类
        PingInfo pingInfo= new PingInfo();
        pingInfo.setHost(InetAddrUtils.getDomain());
        pingInfo.setMacAddress(macAddr);
        pingInfo.setServerName("采集代理");
        pingInfo.setRemark("发送心跳");
        pingInfo.setServerType("PROXY");
        List<String> contextli= new ArrayList<String>();
        contextli.add(CommontUtils.Object2Str(pingInfo));

        //MesBody
        MesBody mesBody= new MesBody();
        mesBody.setSub_type(0);//
        mesBody.setContext(contextli);
        messageEntity.setBody(mesBody);
        message.setPayload(CommontUtils.Object2Byte(messageEntity));
        return  message;
    }


}
