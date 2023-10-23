package com.es.springbootelasticsearch.service.serviceImpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.es.springbootelasticsearch.dto.User;
import com.es.springbootelasticsearch.service.IDocuemtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentServiceImpl implements IDocuemtService {

    Logger logger=  LoggerFactory.getLogger(DocumentServiceImpl.class);
    @Autowired
    private ElasticsearchClient client;
    @Override
    public void createDocument(String indexName,String indexId,Object obj) throws IOException {

        CreateRequest<User> createRequest = new CreateRequest.Builder<User>()
                .index(indexName)
                .id(indexId)
                .document((User) obj)
                .build();
        CreateResponse response = client.create(createRequest);
        logger.info("文档创建成功");
        logger.info("文档信息："+response);

    }

    @Override
    public void batchCreateDocument(List<BulkOperation> list) throws IOException {

        BulkRequest bulkRequest = new BulkRequest.Builder()
                .operations(list)
                .build();
        BulkResponse bulk = client.bulk(bulkRequest);
        logger.info("批量添加文档数据成功");
        logger.info("文档信息："+bulk);
    }

    @Override
    public void deleteByName(String indexName,String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest.Builder()
                .index(indexName)
                .id(id)
                .build();
        client.delete(deleteRequest);
        logger.info("数据删除成功");
    }

    @Override
    public SearchResponse<Object> queryDocument(String content,String condition) throws IOException {
        MatchQuery matchQuery = new MatchQuery.Builder()
                //匹配内容
                .field(content)
                //条件
                .query(condition)
                .build();
        Query query = new Query.Builder()
                .match(matchQuery)
                .build();
        SearchRequest searchRequest = new SearchRequest.Builder()
                .query(query)
                .build();
         return client.search(searchRequest, Object.class);

    }
}
