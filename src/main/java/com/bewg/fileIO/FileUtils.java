package com.bewg.fileIO;

import com.bewg.entity.Constant;
import com.bewg.entity.FileConfigStall;
import com.bewg.entity.Message;
import com.bewg.utils.CommontUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 读写文件
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Slf4j
@Data
public class FileUtils {


    /**
     * 保存配置文件
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public  boolean saveConfigInfo( FileConfigStall fileConfigStall) throws IOException {
        String filePath=fileConfigStall.getFilePath();
        File logFiles = new File(filePath);
        if(!logFiles.exists()){
            // 如果文件不存在 就去创建
            logFiles.mkdirs();
        }
        String allPath=filePath+File.separator+fileConfigStall.getFileName();
        File allFile = new File(allPath);
        if (!allFile.exists()){
            boolean ok= allFile.createNewFile();
            System.out.println("fileConfigStall = [" + ok + "]");
        }
        FileWriter fileWriter= new FileWriter(allPath);
        ObjectOutputStream outputStream=null;
        try {
            outputStream= new ObjectOutputStream(new FileOutputStream(allPath));
            outputStream.writeObject(fileConfigStall);
            outputStream.flush();
            log.info("配置信息保存到 >>> {} <<< 文件中！",allFile);
            return true;
        } catch (Exception e) {
            log.error("配置信息保存失败！ 异常信息是：{}",e.getMessage());
            return false;
        } finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("保存 配置信息文件完成后，输出流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
    }

    /**
     * 保存对象配置
     * @author zcy
     * @date
     * @param
     * @return
     */
    public  boolean saveConfigInfo( String path,String filename,Object t) throws IOException {
        File logFiles = new File(path);
        if(!logFiles.exists()){
            // 如果文件不存在 就去创建
            logFiles.mkdirs();
        }
        String allPath=path+File.separator+filename;
        File allFile = new File(allPath);
        ObjectOutputStream outputStream=null;
        try {
            outputStream= new ObjectOutputStream(new FileOutputStream(allPath));
            outputStream.writeObject(t);
            outputStream.flush();
            log.info("配置信息保存到 >>> {} <<< 文件中！",allFile);
            return true;
        } catch (Exception e) {
            log.error("配置信息保存失败！ 异常信息是：{}",e.getMessage());
            return false;
        } finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("保存 配置信息文件完成后，输出流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
    }

    /**
     * 读取配置文件
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public Object readObjectInfo(String path,String filename ){
        String _url = path+File.separator+filename;
        log.info("配置文件的 -- 读取 -- 本地文件全路径：{}",_url);
        File files = new File(_url);
        if(!files.exists()){
            log.warn("配置信息不存在！读取失败！");
            return null;
        }
        ObjectInputStream ois = null;
        Object obj=null;
        try {
            ois= new ObjectInputStream(new FileInputStream(files));
            obj = ois.readObject();
            log.info("配置信息 -- 读取 -- 成功！ 配置信息是：{}",obj);
        } catch (Exception e) {
            System.out.println("fileConfigStall = [" + e.getMessage() + "]");
            log.error("配置信息 -- 读取 -- 失败！ 异常信息是：{}",e.getMessage());
        } finally {
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    log.error("读取 配置信息文件完成后，输入流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
        return obj;
    }

    public boolean stallData(String fileName,String path,String value) throws IOException {
        File logFiles = new File(path);
        if(!logFiles.exists()){
            // 如果文件不存在 就去创建
            logFiles.mkdirs();
        }
        String allPath=path+File.separator+fileName;
        File allFile = new File(allPath);
        BufferedOutputStream outputStream=null;
        try {
            outputStream= new BufferedOutputStream(new FileOutputStream(allPath));
            outputStream.write(value.getBytes());
            outputStream.flush();
            log.info("配置信息保存到 >>> {} <<< 文件中！",allFile);
            return true;
        } catch (Exception e) {
            log.error("配置信息保存失败！ 异常信息是：{}",e.getMessage());
            return false;
        } finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("保存 配置信息文件完成后，输出流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
    }

    public String readDataLi(String fileName,String path) throws FileNotFoundException {
        String _url = path+File.separator+fileName;
        log.info("配置文件的 -- 读取 -- 本地文件全路径：{}",_url);
        File files = new File(_url);
        if(!files.exists()){
            log.warn("配置信息不存在！读取失败！");
            return null;
        }
        List<String> list= new ArrayList<>();
        BufferedInputStream bufferedInputStream=null;
        String value=null;
        try {
            FileInputStream input = new FileInputStream(new File(_url));
            bufferedInputStream= new BufferedInputStream(input);
            byte[] b=new byte[input.available()];
            bufferedInputStream.read(b);
            value=new String(b);
            log.info("配置信息 -- 读取 -- 成功！ 配置信息是：{}",value);
        } catch (Exception e) {
            log.error("配置信息 -- 读取 -- 失败！ 异常信息是：{}",e.getMessage());
        } finally {
            if(bufferedInputStream != null){
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    log.error("读取 配置信息文件完成后，输入流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
        return value;
    }

    /**
     * 读取配置文件
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public List<FileConfigStall> raadLoginInfo( FileConfigStall fileConfigStall){
        String _url = fileConfigStall.getFilePath()+File.separator+fileConfigStall.getFileName();
        log.info("配置文件的 -- 读取 -- 本地文件全路径：{}",_url);
        File files = new File(_url);
        if(!files.exists()){
            log.warn("配置信息不存在！读取失败！");
            return null;
        }
        FileConfigStall configInfo = new FileConfigStall();
        List<String> list= new ArrayList<>();
        ObjectInputStream ois = null;
        FileConfigStall obj=null;
        List<FileConfigStall> li= new ArrayList<>();
        try {
            FileInputStream input = new FileInputStream(new File(_url));
            byte[] b=new byte[input.available()];
            ois= new ObjectInputStream(input);
            while (input.available()>0){
                obj = (FileConfigStall)ois.readObject();
                li.add(obj);
                log.info("配置信息 -- 读取 -- 成功！ 配置信息是：{}",obj.getValue());
            }
        } catch (Exception e) {
            log.error("配置信息 -- 读取 -- 失败！ 异常信息是：{}",e.getMessage());
        } finally {
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    log.error("读取 配置信息文件完成后，输入流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
        return li;
    }

    public static void main(String[] args) throws IOException {

        FileUtils fileUtils=new FileUtils();

        String object = fileUtils.readDataLi(Constant.MACFileName,Constant.MACConfgPath);
        System.out.println(CommontUtils.byte2Object(object,new Message()).getSource_mac());
    }
}
