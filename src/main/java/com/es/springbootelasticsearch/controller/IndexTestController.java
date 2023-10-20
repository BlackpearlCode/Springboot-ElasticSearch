package com.es.springbootelasticsearch.controller;



import com.es.springbootelasticsearch.service.serviceImpl.IndexServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class IndexTestController {

    @Autowired
    private IndexServiceImpl esService;

    //创建索引索引
    @RequestMapping("/operationIndex")
    public void operationIndex() throws IOException {

    }

    /**
     * 验证索引是否存在
     * @param indexName：索引名称
     * @throws IOException
     */
    @RequestMapping("/IndexIsExists")
    public void IndexIsExists(@Param("indexName")String indexName) throws IOException {
        Boolean isExists = esService.indexIsExists(indexName);
        System.out.println(isExists);
    }

    /**
     * 创建索引
     * @param indexName：索引名称
     * @throws IOException
     */
    @RequestMapping("/createIndex")
    public void createIndex(@Param("indexName")String indexName) throws IOException {
        Boolean isCreate = esService.createIndex(indexName);
        System.out.println(isCreate);
    }

    /**
     * 通过名称查询索引
     * @param indexName：索引名称
     * @throws IOException
     */
    @RequestMapping("/findIndexByName")
    public void findIndexByName(@Param("indexName")String indexName) throws IOException{
        Object indexInfo = esService.findIndexByName(indexName);
        System.out.println(indexInfo.toString());
    }

    /**
     * 更加名称删除索引
     * @param indexName：索引名称
     * @throws IOException
     */
    @RequestMapping("/delIndexByName")
    public void delIndexByName(@Param("indexName")String indexName) throws IOException{
        Boolean bool = esService.deleteIndexByName(indexName);
        System.out.println(bool);
    }

}
