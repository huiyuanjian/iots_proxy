package com.bewg.logger.scheduling;

import com.alibaba.fastjson.JSONObject;
import com.bewg.entity.Constant;
import com.bewg.entity.MesBody;
import com.bewg.entity.Message;
import com.bewg.logger.LogUtil;
import com.bewg.logger.UploadService;
import com.bewg.logger.utils.DateUtil;
import com.bewg.logger.utils.ServerLog;
import com.bewg.mqtt.MqttUtils;
import com.bewg.utils.templates.MessageTemplates;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zcy
 * @date 2018/10/2510:30
 * log文件处理
 */
@Slf4j
@Component
@EnableScheduling
public class LoggTask {

    @Value("${logging.pathL}")
    private String logPath;

    @Value("${module.name}")
    private String serverName;

    @Resource
    private LogUtil LogUtil;

    @Resource
    private UploadService UploadService;

    private MqttUtils mqttUtils = new MqttUtils();

    /**
     * log日志 每半小时上传 fastdfs 成功后保存在redis中
     * zcy
     * 20181025
     */
    @Scheduled(cron = "${schedules.upload}") //
    public void runJob_log() {
        List<String> listPaths= new ArrayList<String>();
        File file= new File(logPath);
        //检测下发文件为空的话 不会上传log文件
        if (Constant.messageModel==null)return;
        try {
            //根据目录 查找log文件
            listPaths = LogUtil.getChild(file,listPaths,"--Already",true);

            if (!CollectionUtils.isEmpty(listPaths)){
                //本地服务器的ip port
                String date=  DateUtil.parseDateToStr(new Date(),"yyyy-MM-dd");
                long time=new Date().getTime();
                Map<String,Map<String,String>> map= new HashMap<String,Map<String,String>>();
                Map<String,String> map2= new HashMap<String,String>();
                for (String path:listPaths) {
                    StringBuffer sf= new StringBuffer();
                    //上传log到 fastdfs 服务器
                    String url = UploadService.upload(path);
                    //IOTs_Proxy_2018-11-21.56.log
                    //服务类型_服务mac地址_日期_时间戳.log
                    sf.append("PROXY_").append(Constant.local_mac).append("_").append(date).append("_").append(time).append(".log");
                    map2.put(sf.toString(),url);
                    //将添加成功的 进行名称修改 标志 已经上传过  "--Already" 标识
//                    LogUtil.reanameFile(path,"--Already");
                    log.info("上传log到 fastdfs 服务器------"+url);
                }
                //判断是否上传成功
                if(!CollectionUtils.isEmpty(map2)){
                    map.put(date,map2);
                    String logTopic = MessageFormat.format(Constant.loggTopic,Constant.messageModel.getSource_mac());
                    //推送到mqtt
                    MqttMessage msg = new MqttMessage();
                    msg = packingData(map);
                    mqttUtils.publish(logTopic,msg);
                    log.info("保存redis  log url-------");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("LoggTask--runJob_log 上传日志log 出现异常：======"+e.getMessage());
        }
    }

    /*
     * @Author zcy
     * @Description //TODO 封装类
     * @Date 17:44 2018/11/21
     * @Param
     * @return
     **/
    private MqttMessage packingData(Map<String,Map<String,String>> map){
        MessageTemplates messageTemplates=new MessageTemplates();
        ServerLog serverLog = messageTemplates.getServerLog(map);
        MesBody body = messageTemplates.getMesBody(5,serverLog);
        Message message = messageTemplates.getMessage(3,body);
        return messageTemplates.getMqttMessage(message);
    }

    /**
     * log日志  fastdfs 成功后 删除
     * zcy
     * 20181025
     */
    @Scheduled(cron = "${schedules.delete}") // 每秒调用一次
    public void runJob_deletelog() {
        List<String> listPaths= new ArrayList<String>();
        File file= new File(logPath);
        //根据目录 查找log文件
        listPaths = LogUtil.getChild(file,listPaths,"--Already",false);
        if (!CollectionUtils.isEmpty(listPaths)){
            for (String path:listPaths) {
                //将删除名称为标志 已经上传过  "--Already" 标识
                LogUtil.deleteFile(path);
            }
        }

    }
}
