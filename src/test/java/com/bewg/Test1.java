package com.bewg;

import com.bewg.mqtt.MqttUtils;
import com.bewg.scheduling.ScheduleTask;
import com.sun.jna.NativeLibrary;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Component
@EnableScheduling
@Slf4j
public class Test1 implements SchedulingConfigurer {
    static String cron="1";
    @Test
    public void test1()  {

        NativeLibrary nativeLibrary = NativeLibrary.getInstance(System.getenv("LOCALAPPDATA")+"\\kxIOClient.dll");
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        try {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    //任务逻辑代码部分.
                    System.out.println("I am going:" + LocalDateTime.now());
                }
            };
            Trigger trigger = new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    //任务触发，可修改任务的执行周期.
                    //每一次任务触发，都会执行这里的方法一次，重新获取下一次的执行时间
                    cron = "1";
                    CronTrigger trigger = new CronTrigger(cron);
                    Date nextExec = trigger.nextExecutionTime(triggerContext);
                    return nextExec;
                }
            };
            scheduledTaskRegistrar.addTriggerTask(task, trigger);
        }catch (Exception e){
            log.info(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Test1 test1= new Test1();
        //test1.configureTasks(this.);
        System.out.println("结束1，，，，");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("开始2，，，，");
        new Thread(()->{
            test1.cron="2";
        }).start();
    }
}
