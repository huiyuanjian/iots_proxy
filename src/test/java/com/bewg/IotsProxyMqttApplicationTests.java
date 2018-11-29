package com.bewg;

import com.bewg.entity.*;
import com.bewg.entity.ctrl.CtrlInfo;
import com.bewg.entity.proxy.DeviceInfo;
import com.bewg.entity.proxy.PackageInfo;
import com.bewg.entity.proxy.VarInfo;
import com.bewg.ioServer.thread.IoServerListenner;
import com.bewg.mqtt.MqttUtils;
import com.bewg.utils.CommontUtils;
import com.bewg.utils.InetAddrUtils;
import com.ioserver.dll.IOServerAPICilent;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//@RunWith(SpringRunner.class)
public class IotsProxyMqttApplicationTests {

	private static IOServerAPICilent client = new IOServerAPICilent();

	@Resource
	IoServerListenner ioServerListenner;

	@Test
	public  void main() {

		MqttUtils mqttUtils=new MqttUtils();
		MqttMessage mqttMessage= new MqttMessage();
		mqttMessage.setQos(1);
		mqttMessage  = packEntity(mqttMessage);
		//mqttMessage=packEntityCtrl(mqttMessage);
		String partConfigData=null;
		try {
			partConfigData="COLLECT/PROXY/"+ InetAddrUtils.getMACAddress()+"/CONFIG";
			//控制 接收
			//String pullCtr="COLLECT/PROXY/"+ InetAddrUtils.getMACAddress()+"/CTRL";
			//mqttUtils.publish(pullCtr,packEntityCtrl(mqttMessage));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mqttUtils.publish(partConfigData,mqttMessage);
	}

	private MqttMessage packEntityCtrl(MqttMessage mqttMessage){
		Message message= new Message();
		message.setSource_type("11");
		message.setCallback_id("11");
		message.setMsg_type(1);
		message.setMsg_id("1");
		try {
			message.setSource_mac(InetAddrUtils.getMACAddress());

		message.setCreate_time(System.currentTimeMillis());

		MesBody mesBody= new MesBody();
		mesBody.setSub_type(1);
		List<String> ll= new ArrayList<>();
		CtrlInfo ctrlInfo= new CtrlInfo();
		ctrlInfo.setData_type("string");
		ctrlInfo.setCreatetime(System.currentTimeMillis());
		ctrlInfo.setCtrl_info("http://www.qq.cn/ioserver/groupid/deviceid/5002/56_"+System.currentTimeMillis()+"_10_2");//http://域名/IOServer标识/分组标识/设备标识/变量标识/写变量值_时间戳_超时时间_尝试次数
		ctrlInfo.setProxy_mac(InetAddrUtils.getMACAddress());
		ll.add(CommontUtils.Object2Str(ctrlInfo));
		mesBody.setContext(ll);
		message.setBody(mesBody);
			mqttMessage.setPayload(CommontUtils.Object2Str(message).getBytes());
		return 	mqttMessage;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private MqttMessage packEntity(MqttMessage mqttMessage){
		Message message= new Message();
		message.setSource_type("11");
		message.setCallback_id("11");
		message.setMsg_type(1);
		message.setMsg_id("1");
		try {
			message.setSource_mac(InetAddrUtils.getMACAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.setCreate_time(System.currentTimeMillis());

		CommonConfig collectionAgentConfig= new CommonConfig();
		//deviceList
		List<VarInfo> varInfoLi= new ArrayList<>();
		VarInfo varInfo=new VarInfo();
		varInfo.setDevice_id(1);
		varInfo.setError_as_null(1);
		varInfo.setId(5001);
		varInfo.setVar_id(5001);
		varInfo.setInfo("111");
		varInfo.setName("变量1");
		varInfo.setPack_type(1);
		varInfo.setTime_span(20);
		VarInfo varInfo2=new VarInfo();
		varInfo2.setDevice_id(2);
		varInfo2.setError_as_null(1);
		varInfo2.setId(5002);
		varInfo2.setVar_id(5002);
		varInfo2.setInfo("222");
		varInfo2.setName("变量2");
		varInfo2.setPack_type(2);
		varInfo2.setTime_span(20);
		varInfoLi.add(varInfo);
		varInfoLi.add(varInfo2);

		VarInfo varInfo3=new VarInfo();
		varInfo3.setDevice_id(2);
		varInfo3.setError_as_null(1);
		varInfo3.setId(5003);
		varInfo3.setVar_id(5003);
		varInfo3.setInfo("111");
		varInfo3.setName("变量3");
		varInfo3.setPack_type(1);
		varInfo3.setTime_span(20);

		VarInfo varInfo4=new VarInfo();
		varInfo4.setDevice_id(2);
		varInfo4.setError_as_null(1);
		varInfo4.setId(5004);
		varInfo4.setVar_id(5004);
		varInfo4.setInfo("111");
		varInfo4.setName("变量4");
		varInfo4.setPack_type(1);
		varInfo4.setTime_span(20);
		varInfoLi.add(varInfo3);
		varInfoLi.add(varInfo4);
		collectionAgentConfig.setVar_list(varInfoLi);

		List<PackageInfo> packageInfos= new ArrayList<PackageInfo>();
		List<Integer> ids= new ArrayList<>();
		ids.add(5001);
		ids.add(5002);

		PackageInfo packageInfo= new PackageInfo();
		packageInfo.setId(1);
		packageInfo.setName("11");
		packageInfo.setGroup_id("1");
		packageInfo.setPack_type(1);
		packageInfo.setTime_out(10);
		packageInfo.setTime_span(10);
		packageInfo.setVar_list(ids);
		packageInfos.add(packageInfo);
		List<PackageInfo> packageInfos2= new ArrayList<PackageInfo>();
		PackageInfo packageInfo2= new PackageInfo();
		List<Integer> ids2= new ArrayList<>();
		ids2.add(5003);
		ids2.add(5004);
		packageInfo2.setId(2);
		packageInfo2.setName("22");
		packageInfo2.setGroup_id("2");
		packageInfo2.setPack_type(0);
		packageInfo2.setTime_out(20);
		packageInfo2.setTime_span(10);
		packageInfo2.setVar_list(ids2);
		packageInfos.add(packageInfo2);
		packageInfos2.add(packageInfo);
		collectionAgentConfig.setPack_list(packageInfos);
		collectionAgentConfig.setServer_name("ios");
		collectionAgentConfig.setServer_id("ios");
		collectionAgentConfig.setServer_remark("ios");
		collectionAgentConfig.setServer_type("ios");
		collectionAgentConfig.setDomain("www.welcome.com");
		collectionAgentConfig.setSend_palpitate_interval(15);
		List list= new ArrayList();
		MesBody msgBody= new MesBody();
		msgBody.setSub_type(2);
		list.add(CommontUtils.Object2Str(collectionAgentConfig));
		msgBody.setContext(list);
		message.setBody(msgBody);
		mqttMessage.setPayload(CommontUtils.Object2Str(message).getBytes());
		return  mqttMessage;
	}

	/*/@Test
	public void contextLoads() {
		// 连接ioserver
		boolean connecton = client.IOServerConnecton("127.0.0.1", 12380);
		if (funcIsConnect() != 0) {
			System.out.println("连接失败");
			return;
		}else{
			System.out.println("连接成功");
		}
		browserAllDevicesAndTags();

		// 断开连接
		int dis = client.IOServerDisConnect(client.getHandle()); // 0
	}

	// 查所有设备和变量
	public void browserAllDevicesAndTags() {
		Struct_DeviceProperty[] deviceProperties = client.BrowserDevices(client.getHandle(), new WString(""));
		for (int i = 0; i < deviceProperties.length; i++) {
			System.out.println(deviceProperties[i].DeviceName);
			Struct_TagProperty[] tagProperties = client.BrowserCollectTags(client.getHandle(), deviceProperties[i].DeviceName);
		}
	}

	// 查所有设备
	public Struct_DeviceProperty[] browserAllDevices() {
		Struct_DeviceProperty[] deviceProperties = client.BrowserDevices(client.getHandle(), new WString(""));
		return deviceProperties;
	}

	// 查所有变量
	public List<WString> browserAllTags() {
		if (funcIsConnect() != 0) {
			return null;
		}
		// 层次化浏览所有变量
		List<WString> vecSubscribeTagsName = new ArrayList<>();// 添加的订阅变量
		Struct_ChannelProperty[] channelProperties = client.BrowserChannels(client.getHandle(), new WString(""));

		for (int i_channel = 0; i_channel < channelProperties.length; i_channel++) {
			Struct_DeviceProperty[] deviceProperties = client.BrowserDevices(client.getHandle(),
					channelProperties[i_channel].ChannelName);

			for (int i_device = 0; i_device < deviceProperties.length; i_device++) {
				Struct_TagProperty[] tagProperties = client.BrowserCollectTags(client.getHandle(),
						deviceProperties[i_device].DeviceName);

				WString[] TagNames = new WString[tagProperties.length - 2];
				int i_tagNames = 0;
				for (int i_tag = 0; i_tag < tagProperties.length; i_tag++) {
					String wTagName = tagProperties[i_tag].TagName.toString();
					if (wTagName.indexOf(new String("$")) == -1) {
						TagNames[i_tagNames] = tagProperties[i_tag].TagName;
						vecSubscribeTagsName.add(tagProperties[i_tag].TagName);
						i_tagNames++;
					}
					int[] TagIDs = new int[i_tagNames];
					for (int i = 0; i < i_tagNames; i++) {
						TagIDs[i] = GlobalCilentBean.getInstance().getTagIDbyName(TagNames[i]);
					}
					client.SubscribeTagValuesChange(client.getHandle(), TagIDs, TagIDs.length);
				}
			}
		}

		return vecSubscribeTagsName;
	}

	// 是否是连接状态
	public int funcIsConnect() {
		if (client.IOServerIsConnected(client.getHandle()) == true) {
			if (client.getIOServerWorkStatus(client.getHandle()) == 0) {
				return 0;
			} else {
				return -1; // IOServer没启动
			}
		}
		return -2; // 连接断开
	}*/

}
