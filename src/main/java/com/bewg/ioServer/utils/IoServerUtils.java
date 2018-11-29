package com.bewg.ioServer.utils;

import com.ioserver.bean.*;
import com.ioserver.dll.IOServerAPICilent;
import com.sun.jna.WString;

import java.util.List;

/**
 * @author zcy
 * @date 2018/10/3115:04
 * IOserver  工具类
 */
public class IoServerUtils {

    private static IOServerAPICilent client=null;

    public IoServerUtils(IOServerAPICilent clienttt) {
        client =clienttt;
    }

    /// <summary>
/// 连接ioserver
/// </summary>
/// <param name="ip">
/// IOServer运行服务器IP
/// </param>
/// <param name="port">
/// IOServer工程端口号
/// </param>
/// <returns>
/// 表示连接是否成功：true表示成功，false表示失败
/// </returns>
/// <remarks>
/// 端口号可在工程设计网络配置中查看与配置
/// </remarks>
    public  boolean IOServerConnecton(String ip,int port){
        return client.IOServerConnecton(ip,port);
    }
    /// <summary>
/// 断开客户端与ioserver的连接
/// </summary>
/// <param name="Handle">
/// IOServer连接句柄
/// </param>
/// <returns>
    /// 表示是否断开：0表示成功，-1表示失败
/// </returns>
/// <remarks>
/// 句柄可以从客户端getHandle方法获取
/// </remarks>
    public  int IOServerDisConnect(int Handle){
        return  client.IOServerDisConnect(Handle);
    }
/// <summary>
/// 获取设备状态
/// </summary>
/// <param name="Handle">
/// IOServer连接句柄
/// </param>
/// <param name="DeviceName">
/// 设备名称
/// </param>
/// <returns>
/// 0：表示设备正常；1：表示系统控制挂起；2：表示设备故障 -1:表示读取失败
    /// </returns>
/// <remarks>
/// 设备名称也就是在ioserver结构树上通道下面的设备名称，是唯一的
/// </remarks>
    public   int GetDeviceWorkStatus(int Handle,String DeviceName){
        return client.GetDeviceWorkStatus(Handle,DeviceName);
    }
    /// <summary>
/// 订阅变量
/// </summary>
/// <param name="Handle">
/// IOServer连接句柄
/// </param>
/// <param name="TagIDs">
/// 变量ID数组
/// </param>
/// <param name="length">
/// 数组长度
/// </param>
/// <returns>
/// 0：订阅成功，其他表示失败
/// </returns>
/// <remarks>
/// 订阅之后需要注册回调函数才可以回去订阅的值
/// </remarks>
    public   int SubscribeTagValuesChange(int Handle,int[] TagIDs,int length){
        return  client.SubscribeTagValuesChange(Handle,TagIDs,length);
    }
    /// <summary>
/// 以变量ID同步读取变量值
/// </summary>
/// <param name="Handle">
/// IOServer连接句柄
/// </param>
/// <param name="TagIDs">
/// 变量ID数组
/// </param>
/// <param name="length">
/// 数组长度
/// </param>
/// <param name="DataSource">
/// 数据源：缓存与设备
/// </param>
/// <returns>
/// 读取的变量值数组
/// </returns>
/// <remarks>
/// Struct_TagInfo是模拟c++变量结构体。此方法为批量同步读值
/// </remarks>
    public   Struct_TagInfo[] SyncReadTagsValueByIDs(int Handle, int[] TagIDs, int length, int DataSource){
        return client.SyncReadTagsValueByIDs(Handle,TagIDs,length,DataSource);
    }
    /// <summary>
/// 以变量名称同步读取变量值
/// </summary>
/// <param name="Handle">
/// IOServer连接句柄
/// </param>
/// <param name="TagNames">
/// 变量名称数组
/// </param>
/// <param name="length">
/// 数组长度
/// </param>
/// <param name="DataSource">
/// 数据源：缓存与设备
/// </param>
/// <returns>
/// 读取的变量值数组
/// </returns>
/// <remarks>
/// Struct_TagInfo是模拟c++变量信息结构体类。此方法为批量同步读值
/// </remarks>
    public   Struct_TagInfo[] SyncReadTagsValueByNames(int Handle, WString[] TagNames, int length, int DataSource){
        return client.SyncReadTagsValueByNames(Handle,TagNames,length,DataSource);
    }
    /// <summary>
/// 以变量ID异步读取变量值
/// </summary>
/// <param name="Handle">
/// IOServer连接句柄
/// </param>
/// <param name="TagNames">
/// 变量ID数组
/// </param>
/// <param name="length">
/// 数组长度
/// </param>
/// <param name="DataSource">
/// 数据源：缓存与设备
/// </param>
/// <returns>
/// 读取请求成功执行与否。0表示成功，其他表示失败
/// </returns>
/// <remarks>
/// 此为批量异步读取变量值，返回结果在读完成回调接口的实现中
/// </remarks>
    public   int AsyncReadTagsValueByIDs(int Handle,int[] TagIDs,int length,int DataSource){
        return  client.AsyncReadTagsValueByIDs(Handle,TagIDs,length,DataSource);
    }

    /// <summary>
    /// 		以变量名称同步读取变量值
    /// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name="TagNames">
    ///		变量名称数组
/// </param>
/// <param name="length">
    ///		数组长度
/// </param>
/// <param name="DataSource">
    ///		数据源：缓存与设备
/// </param>
/// <returns>
    ///		读取的变量值数组
/// </returns>
/// <remarks>
    ///     此为批量异步读取变量值，返回结果在读完成回调接口的实现中
/// </remarks>
    public int AsyncReadTagsValueByNames(int Handle, WString[] TagNames,int length,int DataSource){
        return client.AsyncReadTagsValueByNames(Handle,TagNames, length, DataSource);
    }
    /// <summary>
    /// 		以变量名称同步读取变量值
    /// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name="TagNames">
    ///		变量名称数组
/// </param>
/// <param name="length">
    ///		数组长度
/// </param>
/// <param name="DataSource">
    ///		数据源：缓存与设备
/// </param>
/// <returns>
    ///		读取的变量值数组
/// </returns>
/// <remarks>
///     Struct_TagInfo_AddName是在Struct_TagInfo基础上添加了变量的名称。此方法为批量同步读值
/// </remarks>
    public Struct_TagInfo_AddName[] SyncReadTagsValueReturnNames( int Handle,WString[] TagNames,int length,int DataSource){
        return client.SyncReadTagsValueReturnNames( Handle, TagNames, length, DataSource);
    }
    /// <summary>
    /// 		以变量ID同步写入变量值
    /// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name="valuelist ">
    ///		要写入的变量值数组
/// </param>
/// <param name="TagIDs">
    ///		变量ID数组
/// </param>
/// <param name="length">
    ///		数组长度
/// </param>
/// <returns>
    ///		写入成功与否：0表示写入成功，其他表示失败
/// </returns>
/// <remarks>
///     采用add方式向valuelist存程序，此为批量写入
/// </remarks>
    public  int SyncWriteTagsValueByIDs(int Handle, List valuelist, int[] TagIDs){
        return client.SyncWriteTagsValueByIDs( Handle,  valuelist, TagIDs);
    }
    /// <summary>
    /// 		以变量名称同步写入变量值
    /// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name=" valuelist ">
    ///		要写入的变量值数组
/// </param>
/// <param name="TagNames">
    ///		变量名称数组
/// </param>
/// <param name="length">
    ///		数组长度
/// </param>
/// <returns>
    ///		写入成功与否：0表示写入成功，其他表示失败
/// </returns>
/// <remarks>
///     valuelist是模拟c++变量值结构体的类，此为批量写入
/// </remarks>
    public int SyncWriteTagsValueByNames(int Handle,  List valuelist,WString[] TagNames){
        return client.SyncWriteTagsValueByNames( Handle,   valuelist, TagNames);
    }
/// <summary>
            /// 		以变量ID异步写入变量值
            /// </summary>
/// <param name="Handle">
            ///		IOServer连接句柄
/// </param>
/// <param name=" valuelist ">
            ///		要写入的变量值数组
/// </param>
/// <param name="TagIDs">
            ///		变量ID数组
/// </param>
/// <param name="length">
            ///		数组长度
/// </param>
/// <returns>
            ///		写入请求成功与否：0表示请求成功，其他表示失败
/// </returns>
/// <remarks>
///     valuelist是模拟c++变量值结构体的类，此为批量写入写入成功的结果在写完成回调接口的实现类中显示
    /// </remarks>
    public int AsyncWriteTagsValueByIDs(int Handle, List valuelist,int[] TagIDs){
        return client.AsyncWriteTagsValueByIDs( Handle, valuelist, TagIDs);
    }
/// <summary>
            /// 		以变量名称异步写入变量值
            /// </summary>
/// <param name="Handle">
            ///		IOServer连接句柄
/// </param>
/// <param name=" valuelist ">
            ///		要写入的变量值数组
/// </param>
/// <param name="TagIDs">
            ///		变量名称数组
/// </param>
/// <param name="length">
            ///		数组长度
/// </param>
/// <returns>
            ///		写入请求成功与否：0表示请求成功，其他表示失败
/// </returns>
/// <remarks>
///     valuelist是模拟c++变量值结构体的类，此为批量写入写入成功的结果在写完成回调接口的实现类中显示
    /// </remarks>
    public int AsyncWriteTagsValueByNames(int Handle,
                                          List valuelist,WString[] TagNames){
        return client.AsyncWriteTagsValueByNames(Handle,valuelist,TagNames);
    }
    /// <summary>
    /// 		注册连接状态变化回调接口
    /// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <returns>
    ///		注册成功与否
/// </returns>
/// <remarks>
///     使用默认的回调接口实现
/// </remarks>
    public short RegisterConnectStatusChangedCallbackFunc(
            int Handle){
        return  client.RegisterConnectStatusChangedCallbackFunc(Handle);
    }
    /// <summary>
    /// 		注册ioserver工作状态变化回调接口
    /// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <returns>
    ///		注册成功与否
/// </returns>
/// <remarks>
///     使用默认的回调接口实现
/// </remarks>
    public short RegisterWorkStatusChangedCallbackFunc(
            int Handle){
        return client.RegisterWorkStatusChangedCallbackFunc(Handle);
    }
    /// <summary>
    /// 		注册订阅变量值变化回调接口
    /// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <returns>
    ///		注册成功与否
/// </returns>
/// <remarks>
///     使用默认的回调接口实现
/// </remarks>
    public short RegisterCollectValueCallbackFunc(
            int Handle){
        return client.RegisterCollectValueCallbackFunc(Handle);
    }
    /// <summary>
    /// 		注册读完成回调接口
/// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <returns>
    ///		注册成功与否
/// </returns>
/// <remarks>
///     使用默认的回调接口实现
/// </remarks>
    public short RegisterReadCompleteCallbackFunc(
            int Handle){
        return client.RegisterWriteCompleteCallBackFunc(Handle);
    }
    /// <summary>
    /// 		注册写完成回调接口
/// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <returns>
    ///		注册成功与否
/// </returns>
/// <remarks>
///     使用默认的回调接口实现
/// </remarks>
    public short RegisterWriteCompleteCallBackFunc(
            int Handle){
        return client.RegisterWriteCompleteCallBackFunc(Handle);
    }
    /// <summary>
    /// 		浏览所有的工程属性
/// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name=" Mask ">
    ///		节点名称，空表示根节点
/// </param>
/// <returns>
    ///		工程属性数组
/// </returns>
/// <remarks>
///     Struct_IOServerProperty是模拟c++工程属性结构体的类，mask一般为空
/// </remarks>
    public Struct_IOServerProperty[] BrowserProjects(int Handle, WString Mask){
        return client.BrowserProjects(Handle,Mask);
    }
    /// <summary>
    /// 		浏览工程下的通道属性
/// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name=" Mask ">
    ///		工程名称，空表示浏览所有工程的通道
/// </param>
/// <returns>
    ///		通道属性数组
/// </returns>
/// <remarks>
///    Struct_ChannelProperty是模拟c++通道属性结构体的类，mask一般为空
/// </remarks>
    public Struct_ChannelProperty[] BrowserChannels(int Handle, WString Mask){
        return client.BrowserChannels(Handle,Mask);
    }
    /// <summary>
    /// 		浏览通道下设备属性
/// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name=" Mask ">
    ///		通道名称，空表示浏览所有通道下设备属性
/// </param>
/// <returns>
    ///		设备属性数组
/// </returns>
/// <remarks>
///     Struct_DeviceProperty是模拟c++工程属性结构体的类
/// </remarks>
    public Struct_DeviceProperty[] BrowserDevices(int Handle, WString Mask){
        return  client.BrowserDevices(Handle,Mask);
    }
    /// <summary>
    /// 		浏览设备下的变量属性
/// </summary>
/// <param name="Handle">
    ///		IOServer连接句柄
/// </param>
/// <param name=" Mask ">
    ///		设备名称，空表示浏览所有设备下的变量属性
/// </param>
/// <returns>
    ///		变量属性数组
/// </returns>
/// <remarks>
///     Struct_TagProperty是模拟c++变量属性结构体的类
/// </remarks>
    public Struct_TagProperty[] BrowserCollectTags(int Handle, WString Mask){
        return client.BrowserCollectTags(Handle,Mask);
    }
}
