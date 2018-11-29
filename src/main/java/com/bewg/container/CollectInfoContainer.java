package com.bewg.container;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据采集线程采集到的容器
 */
public class CollectInfoContainer {

    private CollectInfoContainer() {
    }

    private static CollectInfoContainer instance = new CollectInfoContainer();

    public static CollectInfoContainer getInstance() {
        return instance;
    }

    private static List<Device> deviceList = new ArrayList<>();
    private static List<Var> varList = new ArrayList<>();
    private static List<Package> packList = new ArrayList<>();

    // 可以开始采集
    private static boolean canStartCollect = false;

    public static List<Device> getDeviceList() {
        return deviceList;
    }

    public static void setDeviceList(List<Device> deviceList) {
        CollectInfoContainer.deviceList = deviceList;
    }

    public static List<Var> getVarList() {
        return varList;
    }

    public static void setVarList(List<Var> varList) {
        CollectInfoContainer.varList = varList;
    }

    public static List<Package> getPackList() {
        return packList;
    }

    public static void setPackList(List<Package> packList) {
        CollectInfoContainer.packList = packList;
    }

    public static boolean isCanStartCollect() {
        return canStartCollect;
    }

    public static void setCanStartCollect(boolean canStartCollect) {
        CollectInfoContainer.canStartCollect = canStartCollect;
    }

    /**
     * 变量
     */
    class Var {
        private int id;
        private int varId;
        private String name;
        private String deviceName;
        private int type;
        private int deviceId;
        private String info;
        private int errorAsNull;
        private int timeOut;
        private int packType;
        private int timeSpan;
        private String value;
        private Date updateTime = new Date();
        private Date lastPackTime = new Date();

        public boolean canPack() {
            if (this.timeSpan == 0) {
                return false;
            }
            if (packType == 0) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > timeSpan) {
                    return true;
                }
            } else if (packType == 1) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 60) {
                    return true;
                }
            } else if (packType == 2) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 15 * 60) {
                    return true;
                }
            } else if (packType == 3) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 60) {
                    return true;
                }
            } else if (packType == 4) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 24 * 60) {
                    return true;
                }
            }
            return false;
        }

        // 打包
        public String pack() {
            String msg = this.deviceId + "/" + this.varId + "/";
            if ((System.currentTimeMillis() - updateTime.getTime()) / 1000 > timeOut) {
                if (errorAsNull == 1) {
                    msg += "NULL_" + System.currentTimeMillis();
                } else {
                    msg += this.value + "_" + System.currentTimeMillis();
                }
            } else {
                msg += this.value + "_" + System.currentTimeMillis();
            }
            this.lastPackTime = new Date();
            return msg;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getVarId() {
            return varId;
        }

        public void setVarId(int varId) {
            this.varId = varId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public int getErrorAsNull() {
            return errorAsNull;
        }

        public void setErrorAsNull(int errorAsNull) {
            this.errorAsNull = errorAsNull;
        }

        public int getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(int timeOut) {
            this.timeOut = timeOut;
        }

        public int getPackType() {
            return packType;
        }

        public void setPackType(int packType) {
            this.packType = packType;
        }

        public int getTimeSpan() {
            return timeSpan;
        }

        public void setTimeSpan(int timeSpan) {
            this.timeSpan = timeSpan;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public Date getLastPackTime() {
            return lastPackTime;
        }

        public void setLastPackTime(Date lastPackTime) {
            this.lastPackTime = lastPackTime;
        }

        @Override
        public String toString() {
            return "Var{" +
                    "id=" + id +
                    ", varId=" + varId +
                    ", name='" + name + '\'' +
                    ", deviceName='" + deviceName + '\'' +
                    ", type=" + type +
                    ", deviceId=" + deviceId +
                    ", info='" + info + '\'' +
                    ", errorAsNull=" + errorAsNull +
                    ", timeOut=" + timeOut +
                    ", packType=" + packType +
                    ", timeSpan=" + timeSpan +
                    ", value='" + value + '\'' +
                    ", updateTime=" + updateTime +
                    ", lastPackTime=" + lastPackTime +
                    '}';
        }
    }

    /**
     * 设备信息实体bean
     */
    class Device {
        private int id;
        private int deviceId;
        private int deviceOrderId;
        private String name;
        private String addr;
        private int timeOut;
        private int timeSpan;
        private String status;
        private Date lastPackTime;
        private Date updateTime;

        public boolean canPack() {
            if (this.timeSpan == 0) {
                return false;
            }
            if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > timeSpan) {
                return true;
            }
            return false;
        }

        public String pack(String domain, int ioserverid, int groupid) {
            return "http://" + domain + "/" + status + "/" + ioserverid + "/" + groupid + "/" + deviceOrderId + "/" + status + "_" + System.currentTimeMillis();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public int getDeviceOrderId() {
            return deviceOrderId;
        }

        public void setDeviceOrderId(int deviceOrderId) {
            this.deviceOrderId = deviceOrderId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(int timeOut) {
            this.timeOut = timeOut;
        }

        public int getTimeSpan() {
            return timeSpan;
        }

        public void setTimeSpan(int timeSpan) {
            this.timeSpan = timeSpan;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getLastPackTime() {
            return lastPackTime;
        }

        public void setLastPackTime(Date lastPackTime) {
            this.lastPackTime = lastPackTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "id=" + id +
                    ", deviceId=" + deviceId +
                    ", deviceOrderId=" + deviceOrderId +
                    ", name='" + name + '\'' +
                    ", addr='" + addr + '\'' +
                    ", timeOut=" + timeOut +
                    ", timeSpan=" + timeSpan +
                    ", status='" + status + '\'' +
                    ", lastPackTime=" + lastPackTime +
                    ", updateTime=" + updateTime +
                    '}';
        }
    }

    class Package {
        private int id;
        private String name;
        private int errorAsNull;
        private int timeOut;
        // 打包类型：0:间隔时间打包；1：按分钟；2：每15分钟；3：每小时；4每天
        private int packType;
        private int timeSpan;
        private List<Integer> varIdList;
        private List<Var> varList;
        private Date lastPackTime = new Date();

        // 是否可以打包
        public boolean canPack() {
            if (this.timeSpan == 0) {
                return false;
            }
            if (packType == 0) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > timeSpan) {
                    return true;
                }
            } else if (packType == 1) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 60) {
                    return true;
                }
            } else if (packType == 2) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 15 * 60) {
                    return true;
                }
            } else if (packType == 3) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 60) {
                    return true;
                }
            } else if (packType == 4) {
                if ((System.currentTimeMillis() - lastPackTime.getTime()) / 1000 > 24 * 60) {
                    return true;
                }
            }
            return false;
        }

        public List<String> pack(String domain, int ioserverid, int groupid) {
            List<String> msglist = new ArrayList<>();
            for (Var item : varList) {
                String msg = "http://" + domain + "/" + ioserverid + "/" + groupid + "/" + item.pack();
                msglist.add(msg);
            }
            this.lastPackTime = new Date();
            return msglist;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getErrorAsNull() {
            return errorAsNull;
        }

        public void setErrorAsNull(int errorAsNull) {
            this.errorAsNull = errorAsNull;
        }

        public int getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(int timeOut) {
            this.timeOut = timeOut;
        }

        public int getPackType() {
            return packType;
        }

        public void setPackType(int packType) {
            this.packType = packType;
        }

        public int getTimeSpan() {
            return timeSpan;
        }

        public void setTimeSpan(int timeSpan) {
            this.timeSpan = timeSpan;
        }

        public List<Integer> getVarIdList() {
            return varIdList;
        }

        public void setVarIdList(List<Integer> varIdList) {
            this.varIdList = varIdList;
        }

        public List<Var> getVarList() {
            return varList;
        }

        public void setVarList(List<Var> varList) {
            this.varList = varList;
        }

        public Date getLastPackTime() {
            return lastPackTime;
        }

        public void setLastPackTime(Date lastPackTime) {
            this.lastPackTime = lastPackTime;
        }

        @Override
        public String toString() {
            return "Package{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", errorAsNull=" + errorAsNull +
                    ", timeOut=" + timeOut +
                    ", packType=" + packType +
                    ", timeSpan=" + timeSpan +
                    ", varIdList=" + varIdList +
                    ", varList=" + varList +
                    ", lastPackTime=" + lastPackTime +
                    '}';
        }
    }

}
