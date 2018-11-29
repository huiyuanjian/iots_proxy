package com.bewg;

import com.bewg.ioServer.utils.IoServerSingle;
import com.sun.jna.NativeLibrary;
import com.sun.jna.WString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CtrlCommondTest {

    IoServerSingle ioServerSingle=null;
    @Test
    public void test1()  {
        //File file = new File(System.getenv("LOCALAPPDATA"));
       /* System.out.println("================"+System.getenv("LOCALAPPDATA")+"\\kxIOClient");
        NativeLibrary nativeLibrary = NativeLibrary.getInstance(System.getenv("LOCALAPPDATA")+"\\kxIOClient");
        //http://域名/IOServer标识/分组标识/设备标识/变量标识/写变量值_时间戳_超时时间_尝试次数
        ioServerSingle=new IoServerSingle();
        String ctrlCommond="http://www.qq.cn/ioserver/groupid/deviceid/5002/56_"+System.currentTimeMillis()+"_10_2";
        int result = parseMag(ctrlCommond);
        System.out.println(" ctrl result="+result);*/

    }

    /*
     * @Author zcy
     * @Description //解析控制服务传过来的 命令信息
     * @Date 10:23 2018/11/2
     * @Param
     * @return
     * http://域名/IOServer标识/分组标识/设备标识/变量标识/写变量值_时间戳_超时时间_尝试次数
     * http://www.baidu.com/1234567890/123456789/0/5001/123_1234567890_60_5
     **/
    private int parseMag(String message){
        String tempinfo = message.substring(0,message.lastIndexOf("/"));
        String tempValue = message.substring(message.lastIndexOf("/")+1);
        String []temRes=tempValue.split("_");
        String tempVal=temRes[0];//值
        Integer tagId=Integer.parseInt(tempinfo.substring(tempinfo.lastIndexOf("/")+1));
        int timeout=Integer.parseInt(temRes[2]);//超时
        int cycle=Integer.parseInt(temRes[3]);//尝试次数
        long time=Long.parseLong(temRes[1]);//时间戳
        int result =-1;
        return funcAsyncWrite(tagId, new WString(tempVal));
    }


    /*
     * @Author zcy
     * @Description //TODO 同步写值
     * @Date 15:23 2018/11/14
     * @Param
     * @return
     **/
    public int funcSyncWrite(int[] TagIDs, List<WString> valuelist) {
        return ioServerSingle.getIoServer().SyncWriteTagsValueByIDs(ioServerSingle.getIoServer().getHandle(), valuelist, TagIDs);
    }
    /*
     * @Author zcy
     * @Description //TODO 异步写值
     * @Date 15:23 2018/11/14
     * @Param
     * @return
     **/
    public int funcAsyncWrite(int TagIDs, WString tagValue) {
        if (tagValue == null) {
            return -1;
        }
        int [] tagd= new int[1];
        tagd[0]=TagIDs;
        WString[] wsTagName = new WString[1];
        List<WString> valuelist = new ArrayList<WString>();
        valuelist.add(tagValue);
        return ioServerSingle.getIoServer().AsyncWriteTagsValueByIDs(ioServerSingle.getIoServer().getHandle(), valuelist,tagd);

    }
}
