package com.bewg.ioServer.utils;

import com.ioserver.bean.Struct_TagInfo;
import com.ioserver.dll.ClientDataBean;

/**
 * @author zcy
 * @date 2018/11/110:53
 * 回调数据存储类
 */
public class IoSCallBackUtils {

    private static  ClientDataBean clientDataBean;

    public IoSCallBackUtils(ClientDataBean clientData) {
        clientDataBean = clientData;
    }

    //回调数据存储就是存储客户端类执行某些操作后的数据：连接状态、工作状态、订阅变量值、异步读变量值、异步写结果
    /// <summary>
    /// 	获取连接状态
    /// </summary>
    /// <returns>
    ///		最新连接状态
    /// </returns>
    /// <remarks>
    ///
    /// </remarks>
    public int getConnectionStatus(){
        return clientDataBean.getConnectionStatus();
    }
    /// <summary>
    /// 	获取ioserver工作状态
    /// </summary>
    /// <returns>
    ///		最新工作状态
    /// </returns>
    /// <remarks>
    ///
    /// </remarks>
    public int getWorkingStatus(){
        return clientDataBean.getWorkingStatus();
    }
    /// <summary>
    /// 	根据ID获取订阅变量变化回调值
    /// </summary>
/// <param name=" TagID ">
    ///		变量ID
/// </param>
    /// <returns>
    ///		变量值类模拟指针
    /// </returns>
    /// <remarks>
    ///
    /// </remarks>
    public static Struct_TagInfo getTagValueByID(Integer TagID){
        Struct_TagInfo Struct_TagInfo = clientDataBean.getTagValueByID(TagID);
        return Struct_TagInfo;
    }
    /// <summary>
    /// 	根据ID获取读完成变量变化回调值
    /// </summary>
/// <param name=" TagID ">
    ///		变量ID
/// </param>
    /// <returns>
    ///		变量值类模拟指针
    /// </returns>
    /// <remarks>
    ///
    /// </remarks>
    public	Struct_TagInfo.ByReference getReadComTagValueByID(Integer TagID){
        return clientDataBean.getReadComTagValueByID(TagID);
    }
    /// <summary>
    /// 	根据ID获取写完成是否成功的错误码
    /// </summary>
/// <param name="TagID">
    ///		变量ID
/// </param>
    /// <returns>
    ///		对应变量的错误码；0表示成功，其他表示失败
    /// </returns>
    /// <remarks>
    ///
    /// </remarks>
    public Integer getErrorCodeByID(Integer TagID){
        return clientDataBean.getErrorCodeByID(TagID);
    }
//打印订阅变量值变化回调值：showTagValues
    //打印读完成回调值：showCompleteTagValues
}
