package com.es.springbootelasticsearch.service.serviceImpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.es.springbootelasticsearch.service.IDocuemtService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


@Service
public class DocumentServiceImpl implements IDocuemtService {

    Logger logger=  LoggerFactory.getLogger(DocumentServiceImpl.class);
    @Autowired
    private ElasticsearchClient client;
    @Override
    public void createDocument(String indexName,String indexId,Object obj) throws IOException {

        CreateRequest<?> createRequest = new CreateRequest.Builder<>()
                .index(indexName)
                .id(indexId)
                .document(obj)
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

    @Override
    public boolean documentIsExit(String indexName, String id) throws IOException {
        boolean exists = client.exists(e -> e.
                index(indexName)
                .id(id))
                .value();
        return exists;
    }

    @Override
    public boolean updateDocument(String indexName, String id, Object obj )throws IOException {
        if(!documentIsExit(indexName, id)){
            logger.error("文档不存在");
        }

        UpdateResponse<?> updateResponse = client.update(e -> e
                .index(indexName)
                .id(id)
                .doc(obj),
                obj.getClass());
        String value = updateResponse.result().jsonValue();
        logger.info(value);
        if(value.equals("updated")){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public Object getDocument(String indexName, String id) throws IOException {
        if(!documentIsExit(indexName, id)){
            logger.error("文档不存在");
            return null;
        }
        GetResponse<Object> getResponse= client.get(s -> s.index(indexName).id(id),Object.class);
        return getResponse.source();
    }

    @Override
    public Boolean batchDeletDocument(String indexName, List<String> ids) throws IOException {
        BulkRequest.Builder builder = new BulkRequest.Builder();
        for(String id:ids){
            builder.operations(op ->op

                    .delete(c -> c.id(id)));
        }
        BulkResponse bulkResponse = client.bulk(builder.index(indexName).build());
        //判断删除响应是否报错，true:删除失败；false：删除成功
        if(bulkResponse.errors()){
            return false;
        }
        return true;
    }

    @Override
    public List<Object> queryAllDocument(String indexName) throws IOException {
        SearchResponse<Object> searchResponse = client.search(builder -> builder.index(indexName), Object.class);
        List<Hit<Object>> hits = searchResponse.hits().hits();
        List<Object> list=new LinkedList<>();
        hits.forEach(x->list.add(x));
        return list;
    }
}
