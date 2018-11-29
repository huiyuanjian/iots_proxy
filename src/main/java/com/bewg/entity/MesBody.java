package com.bewg.entity;

import lombok.Data;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * body 消息体
 */
@Data
public class MesBody implements Serializable {

    /**
     * 消息子类型
     */
    private Integer sub_type;

    /**
     * 存放各种消息
     */
    private List<String> context;
}
