package com.bewg.utils.config;

public class ChangeServerStatus {

    /**
     * 物联网接口服务的服务状态变化关系
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public CollectServerStatusEnum change(boolean b){

        CollectServerStatusEnum serverstatus = CollectServerStatusEnum.START_UP;

        switch (serverstatus){
            case START_UP:
                serverstatus = CollectServerStatusEnum.READ_LOCAL_CONFIGURATION;
                break;
            case READ_LOCAL_CONFIGURATION:
                if(b){
                    serverstatus = CollectServerStatusEnum.SERVICE_START;
                }else {
                    serverstatus = CollectServerStatusEnum.FAILED_READ_LOCAL_CONFIGURATION;
                }
                break;
            case FAILED_READ_LOCAL_CONFIGURATION:
                serverstatus = CollectServerStatusEnum.WAITING_FOR_WEB_CONNECTION;
                break;
            case WAITING_FOR_WEB_CONNECTION:
                if(b){
                    serverstatus = CollectServerStatusEnum.CONNECT_TO_WEB;
                }
                break;
            case CONNECT_TO_WEB:
                serverstatus = CollectServerStatusEnum.WAITING_FOR_DISPATCH_CONFIGURATION;
                break;
            case WAITING_FOR_DISPATCH_CONFIGURATION:
                if(b){
                    serverstatus = CollectServerStatusEnum.CONFIGURATION_SUCCESS;
                }
                break;
            case CONFIGURATION_SUCCESS:
                if (b){
                    serverstatus = CollectServerStatusEnum.SERVICE_START;
                }else {
                    serverstatus = CollectServerStatusEnum.SERVICE_FAILED;
                }
                break;
            default:
                break;
        }
        return serverstatus;
    }
}
