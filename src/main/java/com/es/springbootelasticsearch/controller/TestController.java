package com.es.springbootelasticsearch.controller;



import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {

    @Autowired
    private ElasticsearchClient client;

    //操作索引
    @RequestMapping("/operationIndex")
    public void operationIndex() throws IOException {
        //判断索引是否存在
        boolean value = client.indices().exists(e -> e.index("test_doc")).value();
        System.out.println(value);
    }

}
