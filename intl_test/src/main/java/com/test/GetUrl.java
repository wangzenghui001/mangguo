package com.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class GetUrl {
    //读取property文件
    public static Properties getPropertyFile(){
        Properties properties = new Properties();
        try{
            InputStream ins = new FileInputStream("src/main/resources/application.properties");
            properties.load(ins);
        }catch (Exception e){
            e.printStackTrace();
        }
        return properties;
    }

    public static String getServer(){
        return getPropertyFile().getProperty("serverName");
    }
}
