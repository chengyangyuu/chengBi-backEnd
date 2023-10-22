package com.cheng.bibackend.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel相关工具类
 */
@Slf4j
public class ExcelUtils {
    /**
     * Excel转Csv
     * @param multipartFile
     * @return
     */
    public static String ExcelToCsv(MultipartFile multipartFile)  {
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:chartDate.xlsx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //读取文件
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格读取失败",e);
        }
        //list能 读取到  转化为csv
        //没取到就返回空
        if (CollectionUtils.isEmpty(list)){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        //先取表头 原本是Map 但是需要顺序 就变成LinkHashMap就行
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        //.stream过滤掉空值
        List<String> headList = headerMap.values().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headList, ",")).append("\n");
        //按顺序读取
        for (int i =1;i<list.size();i++){
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        System.out.println(list);
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        ExcelToCsv(null);
    }

}
