package com.intl.test;

import com.intl.test.Entity.TestCase;
import com.intl.test.Entity.TestResult;
import com.intl.test.utils.MybatisUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ExecuteTestFromDB {
    //获取测试用例
    public List<TestCase> getTestCase(){
        List<TestCase> testCaseList = new ArrayList<>();
        SqlSession sqlSession = null;
        try{
            sqlSession = MybatisUtils.openSession();
            testCaseList = sqlSession.selectList("testcase.selectByRunFlag", "Y");
            System.out.println("case_id case_name   server  uri request_header  request_body    expected_response   method  run_flag");
            for(TestCase testCase:testCaseList){
                System.out.println(testCase.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            sqlSession.close();
        }
        return testCaseList;
    }

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
    public List<TestResult> executeTestCase(){
        List<TestCase> testCaseList = this.getTestCase();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        List<TestResult> testResultList = new ArrayList<>();
        String responseContent;
        if(testCaseList != null){
            System.out.println("总共" + testCaseList.size() + "条测试用例");
            for(TestCase testCase:testCaseList){
                TestResult testResult = new TestResult();
                testResult.setCaseId(testCase.getCaseId());
                testResult.setCaseName(testCase.getCaseName());
                testResult.setServer(testCase.getServer());
                testResult.setUri(testCase.getUri());
                testResult.setRequestHeader(testCase.getRequestHeader());
                testResult.setRequestBody(testCase.getRequestBody());
                testResult.setExpectedResponse(testCase.getExpectedResponse());
                testResult.setRequestMethod(testCase.getRequestMethod());
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
                    testResult.setActualResponse("接口返回为空");
                    testResult.setTestResult("Failed");
                }
                if(testResult != null){
                    testResultList.add(testResult);
                }else{
                    System.out.println("testResult为空");
                }
            }
        }
        return testResultList;
    }

    public void insertTestResult(List<TestResult> testResultList){
        SqlSession sqlSession = null;
        try{
            sqlSession = MybatisUtils.openSession();
            int num = sqlSession.insert("testResult.batchInsert", testResultList);
            sqlSession.commit();
            if(num == testResultList.size()){
                System.out.println("测试用例执行结果插入成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            sqlSession.close();
        }
    }

    @Test
    public void runTest(){
        this.insertTestResult(executeTestCase());
    }
}
