package com.bewg;

import com.sun.jna.NativeLibrary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IotsProxyMqttApplication {

	public static void main(String[] args) {
		//加载dll 文件 C:\Users\Administrator\AppData\Local
		NativeLibrary nativeLibrary = NativeLibrary.getInstance("/Users/zhouxidong/Documents/kxIOClient.dll");
		//NativeLibrary nativeLibrary = NativeLibrary.getInstance("C:\\dll\\kxIOClient.dll");
		//System.load("/User/zhouxidong/Documents/kxIOClient.dll");
		SpringApplication.run(IotsProxyMqttApplication.class, args);
	}


}
