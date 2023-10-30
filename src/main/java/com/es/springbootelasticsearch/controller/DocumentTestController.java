package com.es.springbootelasticsearch.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.es.springbootelasticsearch.dto.User;
import com.es.springbootelasticsearch.service.serviceImpl.DocumentServiceImpl;
import com.es.springbootelasticsearch.util.BulkOperationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class DocumentTestController {

    @Autowired
    private DocumentServiceImpl documentService;

    /**
     * 单个添加文档型数据
     * @param indexName: 索引名称
     * @param id：文档id
     * @param obj:doc内容
     * @throws IOException
     */
    @RequestMapping("/createDocument")
    public void createDocument(@RequestBody Map<String,Object> param) throws IOException {
        documentService.createDocument(param.get("indexName").toString(), param.get("id").toString(),  param.get("obj"));
    }

    /**
     * 批量添加文档型数据
     * @param param: 传入一个索引名称和list数据集
     * @throws IOException
     */
    @RequestMapping("/batchCreateDocument")
    public void batchCreateDocument(@RequestBody Map<String,Object> param) throws IOException {

        BulkOperationUtil util=new BulkOperationUtil();

        List list = util.toBulkOperationList((List) param.get("obj"), param.get("indexName").toString());

        documentService.batchCreateDocument(list);
    }

    /**
     * 根据索引名称和索引ID删除文档型数据
     * @param indexName：索引名称
     * @param id：索引ID
     * @throws IOException
     */
    @RequestMapping("/deleteIndexByNameAndId")
    public void deleteIndexByNameAndId(@Param("indexName")String indexName,@Param("id")String id) throws IOException {
        documentService.deleteByName(indexName,id);
    }

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
    @RequestMapping("/queryDocument")
    public String queryDocument(@RequestBody Map<String,String> param,@Param("indexName") String indexName,@Param("sortField") String sortField,@Param("isDesc") int isDesc,@Param("offset") int offset,@Param("pageSize") int pageSize) throws IOException {
        List<Object> objects = documentService.queryDocument( param, indexName, sortField, isDesc, offset, pageSize);
        return objects.toString();
    }

    /**
     * 根据索引名称文档id获取文档信息
     * @param indexName：索引名称
     * @param id：文档id
     * @throws IOException
     */
    @RequestMapping("/getDocucemt")
    public void getDocucemt(@Param("indexName")String indexName,@Param("id")String id) throws IOException{
        Object document = documentService.getDocument(indexName, id);
        System.out.println(document);
    }

    /**
     * 根据索引名称和文档id判断文档是否存在
     * @param indexName：索引名称
     * @param id：文档id
     * @throws IOException
     */
    @RequestMapping("/documentIsExit")
    public void documentIsExit(@Param("indexName")String indexName,@Param("id")String id) throws IOException{
        boolean isExit = documentService.documentIsExit(indexName, id);
        System.out.println(isExit);

    }

    /**
     * 修改指定文档内容信息
     * @param param
     * @throws IOException
     */
    @RequestMapping("/updateDocument")
    public void updateDocument(@RequestBody Map<String,Object> param) throws IOException{
        boolean updated = documentService.updateDocument(param.get("indexName").toString(), param.get("id").toString(),  param.get("obj"));
        System.out.println(updated);
    }

    /**
     * 批量删除数据
     * @param indexName：索引名称
     * @param ids：文档id集合
     * @throws IOException
     */
    @RequestMapping("/batchDelDocument")
    public void batchDelDocument(@Param("indexName")String indexName,@Param("id")List<String> ids) throws IOException {
        Boolean batchedDeletDocument = documentService.batchDeletDocument(indexName, ids);
        System.out.println(batchedDeletDocument);
    }

    /**
     * 查询指定索引下的所有数据
     * @param indexName：所有名称
     * @throws IOException
     */
    @RequestMapping("/queryAllDocument")
    public void queryAllDocument(@Param("indexName")String indexName) throws IOException {
        List<Object> objects = documentService.queryAllDocument(indexName);
        System.out.println(objects.toString());
    }

    /**
     * 模板搜索
     * @param param
     * @throws IOException
     */
    @RequestMapping("/templatedSearch")
    public void templatedSearch(@RequestBody Map<String,Object> param) throws IOException {
        List<Object> objects = documentService.templatedSearch((String) param.get("indexName"), (String) param.get("field"), (String) param.get("value"));
        System.out.println(objects.toString());
    }
}
