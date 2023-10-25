package com.es.springbootelasticsearch.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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


    /**
     * 根据筛选内容和条件筛选文档型数据
     * @param content：内容
     * @param condition：条件
     * @return
     * @throws IOException
     */
    SearchResponse<Object> queryDocument(String content,String condition) throws IOException;

    /**
     * 判断文档是否存在
     * @param indexName：索引名称
     * @param id：文档id
     * @return
     * @throws IOException
     */
    boolean documentIsExit(String indexName,String id) throws IOException;

    /**
     * 修改指定文档
     * @param indexName：索引名称
     * @param id：文档id
     * @param obj：修改内容
     * @throws IOException
     */
    boolean updateDocument(String indexName, String id, Object obj) throws IOException;

    /**
     * 获取文档信息
     * @param indexName：索引名称
     * @param id：文档id
     * @return
     * @throws IOException
     */
    Object getDocument(String indexName,String id) throws IOException;


    /**
     * 批量删除指定索引下的文档
     * @param indexName：索引名称
     * @param ids：文档id集合
     * @return
     * @throws IOException
     */
    Boolean batchDeletDocument(String indexName,List<String> ids) throws IOException;

    /**
     * 查询指定索引下的所有数据信息
     * @param indexName：索引名称
     * @return
     * @throws IOException
     */
    List<Object> queryAllDocument(String indexName) throws IOException;
}
