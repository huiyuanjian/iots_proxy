package com.bewg.mqtt.thread;

import com.bewg.entity.Constant;
import com.bewg.entity.MesBody;
import com.bewg.entity.Message;
import com.bewg.entity.ctrl.CtrlInfo;
import com.bewg.ioServer.thread.IoSConfigThread;
import com.bewg.ioServer.utils.IoServerSingle;
import com.bewg.mqtt.MqttUtils;
import com.bewg.utils.CommontUtils;
import com.sun.jna.WString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zcy
 * @date 2018/11/118:33
 * 处理控制 传过来的命令
 */
@Slf4j
public class CtrlMqttThread<T> implements Runnable {

    private MqttMessage mqttMessage ;

    private String topic;

    private IoServerSingle ioServerSingle ;

    private IoSConfigThread ioSConfigThread= new IoSConfigThread();

    //当前开始执行时间
    private static long time=0;

    //尝试次数
    private static int cycle=0;

    //超时时间
    private static int timeout=0;

    //回复消息标示 false是 一直发，ture是不用再次发送了
    public volatile static boolean accessIs=true;

    public CtrlMqttThread(MqttMessage mqttMessages, String topic ) {
        this.mqttMessage = mqttMessages;
        this.topic=topic;
    }

    /*
     * @Author zcy
     * @Description 工作线程
     * @Date 10:38 2018/11/23
     * @Param
     * @return
     **/
    @Override
    public void run() {
        int indexTmp= Integer.parseInt(Constant.index);
        try {
            //解析过程
            Message message = CommontUtils.byte2Object(new String(mqttMessage.getPayload()),new Message());
            //回复
            ioSConfigThread.messageArrivedAnswer(Constant.success,message,this.topic);
            ioServerSingle= IoServerSingle.getInstance();
            //解析控制服务传过来的 命令信息
            //http://域名/IOServer标识/分组标识/设备标识/变量标识/写变量值_时间戳_超时时间_尝试次数
            int result = parseMag( message);
            //将执行结果返回
            messageArrivedAnswer(result,message);
            while (accessIs){
                if (indexTmp>0&&accessIs){
                    messageArrivedAnswer(result,message);
                    indexTmp--;
                }else {
                    break;
                }
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("控制服务  出现异常！=="+e.getMessage());
        }
    }
    /*
     * @Author zcy
     * @Description 响应物联网接口
     * @Date 11:58 2018/11/5
     * @Param
     * @return
     **/
    public void messageArrivedAnswer(int result,Message message) throws Exception {
        //协议头
        Message messageValues = new Message();
        messageValues.setCreate_time(System.currentTimeMillis());
        messageValues.setMsg_id(Constant.local_mac + "_" + System.currentTimeMillis());
        messageValues.setSource_mac(Constant.local_mac);
        messageValues.setMsg_type(8);//1 注册消息 2 心跳消息 3 业务消息 4 配置消息  5 服务状态消息 6 应答消息 7 日志消息 8 控制消息
        messageValues.setCallback_id(message.getMsg_id());
        messageValues.setSource_type("PROXY");
        messageValues.setLicense("");

        //消息体
        MesBody mesBody= new MesBody();
        List<String> bodyLi= new ArrayList<String>();
        bodyLi.add((result==0?"成功":(result<0?"失败":"数据格式不正确")));
        mesBody.setContext(bodyLi);
        mesBody.setSub_type(result);//0:成功 ， 正数 格式错误 ，负数：失败
        messageValues.setBody(mesBody);

        // subscribe后得到的消息会执行到这里面
        mqttMessage.setPayload(CommontUtils.Object2Str(messageValues).getBytes());
        mqttMessage.setQos(1);
        //topic
        String answerTopic= MessageFormat.format(Constant.ctrlTopic,message.getSource_mac());// "PROXY/COLLECT/"+ Constant.local_mac+"/"+Constant.messageModel.getSource_mac()+"/ANSWER";
        MqttUtils mqttUtil= new MqttUtils();
        boolean isok = mqttUtil.publish(answerTopic,mqttMessage);
        log.info("控制 响应物联网接口 发送 Topic："+answerTopic+"   发送成功了？？----"+isok);
    }

    /*
     * @Author zcy
     * @Description 解析控制服务传过来的 命令信息
     * @Date 10:23 2018/11/2
     * @Param
     * @return
     * http://域名/IOServer标识/分组标识/设备标识/变量标识/写变量值_时间戳_超时时间_尝试次数
     * http://www.baidu.com/1234567890/123456789/0/5001/123_1234567890_60_5
     **/
    private int parseMag(Message message){
        if(mqttMessage!=null){
           //解析过程
            List<String> valList = message.getBody().getContext();
            if (CollectionUtils.isEmpty(valList)){
                return -1;
            }
            CtrlInfo ctrlInfo= CommontUtils.byte2Object(valList.get(0),new CtrlInfo());
            String info = ctrlInfo.getCtrl_info();
            String tempinfo = info.substring(0,info.lastIndexOf("/"));
            String tempValue = info.substring(info.lastIndexOf("/")+1);
            String []temRes=tempValue.split("_");
            String tempVal=temRes[0];//值
            Integer tagId=Integer.parseInt(tempinfo.substring(tempinfo.lastIndexOf("/")+1));
            timeout=Integer.parseInt(temRes[2]);//超时
            cycle=Integer.parseInt(temRes[3]);//尝试次数
            time=Long.parseLong(temRes[1]);//时间戳
            int result =-1;
            result = funcAsyncWrite(tagId, new WString(tempVal),ctrlInfo.getData_type());
            //下发配置
            if (result!=0){
                result = processWork(tagId, new WString(tempVal),ctrlInfo.getData_type());
            }
            return result;
        }
        return -1;
    }


    private int processWork(Integer tagId, WString wString, String data_type){
        int result=-1;
        long tempTime=0;
        while (result!=0&&cycle!=0&&(tempTime-time)/1000<timeout){
            result = funcAsyncWrite(tagId, wString,data_type);
            tempTime=System.currentTimeMillis();
            cycle--;
        }
        return result;
    }


    /*
     * @Author zcy
     * @Description 同步写值
     * @Date 15:23 2018/11/14
     * @Param
     * @return
     **/
    public int funcSyncWrite(int[] TagIDs, List<WString> valuelist) {
        return ioServerSingle.getIoServer().SyncWriteTagsValueByIDs(ioServerSingle.getIoServer().getHandle(), valuelist, TagIDs);
    }
    /*
     * @Author zcy
     * @Description 异步写值
     * @Date 15:23 2018/11/14
     * @Param
     * @return
     **/
    public int funcAsyncWrite(int TagIDs, WString tagValue,String type) {
        if (tagValue == null) {
            return -1;
        }
        int [] tagd= new int[1];
        tagd[0]=TagIDs;
        WString[] wsTagName = new WString[1];
        if (type .equals("int")) {
            List<Short> valuelist = new ArrayList<Short>();
            Short intValue = Short.parseShort(tagValue.toString());
            valuelist.add(intValue);
            return ioServerSingle.getIoServer().AsyncWriteTagsValueByIDs(ioServerSingle.getIoServer().getHandle(), valuelist,tagd);
        }
        if (type .equals("double")) {
            List<Float> valuelist = new ArrayList<Float>();
            float intValue = Float.parseFloat(tagValue.toString());
            valuelist.add((float) intValue);
            return ioServerSingle.getIoServer().AsyncWriteTagsValueByIDs(ioServerSingle.getIoServer().getHandle(), valuelist,tagd);
        }
        if (type .equals("string")) {
            List<WString> valuelist = new ArrayList<WString>();
            valuelist.add(tagValue);
            return ioServerSingle.getIoServer().AsyncWriteTagsValueByIDs(ioServerSingle.getIoServer().getHandle(), valuelist,tagd);
        }

        return -1;
    }
}
