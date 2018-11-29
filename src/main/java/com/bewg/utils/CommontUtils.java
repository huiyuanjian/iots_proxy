package com.bewg.utils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zcy
 * @date 2018/11/410:29
 */
public class CommontUtils {

    /*
     * @Author zcy
     * @Description //对象转 String
     * @Date 10:32 2018/11/4
     * @Param
     * @return
     **/
    public static <T> String Object2Str(T value){
        String url="";
        if(value!=null && value!=""){
            Gson gson = new Gson();
            url = gson.toJson(value);
            return url;
        }
        return null;
    }


    /*
     * @Author zcy
     * @Description //对象转 bytes【】
     * @Date 10:32 2018/11/4
     * @Param
     * @return
     **/
    public static byte[] Object2Byte(Object value){
        String url="";
        if(value!=null && value!=""){
            Gson gson = new Gson();
            url = gson.toJson(value);
            return url.getBytes();
        }
        return null;
    }


    /*
     * @Author zcy
     * @Description //对象转 bytes【】
     * @Date 10:32 2018/11/4
     * @Param
     * @return
     **/
    public static  <T> T byte2Object(String value,T t){
        if(value!=null && value!=""){
            Gson gson = new Gson();
            t = (T) gson.fromJson(value, t.getClass());
            return t;
        }
        return null;
    }

    public static void main(String[] args) {
        List list= new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);

        String val=CommontUtils.Object2Str(list);
        List ll= CommontUtils.byte2Object(val,new ArrayList());
        System.out.println("args = [" + ll.get(0) + "]");
    }

}
