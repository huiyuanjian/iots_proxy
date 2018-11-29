package com.bewg.ioServer.utils;

import com.bewg.entity.*;
import com.bewg.entity.proxy.PackageInfo;
import com.bewg.entity.proxy.VarInfo;
import com.bewg.entity.selfDone.SubscribeInfo;
import com.bewg.utils.CommontUtils;
import com.sun.jna.WString;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zcy
 * @date 2018/11/910:12
 */
public class IoSConfigParserUtil {


    /*
     * @Author zcy
     * @Description //解析发布命令信息
     * @Date 14:27 2018/11/5
     * @Param
     * @return
     **/
    public ConcurrentHashMap<String, Object> parseMqMessages(ConcurrentHashMap<String, Object> subscribeTags, Message message) throws IOException {
        if (message!=null){
            //将配置类保存到内存中
            Constant.messageModel=message;
            //if (subscribeTags==null)
            subscribeTags=new ConcurrentHashMap<String, Object>();
            //解析  IoServerListenner
            for (String string:message.getBody().getContext()) {
                CommonConfig proxy = CommontUtils.byte2Object(string,new CommonConfig());
                //变量
                List<VarInfo> varInfos= proxy.getVar_list();
                if (CollectionUtils.isEmpty(varInfos))return null;
                Map<Integer,VarInfo> varMap= new HashMap<Integer,VarInfo>();
                for (VarInfo varInfo: varInfos) {
                    varMap.put(varInfo.getId(),varInfo);
                }
                //设备
                //List<DeviceInfo> deviceLi= proxy.getDevice_list();
                List<PackageInfo> packages= proxy.getPack_list();

                for (PackageInfo packageInfo: packages){
                    List<Integer> varIdLi= packageInfo.getVar_list();
                    WString[] varLis= new WString[varIdLi.size()];
                    for (int i=0;i<varIdLi.size();i++){//Integer id:varIdLi
                        VarInfo varModel = varMap.get(varIdLi.get(i));
                        varLis[i]= new WString(varModel.getName());
                        //实体封装
                        SubscribeInfo subscribeInfo= new SubscribeInfo();
                        subscribeInfo.setVarId(varModel.getVar_id());
                        subscribeInfo.setServiceId(proxy.getServer_id());
                        subscribeInfo.setServiceName(proxy.getServer_name());
                        subscribeInfo.setId(varModel.getId());//数据库中变量id
                        subscribeInfo.setName(varModel.getName());
                        subscribeInfo.setDevice_id(varModel.getDevice_id());
                        //subscribeInfo.setDevice_name();
                        subscribeInfo.setInfo(varModel.getInfo());
                        subscribeInfo.setTime_span(varModel.getTime_span());
                        subscribeInfo.setTime_out(varModel.getTime_out());
                        subscribeInfo.setPack_type(varModel.getPack_type());
                        subscribeInfo.setError_as_null(varModel.getError_as_null());
                        subscribeInfo.setDomain(proxy.getDomain());//域名
                        subscribeInfo.setGroupId(packageInfo.getGroup_id());//组名
                        subscribeTags.put(varModel.getVar_id()+"&&"+varModel.getName()+"-Model",subscribeInfo);
                    }
                    if (varLis!=null&&varLis.length>0){
                        StringBuffer stringBuffer= new StringBuffer();
                        stringBuffer.append("group_id=")
                        .append(packageInfo.getGroup_id())
                        .append("##pack_type=")
                        .append(packageInfo.getPack_type())
                        .append("##time_span=")
                        .append(packageInfo.getTime_span())
                        .append("&&-VarNamelist");
                        //变量集合
                        subscribeTags.put(stringBuffer.toString(),varLis);
                    }
                }

            }
        }
        return subscribeTags;
    }

}
