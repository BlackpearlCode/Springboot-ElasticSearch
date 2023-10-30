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
     * 多条件查询
     * @param param：筛选条件；key为筛选字段；value:字段对应的属性值
     * @param indexName：索引名称
     * @param sortField：排序字段
     * @param isDesc：是否降序；0：降序；1:升序
     * @param offset：起始页
     * @param pageSize：每页条数
     * @return
     * @throws IOException
     */
    List<Object> queryDocument(Map<String,String> param,String indexName,String sortField,int isDesc,int offset,int pageSize ) throws IOException;

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


    /**
     * 模板搜索
     * @param param
     * @return
     * @throws IOException
     */
    List<Object> templatedSearch(String indexName,String field,String value) throws IOException;
}
