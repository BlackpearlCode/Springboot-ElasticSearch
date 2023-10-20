package com.es.springbootelasticsearch.service;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;

import java.io.IOException;
import java.util.List;

public interface IDocuemtService {

    /**
     * 单个添加文档数据
     * @param indexName：给文档设置索引名称
     * @param indexId：设置唯一性标识
     * @param obj：文档对象
     * @return
     * @throws IOException
     */
    void createDocument(String indexName,String indexId,Object obj) throws IOException;


    /**
     * 批量添加文档数据
     * @param list：批量文档数据
     * @throws IOException
     */
    void batchCreateDocument(List<BulkOperation> list) throws IOException;

    /**
     * 根据索引名称和索引ID删除数据
     * @param indexName：索引名称
     * @param id：索引ID
     * @return
     * @throws IOException
     */
    void deleteByName(String indexName,String id) throws IOException;


}