package com.test;

import com.alibaba.fastjson2.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class test {

    @Test
    public void testGetServerInfo(){
        System.out.println(GetUrl.getServer());
    }

    @Test
    public void jsonTest(){
        String jsString = "{\"status\":10001,\"code\":\"HtpmUh\",\"pubkey\":\"DBCEA86ACD310CC0ED8A56D9E3C3CFE26951E8A6C0AC103419B43617C410B0537B13E7D145AB007E61BB39CB66854A4AA9BABD108BD93212376CD9A61A03B80B03D54D760F8FD317C784AE1B8489A2D3890ABCF3F73946EEBF7CF433BB4C53526DE29F4CFECF07F3C95CF2A95BF140EE605F695FF0889EECFD3F6808C85254B1\",\"servertime\":1695800871}\n";
        JSONObject js1 = JSONObject.parseObject(jsString);
        js1.put("Test","test001");
        System.out.println(js1);
        System.out.println("test属性的值为："+js1.get("Test"));
        System.out.println("status属性的值为："+js1.get("status"));
        System.out.println("更新后的json字符串：" + js1);
        Assert.assertEquals(1,1);
        Assert.assertEquals("abc","abc");
        Assert.fail("1234567");
    }
}
