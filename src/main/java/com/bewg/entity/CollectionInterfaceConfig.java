package com.bewg.entity;

import java.util.List;

/**
 * @ClassName: CollectionInterfaceConfig
 * @Description: 物联网采集服务配置信息实体bean
 * @author 周西栋
 * @date 2018年5月7日
 *
 */
public class CollectionInterfaceConfig {

	/**
	 * 服务id
	 */
	private Integer id;
	
	/**
	 * 服务名称
	 */
	private String name;
	
	/**
	 * 服务地址
	 */
	private String address;
	
	/**
	 * 存储队列服务标识
	 */
	private String storage_topic;
	
	/**
	 * 设备容器
	 */
	private List<IotsIoserverInfoEntity> ioserver_list;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStorage_topic() {
		return storage_topic;
	}

	public void setStorage_topic(String storage_topic) {
		this.storage_topic = storage_topic;
	}

	public List<IotsIoserverInfoEntity> getIoserver_list() {
		return ioserver_list;
	}

	public void setIoserver_list(List<IotsIoserverInfoEntity> ioserver_list) {
		this.ioserver_list = ioserver_list;
	}

	@Override
	public String toString() {
		return "CollectionInterfaceConfig [id=" + id + ", name=" + name + ", address=" + address + ", storage_topic="
				+ storage_topic + ", ioserver_list=" + ioserver_list + "]";
	}
	
	
}
