package com.bewg.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

/**
 * @author zcy
 * @date 2018/10/3018:35
 */
public class InetAddrUtils {
    public static String getMACAddress() throws Exception {
        InetAddress ia = InetAddress.getLocalHost();
        // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        // 下面代码是把mac地址拼装成String
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            // mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }
        // 把字符串所有小写字母改为大写成为正规的mac地址并返回
        return sb.toString().toUpperCase().replaceAll("-", "");
    }

    public static String getDomain() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        addr.getAddress();
//      String ip=addr.getHostAddress();
//      addr=InetAddress.getByName(addr.getHostAddress());
       return addr.getHostAddress();

    }



    public static void main(String[] args) throws Exception {
        System.out.println("args = [" + InetAddrUtils.getDomain() + "]");
    }
}
