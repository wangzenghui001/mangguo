package com.test;

import com.Entity.TestCase;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.util.List;

public class ExecuteTest {
    //post请求方法
    public CloseableHttpResponse runPostRequest(CloseableHttpClient httpClient, TestCase testCase){
        CloseableHttpResponse httpResponse = null;
        String requestUrl = testCase.getServer() + testCase.getUri();
        HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setEntity(new StringEntity(testCase.getRequestBody()));
        try{
            httpResponse = httpClient.execute(httpPost);
        }catch (Exception e){
            e.printStackTrace();
        }
        return httpResponse;
    }

    //get请求方法
    public CloseableHttpResponse runGetRequest(CloseableHttpClient httpClient, TestCase testCase){
        CloseableHttpResponse httpResponse = null;
        String requestUrl = testCase.getServer() + testCase.getUri();
        HttpGet httpGet = new HttpGet(requestUrl);
        try{
            httpResponse = httpClient.execute(httpGet);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(httpResponse.getEntity());
        return httpResponse;
    }

    //执行测试用例，返回测试用例和执行结果
    public List<TestCase> executeTestCase(){
        List<TestCase> testCaseList = ReadExcelFile.getTestCaseData(ReadExcelFile.getExcelWorksheet());
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        String responseContent;
        if(testCaseList != null){
            System.out.println("总共" + testCaseList.size() + "条测试用例");
            for(TestCase testCase:testCaseList){
                switch(testCase.getRequestMethod()){
                    case "GET":
                        httpResponse = this.runGetRequest(httpClient,testCase);
                        break;
                    case "POST":
                        httpResponse = this.runPostRequest(httpClient,testCase);
                        break;
                    default:
                        System.out.println("接口method不正确！");
                }
                if(httpResponse != null){
                    try{
                       responseContent = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
                       System.out.println(responseContent);
                       testCase.setActualResponse(responseContent);
                       if(testCase.getExpectedResponse().equals(responseContent)){
                           testCase.setTestResult("Pass");
                       }else{
                           testCase.setTestResult("Failed");
                       }
                       httpResponse.close();
                       httpClient.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("接口响应返回空");
                }
            }
        }
        return testCaseList;
    }

    public void generateTestResult(List<TestCase> testCaseList){
        int i = 1;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("测试结果");
        XSSFRow titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("caseId");
        titleRow.createCell(1).setCellValue("caseName");
        titleRow.createCell(2).setCellValue("server");
        titleRow.createCell(3).setCellValue("uri");
        titleRow.createCell(4).setCellValue("requestHeader");
        titleRow.createCell(5).setCellValue("requestBody");
        titleRow.createCell(6).setCellValue("expectResponse");
        titleRow.createCell(7).setCellValue("method");
        titleRow.createCell(8).setCellValue("actualResponse");
        titleRow.createCell(9).setCellValue("testResult");
        for(TestCase testCase:testCaseList){
            XSSFRow row = sheet.createRow(i);
            row.createCell(0).setCellValue(testCase.getCaseId());
            row.createCell(1).setCellValue(testCase.getCaseName());
            row.createCell(2).setCellValue(testCase.getServer());
            row.createCell(3).setCellValue(testCase.getUri());
            row.createCell(4).setCellValue(testCase.getRequestHeader());
            row.createCell(5).setCellValue(testCase.getRequestBody());
            row.createCell(6).setCellValue(testCase.getExpectedResponse());
            row.createCell(7).setCellValue(testCase.getRequestMethod());
            row.createCell(8).setCellValue(testCase.getActualResponse());
            row.createCell(9).setCellValue(testCase.getTestResult());
            i++;
        }
        try{
            FileOutputStream fos = new FileOutputStream("src/main/resources/testResult_"+ System.currentTimeMillis() +".xlsx");
            workbook.write(fos);
            workbook.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void runTestCase(){
        this.generateTestResult(executeTestCase());
    }
}
