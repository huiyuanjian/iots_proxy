package com.bewg.ioServer.utils;

import com.ioserver.dll.IOServerAPICilent;
import com.sun.jna.NativeLibrary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * @author zcy
 * @date 2018/10/3114:26
 */
@Slf4j
public class IoServerSingle {

    private static IoServerSingle ioServerSingle=new IoServerSingle();

    private static IOServerAPICilent ioServerAPICilent= new IOServerAPICilent();

    private static boolean connecton =false;

    private static String ip;

    private static int port;

    static {
        Resource resource = new ClassPathResource("/application.yml");
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            ip = props.getProperty("ioserver_ip");
            port = Integer.valueOf(props.getProperty("ioserver_port"));
            connecton = ioServerAPICilent.IOServerConnecton(ip, port);
        } catch (IOException e) {
            log.error("ioserver连接异常，异常信息为：{}",e.getMessage());
        }
    }
    public IoServerSingle (){
        if (connecton){
            log.info("与Ioserver连接成功了！");
        }else {
            getIoServerSingle();
        }
    }

    /*
     * @Author zcy
     * @Description //TODO 对象
     * @Date 10:50 2018/11/8
     * @Param
     * @return
     **/

    public static IoServerSingle getInstance(){
         return  ioServerSingle;
    }
    /*
     * @Author zcy
     * @Description //创建连接 ioserver
     * @Date 14:31 2018/10/31
     * @Param
     * @return
     **/

    private  boolean getIoServerSingle() {
        //NativeLibrary nativeLibrary = NativeLibrary.getInstance(System.getenv("LOCALAPPDATA")+"\\kxIOClient.dll");
        // 连接ioserver
        try {
            if (!connecton){
                log.info("与Ioserver连接失败了！");
                reLinkIoServer();
            }
            log.info("与Ioserver连接成功了！");
        } catch (Exception e) {
            reLinkIoServer();
        }
        return connecton;
    }

    public IOServerAPICilent  getIoServer(){
        if (connecton){
            return ioServerAPICilent;
        }else{
            return  null;
        }
    }

    private boolean reLinkIoServer(){
        log.info("与Ioserver 进入再次连接！");
        try {
            //每隔10秒再次尝试连接
            while (true) {
                connecton = ioServerAPICilent.IOServerConnecton(ip, port);
                if (connecton) {
                    log.info("与Ioserver连接成功了！");
                    return true;
                }else {
                    log.error("与Ioserver连接失败了！");
                }
                Thread.sleep(10000);
            }
        } catch (Exception ee) {
            log.error("与Ioserver连接失败！");
            log.error("与Ioserver连接失败  异常信息为：{}",ee.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        IoServerSingle ioServerSingle=  IoServerSingle.getInstance();
        ioServerSingle.getIoServer();
    }

}
