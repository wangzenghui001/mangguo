package com.test;

import com.Entity.TestCase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadExcelFile {

    public static Sheet getExcelWorksheet(){
        Sheet sheet = null;
        try{
            InputStream fis = new FileInputStream("src/main/resources/testcase.xlsx");
            Workbook workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet("测试用例");
            workbook.close();
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sheet;
    }

    public static List<TestCase> getTestCaseData(Sheet sheet){
        Row row = null;
        String needRun = "Y";
        List<TestCase> testCaseList = new ArrayList<>();
        System.out.println("totally" + sheet.getPhysicalNumberOfRows() + "lines");
        if(sheet != null){
            for(int i = sheet.getFirstRowNum()+1;i <=sheet.getLastRowNum();i++){
                row = sheet.getRow(i);
                if(row != null && needRun.equals(row.getCell(8).getStringCellValue())){
                    System.out.println("totally" + row.getLastCellNum() + "columns" );
                    TestCase testCase = new TestCase();
                    testCase.setCaseId(row.getCell(0).getStringCellValue());
                    testCase.setCaseName(row.getCell(1).getStringCellValue());
                    testCase.setServer(row.getCell(2).getStringCellValue());
                    testCase.setUri(row.getCell(3).getStringCellValue());
                    testCase.setRequestHeader(row.getCell(4).getStringCellValue());
                    testCase.setRequestBody(row.getCell(5).getStringCellValue());
                    testCase.setExpectedResponse(row.getCell(6).getStringCellValue());
                    testCase.setRequestMethod(row.getCell(7).getStringCellValue());
                    //testCase.setRunFlag(row.getCell(8).getStringCellValue());
                    System.out.println("caseId:" + testCase.getCaseId() +"; caseName:" + testCase.getCaseName() +"; expectedResult" + testCase.getExpectedResponse());
                    System.out.println(testCase.toString());
                    if(testCase != null){
                        testCaseList.add(testCase);
                    }
                }else{
                    break;
                }

            }
        }
        return testCaseList;
    }
}
