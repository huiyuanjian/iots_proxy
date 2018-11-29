package com.bewg.logger;

import com.bewg.logger.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class LogUtil {

    @Value("${module.name}")
    private String moduleName;

    /*
     * @Author zcy
     * @Description //TODO 获取目录下文件列表
     * @Date 16:57 2018/11/8
     * @Param 
     * @return 
     **/
    
    public List<String> getChild( File file, List<String> list,String identify,boolean isupload){
        File[] tempList = file.listFiles();
        if(tempList == null) return null;
        for (int i = 0; i < tempList.length; i++) {
            if(isupload){
                if (tempList[i].isFile()&&tempList[i].getName().indexOf(identify)==-1) {
                    list.add(tempList[i].toString());
                    log.info("文     件：{}",tempList[i]);
                }
            }else{
                if (tempList[i].isFile()&&tempList[i].getName().indexOf(identify)>0) {
                    list.add(tempList[i].toString());
                    log.info("文     件：{}",tempList[i]);
                }
            }
            if (tempList[i].isDirectory()) {
                System.out.println("文件夹："+tempList[i]);
                //List<String> list_children = new ArrayList<>();
                list = getChild(tempList[i],list,identify,isupload);
            }
        }
        //检查文件名 是不是 最新的正在占用的
        if(identify.equals("--Already")) list = checkedFileName(list);
        return list;
    }

    /*
     * @Author zcy
     * @Description //TODO 检查文件名 是不是 最新的正在占用的
     * @Date 14:04 2018/11/8
     * @Param
     * @return
     **/
    private List<String> checkedFileName(List<String> list) {
        List<Integer> indexLi=new ArrayList<>();
        String newDate=DateUtil.parseDateToStr(new Date(),"yyyy-MM-dd");
        String path=null;
        for (int i=0; i<list.size();i++){//F:\IOTS\IOTs_Proxy\IOTs_Proxy_2018-11-08.0.log
            String tempName=list.get(i);
            //解析
            path=tempName.substring(0,tempName.lastIndexOf(moduleName));
            String dateTime=tempName.substring(tempName.lastIndexOf(moduleName+"_")+moduleName.length()+1,tempName.indexOf("."));
            String index=tempName.substring(tempName.indexOf(".")+1,tempName.lastIndexOf("."));
            if (newDate.equals(dateTime)){
                indexLi.add(Integer.parseInt(index));
            }
        }
        Integer maxIndel=0;
        String filename=null;
        //查出集合中最大的是哪个
        if (!CollectionUtils.isEmpty(indexLi)){
            maxIndel = Collections.max(indexLi);
            filename=path+moduleName+"_"+newDate+"."+maxIndel+".log";
        }
        if (!StringUtils.isEmpty(filename)){
            list.remove(filename);
        }
        return list;
    }

    public static void main(String[] args) {
       List list= new ArrayList();
       list.add("F:\\IOTS\\IOTs_Proxy\\IOTs_Proxy_2018-11-08_0.log");
       list.remove("F:\\IOTS\\IOTs_Proxy\\IOTs_Proxy_2018-11-08_0.log");
        System.out.println(list.size());
    }

    /**
     * 文件重命名
     * @param orgFile
     */
    public void reanameFile(String orgFile,String identify){
        if (!StringUtils.isEmpty(orgFile)){
            String orgName= orgFile.substring(0,orgFile.lastIndexOf("."))+identify+orgFile.substring(orgFile.lastIndexOf("."));
            File fileorg= new File(orgFile);
            File fileNew= new File(orgName);
            boolean isok =fileorg.renameTo(fileNew);
            log.info(" 修改成功？？？？？"+isok);
        }
    }

    /**
     * 文件删除
     * @param orgFile
     */
    public void deleteFile(String orgFile){
        if (!StringUtils.isEmpty(orgFile)){
            File fileorg= new File(orgFile);
            boolean isok = fileorg.delete();
            log.info(" 删除成功？？？？？"+isok);
        }
    }


}
