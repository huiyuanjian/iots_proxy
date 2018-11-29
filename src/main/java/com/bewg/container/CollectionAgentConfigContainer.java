package com.bewg.container;

import com.bewg.entity.CommonConfig;

/**
 * 采集配置容器
 */
public class CollectionAgentConfigContainer {

    private CollectionAgentConfigContainer() {
    }

    private static CollectionAgentConfigContainer instance = new CollectionAgentConfigContainer();

    public static CollectionAgentConfigContainer getInstance() {
        return instance;
    }


    // 配置
    private CommonConfig config = new CommonConfig();

    private boolean configEnable = false;

    public CommonConfig getConfig() {
        return config;
    }

    public void setConfig(CommonConfig config) {
        this.config = config;
    }

    public boolean isConfigEnable() {
        return configEnable;
    }

    public void setConfigEnable(boolean configEnable) {
        this.configEnable = configEnable;
    }
}
