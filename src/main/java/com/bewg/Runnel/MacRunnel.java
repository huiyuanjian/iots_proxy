package com.bewg.Runnel;

import com.bewg.entity.Constant;
import com.bewg.entity.MesBody;
import com.bewg.entity.Message;
import com.bewg.entity.regist.RegistInfo;
import com.bewg.fileIO.FileUtils;
import com.bewg.mqtt.MqttUtils;
import com.bewg.utils.CommontUtils;
import com.bewg.utils.InetAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zcy
 * @date 2018/10/3016:37
 * 采集代理上传mac地址 到mqtt
 */
@Slf4j
@Component
@Order(1)
public class MacRunnel implements CommandLineRunner {


    FileUtils fileUtils= new FileUtils();

    @Override
    public void run(String... strings) {

        //开启线程
        new Thread(new Runnable(){
            @Override
            public void run() {
                boolean isok  =false;
                try {
                    //先从配置文件读取配置内容
                    String object = fileUtils.readDataLi(Constant.MACFileName,Constant.MACConfgPath);
                    //判断是否null
                    if(object!=null){
                        Message message=CommontUtils.byte2Object(object.toString(),new Message());
                        Constant.local_mac=message.getSource_mac();
                        return;
                    }
                    isok  = connectMqtt();
                    //连接mqtt
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        while (!isok){
                            isok  = connectMqtt();
                            Thread.sleep(10000);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    //如若出现异常则开启一个线程 不断去发直到发送成功为止
                    log.error("服务器启动发送MAC地址出现异常 信息为------"+e.getMessage());
                }

            }

            /*
             * @Author zcy
             * @Description //MAC发送
             * @Date 10:49 2018/11/4
             * @Param [strings]
             * @return void
             **/
            private boolean connectMqtt() throws Exception {
                boolean isok  =false;
                MqttUtils mqttUtils = new MqttUtils();
                MqttMessage message = new MqttMessage();
                message.setQos(2);
                message.setRetained(false);
                //获取ioserver mac 地址
                String macAddr = InetAddrUtils.getMACAddress();
                Constant.local_mac=macAddr;
                String macTopic= MessageFormat.format(Constant.macTopic,macAddr);
                //此处用类封装
                message = packEntityVal(message,macAddr);
                isok  = mqttUtils.publish(macTopic,message);
                log.info("服务器启动发{0}送MAC地址------"+(isok?"发送成功了":"发送失败了"));
                //连接mqtt
                return isok;
            }

            /*
             * @Author zcy
             * @Description //类的封装
             * @Date 10:47 2018/11/4
             * @Param [strings]
             * @return void
             **/
            private MqttMessage packEntityVal(MqttMessage message,String macAddr) throws IOException {
                Message messageEntity= new Message();
                messageEntity.setMsg_id(macAddr+"_"+(new Date()).getTime());//发布者mac地址_时间戳
                messageEntity.setMsg_type(1);// 1 注册消息 2 心跳消息  3功能消息
                messageEntity.setSource_mac(macAddr);//发布者的mac地址
                messageEntity.setSource_type("PROXY");//发布者类服务类型，可参考《14_mqtt通讯协议》的 2.1款说明
                messageEntity.setCreate_time((new Date()).getTime());//消息生成时的时间戳

                //注册类
                RegistInfo registInfo= new RegistInfo();
                registInfo.setHost(InetAddrUtils.getDomain());
                registInfo.setMacAddress(macAddr);
                registInfo.setServerName("采集代理");
                registInfo.setRemark("注册MAC");
                registInfo.setServerType("PROXY");
                List<String> contextli= new ArrayList<String>();
                contextli.add(CommontUtils.Object2Str(registInfo));

                //MesBody
                MesBody mesBody= new MesBody();
                mesBody.setSub_type(0);//此处有诈
                mesBody.setContext(contextli);
                messageEntity.setBody(mesBody);
                message.setPayload(CommontUtils.Object2Byte(messageEntity));
                //写入本地配置文件
                boolean isok = fileUtils.stallData(Constant.MACFileName,Constant.MACConfgPath,CommontUtils.Object2Str(messageEntity));
                log.info("服务器启动 配置文件保存成功？？------"+(isok?"保存成功了":"保存失败了"));
                return  message;
            }
        },"mac地址到mqtt-Thread").start();


    }
}
