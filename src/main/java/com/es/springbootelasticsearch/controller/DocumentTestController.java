package com.es.springbootelasticsearch.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.es.springbootelasticsearch.dto.User;
import com.es.springbootelasticsearch.service.serviceImpl.DocumentServiceImpl;
import com.es.springbootelasticsearch.util.BulkOperationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class DocumentTestController {

    @Autowired
    private DocumentServiceImpl documentService;

    /**
     * 单个添加文档型数据
     * @param indexName
     * @param indexID
     * @throws IOException
     */
    @RequestMapping("/createDocument")
    public void createDocument(@Param("indexName")String indexName,@Param("indexID") String indexID) throws IOException {
        User user=new User(1001,"zhangsan",11);
        documentService.createDocument(indexName,indexID,user);
    }

    /**
     * 批量添加文档型数据
     * @param param: 传入一个索引名称和list数据集
     * @throws IOException
     */
    @RequestMapping("/batchCreateDocument")
    public void batchCreateDocument(@RequestBody Map<String,Object> param) throws IOException {

        BulkOperationUtil util=new BulkOperationUtil();

        List list = util.toBulkOperationList((List) param.get("obj"), param.get("indexName").toString());

        documentService.batchCreateDocument(list);
    }

    /**
     * 根据索引名称和索引ID删除文档型数据
     * @param indexName：索引名称
     * @param id：索引ID
     * @throws IOException
     */
    @RequestMapping("/deleteIndexByNameAndId")
    public void deleteIndexByNameAndId(@Param("indexName")String indexName,@Param("id")String id) throws IOException {
        documentService.deleteByName(indexName,id);
    }

    /**
     * 通过内容和条件查询文档型数据
     * @param content：内容
     * @param condition：条件
     * @throws IOException
     */
    @RequestMapping("/queryDocument")
    public void queryDocument(@Param("content")String content,@Param("condition") String condition) throws IOException {
        SearchResponse<Object> objectSearchResponse = documentService.queryDocument(content, condition);
        System.out.println(objectSearchResponse.toString());
    }
}
