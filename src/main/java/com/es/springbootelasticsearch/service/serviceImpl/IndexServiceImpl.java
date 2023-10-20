package com.es.springbootelasticsearch.service.serviceImpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.*;
import com.es.springbootelasticsearch.service.IIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service

public class IndexServiceImpl implements IIndexService {
    Logger logger=  LoggerFactory.getLogger(IndexServiceImpl.class);
    @Autowired
    private ElasticsearchClient client;
    @Override
    public Boolean indexIsExists(String indexName) throws IOException {
        boolean bool = client.indices().exists(e -> e.index(indexName)).value();
        return bool;
    }

    @Override
    public Boolean createIndex(String indexName) throws IOException {
        if(indexIsExists(indexName)){
            logger.info("索引存在，无法创建");
            return false;
        }
        //通过构建器方式来构建对象
        CreateIndexRequest request = new CreateIndexRequest.Builder().index(indexName).build();
        CreateIndexResponse createIndexResponse = client.indices().create(request);
        //判断索引是否创建成功
        boolean acknowledged = createIndexResponse.acknowledged();
        logger.info("索引创建状态："+acknowledged);
        return acknowledged;
    }

    @Override
    public Object findIndexByName(String indexName) throws IOException {
        if(!indexIsExists(indexName)){
            logger.info(indexName+": 该索引不存在");
            return false;
        }
        GetIndexRequest getIndexRequest = new GetIndexRequest.Builder().index(indexName).build();
        GetIndexResponse response = client.indices().get(getIndexRequest);
        return response;
    }

    @Override
    public Boolean deleteIndexByName(String indexName) throws IOException {
        if(!indexIsExists(indexName)){
            logger.info("删除失败；"+indexName+": 该索引不存在");
            return false;
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(indexName).build();
        DeleteIndexResponse delete = client.indices().delete(deleteIndexRequest);
        logger.info(indexName+": 索引删除成功");
        return delete.acknowledged();
    }
}
