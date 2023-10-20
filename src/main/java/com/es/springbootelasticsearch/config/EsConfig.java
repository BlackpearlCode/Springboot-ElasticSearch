package com.es.springbootelasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
@Configuration
public class EsConfig {


    static Logger logger=  LoggerFactory.getLogger(EsConfig.class);
    @Value("${spring.elasticsearch.uris}")
    private String hosts;
    @Value("${spring.elasticsearch.port}")
    private int port;
    @Value("${spring.elasticsearch.username}")
    private String userName;
    @Value("${spring.elasticsearch.password}")
    private String password;


    //同步客户端连接
    @Bean
    public ElasticsearchClient clientByPasswd()  {
        ElasticsearchTransport transport = getElasticsearchTransport(userName,password, toHttpHost());
        return new ElasticsearchClient(transport);
    }

    //异步客户端连接
    public ElasticsearchAsyncClient asyncClientByPasswd(){
        ElasticsearchTransport transport = getElasticsearchTransport(userName,password, toHttpHost());
        return new ElasticsearchAsyncClient(transport);
    }

    private static SSLContext buildSSLContext() {
        ClassPathResource resource = new ClassPathResource("java-ca.crt");
        SSLContext sslContext = null;
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate trustedCa;
            try (InputStream is = resource.getInputStream()) {
                trustedCa = factory.generateCertificate(is);
            }
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            sslContext = sslContextBuilder.build();
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException |
                 KeyManagementException e) {
            logger.error("ES连接认证失败", e);
        }

        return sslContext;
    }

    private static ElasticsearchTransport getElasticsearchTransport(String username, String passwd, HttpHost...hosts) {
        // 账号密码的配置
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, passwd));

        // 自签证书的设置，并且还包含了账号密码
        RestClientBuilder.HttpClientConfigCallback callback = httpAsyncClientBuilder -> httpAsyncClientBuilder
                .setSSLContext(buildSSLContext())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultCredentialsProvider(credentialsProvider);

        // 用builder创建RestClient对象
        RestClient client = RestClient
                .builder(hosts)
                .setHttpClientConfigCallback(callback)
                .build();

        return new RestClientTransport(client, new JacksonJsonpMapper());
    }

//安全设置：证书+apikey
//    private static ElasticsearchTransport getElasticsearchTransport(String apiKey, HttpHost...hosts) {
//        // 将ApiKey放入header中
//        Header[] headers = new Header[] {new BasicHeader("Authorization", "ApiKey " + apiKey)};
//
//        // es自签证书的设置
//        HttpClientConfigCallback callback = httpAsyncClientBuilder -> httpAsyncClientBuilder
//                .setSSLContext(buildSSLContext())
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
//
//        // 用builder创建RestClient对象
//        RestClient client = RestClient
//                .builder(hosts)
//                .setHttpClientConfigCallback(callback)
//                .setDefaultHeaders(headers)
//                .build();
//
//        return new RestClientTransport(client, new JacksonJsonpMapper());
//    }

//    @Bean
//    public ElasticsearchClient clientByApiKey() throws Exception {
//        ElasticsearchTransport transport = getElasticsearchTransport(apikey, toHttpHost());
//        return new ElasticsearchClient(transport);
//    }

    //将多个连接地址存放进数组中
    private HttpHost[] toHttpHost(){

        if(!StringUtils.hasLength(hosts)){
            logger.error("elasticsearch 连接地址不能为空");
        }
        //将多个用逗号隔开的ip存放进数组中
        String[] hostArray = hosts.split(",");
        HttpHost[] httpHosts=new HttpHost[hostArray.length];
        HttpHost httpHost;

        for(int i=0;i<hostArray.length;i++){
            httpHost=new HttpHost(hostArray[i],port,"https");
            httpHosts[i]=httpHost;
        }
        return httpHosts;
    }
}
