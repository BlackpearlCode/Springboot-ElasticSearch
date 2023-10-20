package com.es.springbootelasticsearch.util;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation;

import java.util.LinkedList;
import java.util.List;

/**
 * 将通用类型集合转换成BulkOperation类型的集合
 * @param <T>
 */
public class BulkOperationUtil<T> {


    /**
     *
     * @param list：文档数据
     * @param indexName：索引名称
     * @return
     */
    public List<BulkOperation> toBulkOperationList(List<T> list, String indexName){
        List<BulkOperation> bulkOperations=new LinkedList<>();
        for(int i=0;i<list.size();i++){
            CreateOperation<T> operation = new CreateOperation.Builder<T>()
                    .index(indexName)
                    .id("200" + i)
                    .document(list.get(i))
                    .build();
            BulkOperation bulkOperation = new BulkOperation.Builder().create(operation).build();
            bulkOperations.add(bulkOperation);
        }
        return bulkOperations;
    }
}
