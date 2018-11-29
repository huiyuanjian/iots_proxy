package com.bewg.ioServer.thread;

import com.bewg.entity.*;
import com.bewg.entity.selfDone.SubscribeInfo;
import com.bewg.fileIO.FileUtils;
import com.bewg.ioServer.utils.IoSConfigParserUtil;
import com.bewg.ioServer.utils.IoServerSingle;
import com.bewg.ioServer.utils.IoServerUtils;
import com.bewg.scheduling.ScheduleTask;
import com.bewg.utils.CommontUtils;
import com.bewg.utils.InetAddrUtils;
import com.ioserver.bean.Struct_TagInfo;
import com.ioserver.bean.Struct_TagInfo_AddName;
import com.ioserver.dll.ClientDataBean;
import com.ioserver.dll.GlobalCilentBean;
import com.ioserver.dll.IOServerAPICilent;
import com.sun.jna.WString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zcy
 * @date 2018/10/3111:04
 * 开启实时接收ioserver 数据线程
 * 同步接收
 */
@Component
@Slf4j
public class IoServerListenner implements Runnable{

    //数据
    public volatile static ConcurrentHashMap<String,BlockingDeque<String>>  tempCollectVal = new ConcurrentHashMap<String,BlockingDeque<String>>();
    //时间标志
    public volatile static ConcurrentHashMap<String,Long>  tempTime = new ConcurrentHashMap<String,Long>();
    private  IoServerSingle ioServerSingle ;

    //获取要监听的变量
    public static volatile ConcurrentHashMap<String,Object> subscribeTags = new ConcurrentHashMap<String, Object>();

    private ScheduleTask scheduleTask= new ScheduleTask();

    /*
     * @Author zcy
     * @Description //手动开启线程去监听收取数据
     * @Date 15:08 2018/11/1
     * @Param
     * @return
     **/
    @Override
    public void run(){
        ioServerSingle= IoServerSingle.getInstance();
        IOServerAPICilent ioServerAPICilent =null;
        try {
            //检测本地配置文件  有没有 有就去加载
            subscribeTags = loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("加载本地配置文件有异常！====="+e.getMessage());
        }
        ioServerAPICilent = ioServerSingle.getIoServer();
        //返回值
        while(true){
            if (ioServerAPICilent==null){
                log.error("IOserver 连接失败！");
                return;
            }
            try {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //判断订阅的主题是否空
                        if(!CollectionUtils.isEmpty(subscribeTags)){
                            Set<String> sets = subscribeTags.keySet();
                            for (String key: sets){
                                if (key.indexOf("-VarNamelist")!=-1){
                                    BlockingDeque<String> blockingDeque= new LinkedBlockingDeque<>();
                                    WString[] wast = (WString[])subscribeTags.get(key);
                                    //封装name
                                    Struct_TagInfo_AddName[] values =  funcSyncRead(wast);
                                    if (values.length==0||values==null) {
                                        log.error("ioserver 服务器停了！");
                                        return;
                                    }
                                    for (Struct_TagInfo_AddName value:values) {
                                        int tagid=value.TagID;
                                        SubscribeInfo info= (SubscribeInfo)subscribeTags.get(tagid+"&&"+value.TagName+"-Model");
                                        StringBuffer stringBuffer= new StringBuffer();
                                        stringBuffer.append("http://");
                                        stringBuffer.append(info.getDomain());
                                        stringBuffer.append("/");
                                        stringBuffer.append(info.getServiceId());
                                        stringBuffer.append("/");
                                        stringBuffer.append(info.getGroupId());//分组标识
                                        stringBuffer.append("/");
                                        stringBuffer.append(info.getDevice_id());
                                        stringBuffer.append("/");
                                        stringBuffer.append(info.getId());
                                        stringBuffer.append(",");
                                        stringBuffer.append(value.TagValue.TagValue.i2Val);
                                        stringBuffer.append(",");
                                        stringBuffer.append(System.currentTimeMillis());

                                        //http://域名/IOServer标识/分组标识/设备标识/变量标识,变量值,时间戳
                                        log.info("IOserver  值==="+stringBuffer.toString());
                                        blockingDeque.add(stringBuffer.toString());
                                    }
                                    //放入map
                                    String tempStr=key.split("&&")[0];
                                    if (tempTime.get(key)!=null){
                                        long time= tempTime.get(key);
                                        BlockingDeque<String> blockingDeque1 = tempCollectVal.get(tempStr+"##time="+time);
                                        blockingDeque1.addAll(blockingDeque);
                                        if (blockingDeque1.size()>=30){
                                            tempCollectVal.put(tempStr+"##time="+time,blockingDeque1);
                                        }
                                    }else{
                                        long time=(new Date()).getTime();
                                        tempTime.put(key,time);
                                        tempCollectVal.put(tempStr+"##time="+time,blockingDeque);
                                    }

                                }
                            }
                        }
                    }


                },"监听收取数据-Data-Thread").start();

                Thread.sleep(10000);
            }catch (Exception ee){
                log.error("监听收取数据 有异常！====="+ ee.getMessage());
            }
        }
    }

    /*
     * @Author zcy
     * @Description //TODO 检测本地配置文件  有没有 有就去加载
     * @Date 9:49 2018/11/9
     * @Param
     * @return
     **/
    private ConcurrentHashMap<String, Object> loadConfig() throws IOException {
        //Constant.configFileName;
        FileConfigStall fileConfigStall = IoSConfigThread.loadLocalConfig();
        if (fileConfigStall==null)return null;
        Message message = CommontUtils.byte2Object(fileConfigStall.getValue(),new Message());
        CommonConfig proxy = CommontUtils.byte2Object(message.getBody().getContext().get(0),new CommonConfig());
        //开启心跳
        if(proxy.getSend_palpitate_interval()!=0){
            scheduleTask.changeCron(proxy.getSend_palpitate_interval());
        }
        if (proxy.getServer_id()==null){//本地配置设置为空
            //将本地覆盖
            return null;
        }

        //要采集的 设备变量
        ConcurrentHashMap<String,Object> subscribe= this.subscribeTags;
        //解析
        IoSConfigParserUtil ioSConfigParserUtil= new IoSConfigParserUtil();
        ConcurrentHashMap<String, Object> subscribeTags = ioSConfigParserUtil.parseMqMessages(subscribe,message);
        return  subscribeTags==null?(new ConcurrentHashMap<String, Object>()):subscribeTags;
    }

    /*
     * @Author zcy
     * @Description //根据变量名 获取变量标签对象
     * @Date 18:21 2018/11/1
     * @Param
     * @return
     **/
    private   Struct_TagInfo funcGetTagValue(WString TagName) {
        ClientDataBean dataBean = GlobalCilentBean.getInstance().getClientByHandle(ioServerSingle.getIoServer().getHandle());
        Struct_TagInfo structTagValue = dataBean.getTagValueByName(TagName);
        return structTagValue;
    }
    /*
     * @Author zcy
     * @Description //同步读取 变量值
     * @Date 14:42 2018/11/1
     * @Param
     * @return
     **/
    private Struct_TagInfo_AddName[] funcSyncRead(WString[] tagNames) {
        if ( (subscribeTags.size() < 1)) {
            return null;
        }
        //SyncReadTagsValueByIDs
        if (ioServerSingle.getIoServer()==null)return null;
        Struct_TagInfo_AddName[] structTagValue = ioServerSingle.getIoServer().SyncReadTagsValueReturnNames(ioServerSingle.getIoServer().getHandle(), tagNames,
                tagNames.length, 0);
        return structTagValue;
    }

}
