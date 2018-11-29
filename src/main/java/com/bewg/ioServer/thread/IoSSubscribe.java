package com.bewg.ioServer.thread;

import com.bewg.entity.proxy.VarInfo;
import com.ioserver.dll.GlobalCilentBean;
import com.ioserver.dll.IOServerAPICilent;
import com.sun.jna.WString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zcy
 * @date 2018/11/114:11
 * IOserver  变量 订阅类
 */
@Slf4j
public class IoSSubscribe {

    private IOServerAPICilent client;

    public IoSSubscribe(IOServerAPICilent client) {
        this.client = client;
    }

    public IoSSubscribe() {
    }

    /*
     * @Author zcy
     * @Description 变量订阅
     * @Date 13:41 2018/11/1
     * @Param
     * @return
     **/
    public void subscribeTags(List<VarInfo> blockingDeque) {
        try {
            int isok  = subscribeIoserver(blockingDeque);
            if (isok>-1){
                log.info("订阅IO server 成功  订阅变量有：",blockingDeque.toArray());
            }else{
                subscribeIoserver(blockingDeque);
            }
        }catch (Exception e){
            //再次订阅
            try {
                subscribeIoserver(blockingDeque);
            }catch (Exception ee){
                log.error("订阅 ioserver 出现异常 请检查查："+ee.getMessage());
            }
        }

    }

    private int subscribeIoserver(List<VarInfo> blockingDeque){
        int[] TagIDs =new int[blockingDeque.size()];
        for (int i = 0; i < blockingDeque.size(); i++) {
            TagIDs[i] = GlobalCilentBean.getInstance().getTagIDbyName(new WString(blockingDeque.get(i).getName()));
        }
        return client.SubscribeTagValuesChange(client.getHandle(), TagIDs, TagIDs.length);
    }

}
