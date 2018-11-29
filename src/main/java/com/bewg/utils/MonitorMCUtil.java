package com.bewg.utils;


import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

public class MonitorMCUtil {

	private static Logger logger = LoggerFactory.getLogger(MonitorMCUtil.class);

	public static void main(String[] args) throws Exception {
		MonitorMCUtil s = new MonitorMCUtil();
		logger.debug("CPU个数：{}", s.getCpuCount());
		logger.debug("----------------------------");
		s.testCpuPerc();
		s.getPhysicalMemory();
		s.testNetIfList();
	}

	/**
	 * 1.CPU资源信息
	 */
	// a)CPU数量（单位：个）
	public int getCpuCount() throws SigarException {
		Sigar sigar = new Sigar();
		try {
			return sigar.getCpuInfoList().length;
		} finally {
			sigar.close();
		}
	}

	// b)CPU的总量（单位：HZ）及CPU的相关信息
	public void getCpuTotal() {
		Sigar sigar = new Sigar();
		CpuInfo[] infos;
		try {
			infos = sigar.getCpuInfoList();
			for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
				CpuInfo info = infos[i];
				logger.debug("CPU的总量:" + info.getMhz());// CPU的总量MHz
				logger.debug("获得CPU的卖主：" + info.getVendor());// 获得CPU的卖主，如：Intel
				logger.debug("CPU的类别：" + info.getModel());// 获得CPU的类别，如：Celeron
				logger.debug("缓冲存储器数量：" + info.getCacheSize());// 缓冲存储器数量
				logger.debug("**************");
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}
	}

	// c)CPU的用户使用量、系统使用剩余量、总的剩余量、总的使用占用量等（单位：100%）
	public void testCpuPerc() {
		Sigar sigar = new Sigar();
		// 方式一，主要是针对一块CPU的情况
		// CpuPerc cpu;
		// try {
		// cpu = sigar.getCpuPerc();
		// printCpuPerc(cpu);
		// } catch (SigarException e) {
		// e.printStackTrace();
		// }
		// 方式二，不管是单块CPU还是多CPU都适用
		CpuPerc cpuList[] = null;
		try {
			cpuList = sigar.getCpuPercList();
		} catch (SigarException e) {
			e.printStackTrace();
			return;
		}
		logger.debug("*******cpu信息*******");
		for (int i = 0; i < cpuList.length; i++) {
			printCpuPerc(cpuList[i], i + 1);
		}
		logger.debug("*******cpu信息*******");
	}

	private void printCpuPerc(CpuPerc cpu, int n) {
		logger.debug("第 {} 块cpu：", n);// 用户使用率
		logger.debug("用户使用率:" + CpuPerc.format(cpu.getUser()));// 用户使用率
		logger.debug("系统使用率:" + CpuPerc.format(cpu.getSys()));// 系统使用率
		logger.debug("当前等待率:" + CpuPerc.format(cpu.getWait()));// 当前等待率
		logger.debug("Nice :" + CpuPerc.format(cpu.getNice()));//
		logger.debug("当前空闲率:" + CpuPerc.format(cpu.getIdle()));// 当前空闲率
		logger.debug("总的使用率:" + CpuPerc.format(cpu.getCombined()));// 总的使用率
	}

	/**
	 * 2.内存资源信息
	 * 
	 */
	public void getPhysicalMemory() {
		logger.debug("*******内存信息*******");
		// a)物理内存信息
		DecimalFormat df = new DecimalFormat("#0.00");
		Sigar sigar = new Sigar();
		Mem mem;
		try {
			mem = sigar.getMem();
			// 内存总量
			logger.debug("内存总量：{}G", df.format((float) mem.getTotal() / 1024 / 1024 / 1024));
			// 当前内存使用量
			logger.debug("当前内存使用量：{}G", df.format((float) mem.getUsed() / 1024 / 1024 / 1024));
			// 当前内存剩余量
			logger.debug("当前内存剩余量：{}G", df.format((float) mem.getFree() / 1024 / 1024 / 1024));
			// b)系统页面文件交换区信息
			Swap swap = sigar.getSwap();
			// 交换区总量
			logger.debug("交换区总量：{}G", df.format((float) swap.getTotal() / 1024 / 1024 / 1024));
			// 当前交换区使用量
			logger.debug("当前交换区使用量：{}G", df.format((float) swap.getUsed() / 1024 / 1024 / 1024));
			// 当前交换区剩余量
			logger.debug("当前交换区剩余量：{}G", df.format((float) swap.getFree() / 1024 / 1024 / 1024));
		} catch (SigarException e) {
			e.printStackTrace();
		}
		logger.debug("*******内存信息*******");
	}

	/**
	 * 3.操作系统信息
	 * 
	 */
	// a)取到当前操作系统的名称：
	public String getPlatformName() {
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception exc) {
			Sigar sigar = new Sigar();
			try {
				hostname = sigar.getNetInfo().getHostName();
			} catch (SigarException e) {
				hostname = "localhost.unknown";
			} finally {
				sigar.close();
			}
		}
		return hostname;
	}

	// b)取当前操作系统的信息
	public void testGetOSInfo() {
		OperatingSystem OS = OperatingSystem.getInstance();
		// 操作系统内核类型如： 386、486、586等x86
		logger.debug("OS.getArch() = {}", OS.getArch());
		logger.debug("OS.getCpuEndian() = {}", OS.getCpuEndian());//
		logger.debug("OS.getDataModel() = {}", OS.getDataModel());//
		// 系统描述
		logger.debug("OS.getDescription() ={} ", OS.getDescription());
		logger.debug("OS.getMachine() = {}", OS.getMachine());//
		// 操作系统类型
		logger.debug("OS.getName() = {}", OS.getName());
		logger.debug("OS.getPatchLevel() = {}", OS.getPatchLevel());//
		// 操作系统的卖主
		logger.debug("OS.getVendor() = {}", OS.getVendor());
		// 卖主名称
		logger.debug("OS.getVendorCodeName() = {}", OS.getVendorCodeName());
		// 操作系统名称
		logger.debug("OS.getVendorName() = {}", OS.getVendorName());
		// 操作系统卖主类型
		logger.debug("OS.getVendorVersion() = {}", OS.getVendorVersion());
		// 操作系统的版本号
		logger.debug("OS.getVersion() = {}", OS.getVersion());
	}

	// c)取当前系统进程表中的用户信息
	public void testWho() {
		try {
			Sigar sigar = new Sigar();
			org.hyperic.sigar.Who[] who = sigar.getWhoList();
			if (who != null && who.length > 0) {
				for (int i = 0; i < who.length; i++) {
					logger.debug("\n~~~~~~~~~{}~~~~~~~~~", String.valueOf(i));
					org.hyperic.sigar.Who _who = who[i];
					logger.debug("获取设备getDevice() = " + _who.getDevice());
					logger.debug("获得主机getHost() = " + _who.getHost());
					logger.debug("获取的时间getTime() = " + _who.getTime());
					// 当前系统进程表中的用户名
					logger.debug("获取用户getUser() = " + _who.getUser());
				}
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}
	}

	// 4.资源信息（主要是硬盘）
	// a)取硬盘已有的分区及其详细信息（通过sigar.getFileSystemList()来获得FileSystem列表对象，然后对其进行编历）：
	public void testFileSystemInfo() throws Exception {
		Sigar sigar = new Sigar();
		FileSystem fslist[] = sigar.getFileSystemList();
		DecimalFormat df = new DecimalFormat("#0.00");
		// String dir = System.getProperty("user.home");// 当前用户文件夹路径
		for (int i = 0; i < fslist.length; i++) {
			logger.debug("\n~~~~~~~~~~" + i + "~~~~~~~~~~");
			FileSystem fs = fslist[i];
			// 分区的盘符名称
			logger.debug("fs.getDevName() = " + fs.getDevName());
			// 分区的盘符名称
			logger.debug("fs.getDirName() = " + fs.getDirName());
			logger.debug("fs.getFlags() = " + fs.getFlags());//
			// 文件系统类型，比如 FAT32、NTFS
			logger.debug("fs.getSysTypeName() = " + fs.getSysTypeName());
			// 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
			logger.debug("fs.getTypeName() = " + fs.getTypeName());
			// 文件系统类型
			logger.debug("fs.getType() = " + fs.getType());
			FileSystemUsage usage = null;
			try {
				usage = sigar.getFileSystemUsage(fs.getDirName());
			} catch (SigarException e) {
				if (fs.getType() == 2)
					throw e;
				continue;
			}
			switch (fs.getType()) {
			case 0: // TYPE_UNKNOWN ：未知
				break;
			case 1: // TYPE_NONE
				break;
			case 2: // TYPE_LOCAL_DISK : 本地硬盘
				// 文件系统总大小
				logger.debug(" Total = " + df.format((float) usage.getTotal() / 1024 / 1024) + "G");
				// 文件系统剩余大小
				logger.debug(" Free = " + df.format((float) usage.getFree() / 1024 / 1024) + "G");
				// 文件系统可用大小
				logger.debug(" Avail = " + df.format((float) usage.getAvail() / 1024 / 1024) + "G");
				// 文件系统已经使用量
				logger.debug(" Used = " + df.format((float) usage.getUsed() / 1024 / 1024) + "G");
				double usePercent = usage.getUsePercent() * 100D;
				// 文件系统资源的利用率
				logger.debug(" Usage = " + df.format(usePercent) + "%");
				break;
			case 3:// TYPE_NETWORK ：网络
				break;
			case 4:// TYPE_RAM_DISK ：闪存
				break;
			case 5:// TYPE_CDROM ：光驱
				break;
			case 6:// TYPE_SWAP ：页面交换
				break;
			}
			logger.debug(" DiskReads = " + usage.getDiskReads());
			logger.debug(" DiskWrites = " + usage.getDiskWrites());
		}
		return;
	}

	// 5.网络信息
	// a)当前机器的正式域名
	public String getFQDN() {
		Sigar sigar = null;
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			try {
				sigar = new Sigar();
				return sigar.getFQDN();
			} catch (SigarException ex) {
				return null;
			} finally {
				sigar.close();
			}
		}
	}

	// b)取到当前机器的IP地址
	public String getDefaultIpAddress() {
		String address = null;
		try {
			address = InetAddress.getLocalHost().getHostAddress();
			// 没有出现异常而正常当取到的IP时，如果取到的不是网卡循回地址时就返回
			// 否则再通过Sigar工具包中的方法来获取
			if (!NetFlags.LOOPBACK_ADDRESS.equals(address)) {
				return address;
			}
		} catch (UnknownHostException e) {
			// hostname not in DNS or /etc/hosts
		}
		Sigar sigar = new Sigar();
		try {
			address = sigar.getNetInterfaceConfig().getAddress();
		} catch (SigarException e) {
			address = NetFlags.LOOPBACK_ADDRESS;
		} finally {
			sigar.close();
		}
		return address;
	}

	// c)取到当前机器的MAC地址
	public String getMAC() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			String[] ifaces = sigar.getNetInterfaceList();
			String hwaddr = null;
			for (int i = 0; i < ifaces.length; i++) {
				NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
				if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0 || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
					continue;
				}
				/*
				 * 如果存在多张网卡包括虚拟机的网卡，默认只取第一张网卡的MAC地址，如果要返回所有的网卡（包括物理的和虚拟的）
				 * 则可以修改方法的返回类型为数组或Collection ，通过在for循环里取到的多个MAC地址。
				 */
				hwaddr = cfg.getHwaddr();
				break;
			}
			return hwaddr != null ? hwaddr : null;
		} catch (Exception e) {
			return null;
		} finally {
			if (sigar != null)
				sigar.close();
		}
	}

	// d)获取网络流量等信息
	public void testNetIfList() throws Exception {
		Sigar sigar = new Sigar();
		String ifNames[] = sigar.getNetInterfaceList();
		for (int i = 0; i < ifNames.length; i++) {
			String name = ifNames[i];
			NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
			print("\nname = " + name);// 网络设备名
			print("Address = " + ifconfig.getAddress());// IP地址
			print("Netmask = " + ifconfig.getNetmask());// 子网掩码
			if ((ifconfig.getFlags() & 1L) <= 0L) {
				print("!IFF_UP...skipping getNetInterfaceStat");
				continue;
			}
			try {
				NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
				print("RxPackets = " + ifstat.getRxPackets());// 接收的总包裹数
				print("TxPackets = " + ifstat.getTxPackets());// 发送的总包裹数
				print("RxBytes = " + ifstat.getRxBytes());// 接收到的总字节数
				print("TxBytes = " + ifstat.getTxBytes());// 发送的总字节数
				print("RxErrors = " + ifstat.getRxErrors());// 接收到的错误包数
				print("TxErrors = " + ifstat.getTxErrors());// 发送数据包时的错误数
				print("RxDropped = " + ifstat.getRxDropped());// 接收时丢弃的包数
				print("TxDropped = " + ifstat.getTxDropped());// 发送时丢弃的包数
			} catch (SigarNotImplementedException e) {
			} catch (SigarException e) {
				print(e.getMessage());
			}
		}
	}

	void print(String msg) {
		logger.debug(msg);
	}

	// e)一些其他的信息
	public void getEthernetInfo() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			String[] ifaces = sigar.getNetInterfaceList();
			for (int i = 0; i < ifaces.length; i++) {
				NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
				if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0 || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
					continue;
				}
				logger.debug("cfg.getAddress() = " + cfg.getAddress());// IP地址
				logger.debug("cfg.getBroadcast() = " + cfg.getBroadcast());// 网关广播地址
				logger.debug("cfg.getHwaddr() = " + cfg.getHwaddr());// 网卡MAC地址
				logger.debug("cfg.getNetmask() = " + cfg.getNetmask());// 子网掩码
				logger.debug("cfg.getDescription() = " + cfg.getDescription());// 网卡描述信息
				logger.debug("cfg.getType() = " + cfg.getType());//
				logger.debug("cfg.getDestination() = " + cfg.getDestination());
				logger.debug("cfg.getFlags() = " + cfg.getFlags());//
				logger.debug("cfg.getMetric() = " + cfg.getMetric());
				logger.debug("cfg.getMtu() = " + cfg.getMtu());
				logger.debug("cfg.getName() = " + cfg.getName());
			}
		} catch (Exception e) {
			logger.debug("Error while creating GUID" + e);
		} finally {
			if (sigar != null)
				sigar.close();
		}
	}
}
