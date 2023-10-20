package com.es.springbootelasticsearch.service;

import java.io.IOException;

public interface IIndexService {
    /**
     * 方法：判断索引是否存在
     * @param indexName：索引名称
     * @return：true：索引存在；false：索引不存在
     */
    Boolean indexIsExists(String indexName) throws IOException;

    /**
     * 方法：查询索引
     * @param indexName：索引名称
     */
    Boolean createIndex (String indexName) throws IOException;

    /**
     * 根据名称查询索引信息
     * @param indexName：索引名称
     * @return
     * @throws IOException
     */
    Object findIndexByName(String indexName) throws IOException;


    /**
     * 根据名称删除索引
     * @param indexName：索引名称
     * @return
     * @throws IOException
     */
    Boolean deleteIndexByName(String indexName) throws IOException;
}
