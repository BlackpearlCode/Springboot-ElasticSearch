package com.es.springbootelasticsearch.service.serviceImpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.es.springbootelasticsearch.service.IDocuemtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Service
public class DocumentServiceImpl implements IDocuemtService {

    Logger logger=  LoggerFactory.getLogger(DocumentServiceImpl.class);
    @Autowired
    private  ElasticsearchClient client;

    private static  int size=5;
    private static  int pageNum=0;
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
    public List<Object> queryDocument(Map<String,String> param,String indexName,String sortField,int isDesc,int offset,int pageSize ) throws IOException {
        if(pageSize !=0){
            size= pageSize;
        }
        if(offset !=0){
            pageNum=offset*size;
        }
        SortOrder sort;
        //判断是否为0；0：降序；1：升序
        boolean bool = isDesc == 0 ? true : false;
        if(bool){
            sort = SortOrder.Desc;
        }else{
            sort=SortOrder.Asc;
        }
        List<Query> queries = initMatchQuery(param);
        SearchResponse<?> searchResponse = client.search(s -> s
                .index(indexName)
                .query(q -> q
                        .bool(b -> b.must(queries))
                )
                .from(pageNum)
                .size(size)
                        .sort(f -> f.field(o -> o.field(sortField)
                                .order(sort))),
                Object.class);
        List<? extends Hit<?>> hitList = searchResponse.hits().hits();
        List<Object> objectList=new LinkedList<>();
        for(Hit<?> hit:hitList){
            Object source = hit.source();
            objectList.add(source);
        }
        return objectList;

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
        SearchResponse<Object> searchResponse = client.search(builder -> builder
                        .index(indexName),
                        Object.class);
        List<Hit<Object>> hits = searchResponse.hits().hits();
        List<Object> list=new LinkedList<>();
        hits.forEach(x->list.add(x));
        return list;
    }

    @Override
    public List<Object> templatedSearch(String indexName,String field,String value) throws IOException {
        //创建模板
        client.putScript(r -> r
                //模板标识符
                .id("query-script")
                .script(s -> s
                        .lang("mustache")
                        .source("{\"query\":{\"match\":{\"{{field}}\":\"{{value}}\"}}}")));
        //开始用模板查询
        SearchTemplateResponse<?> response = client.searchTemplate(r -> r
                        //索引名称
                        .index(indexName)
                        //模板标识符
                        .id("query-script")
                        //模板参数值
                        .params("field", JsonData.of(field))
                        .params("value", JsonData.of(value)),
                Object.class);
        List<? extends Hit<?>> hits = response.hits().hits();
        List<Object> list = new ArrayList<>();
        for(Hit<?> obj:hits){
            Object source = obj.source();
            assert source !=null;
            list.add(source);
        }
        return list;
    }

    @Override
    public List<Object> fuzzyQuerySearch(String indexName, String fileName, String fileValue,String fuzziness) throws IOException {
        SearchResponse<Object> response = client.search(s -> s
                        .index(indexName)
                        .query(q -> q
                                // 模糊查询
                                .fuzzy(f -> f
                                        // 需要判断的字段名称
                                        .field(fileName)
                                        // 需要模糊查询的关键词
                                        // 目前文档中没有liuyi这个用户名
                                        .value(fileValue)
                                        // fuzziness代表可以与关键词有误差的字数，可选值为0、1、2这三项
                                        .fuzziness(fuzziness)
                                )
                        ),
                Object.class
        );
        List<Hit<Object>> hits = response.hits().hits();
        List<Object> objects=new LinkedList<>();
        for (Hit<?> hit:hits) {
            objects.add(hit.source());
        }
        return objects;
    }

    //将筛选条件转换成Query类型的集合
    private List<Query> initMatchQuery(Map<String,String> param){
       if(param.isEmpty()){
           return null;
       }
       List<Query> queries=new LinkedList<>();
        for (Map.Entry<String, String> s : param.entrySet()) {
            Query query = MatchQuery.of(m -> m
                    .field(s.getKey())
                    .query(s.getValue())

            )._toQuery();
            queries.add(query);
        }
        logger.info(queries.toString());
        return queries;
    }



}
