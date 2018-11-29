package com.bewg.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zcy
 * @date 2018/11/611:35
 * 存储实用类
 */
@Data
public class FileConfigStall implements Serializable {
    //文件路径
    private String filePath;

    //文件名
    private String fileName;

    //数据
    private String value;
}
