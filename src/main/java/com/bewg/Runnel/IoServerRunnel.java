package com.bewg.Runnel;

import com.bewg.ioServer.thread.IoServerListenner;
import com.bewg.ioServer.utils.IoServerSingle;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zcy
 * @date 2018/10/3016:37
 * 连接ioserver 实时接收数据
 *  并保存数据到本地
 */
@Component
@Order(3)
public class IoServerRunnel implements CommandLineRunner {

    @Override
    public void run(String... strings) {

        //创建线程
        new Thread(new IoServerListenner(),"实时接收数据-Thread").start();

    }
}
