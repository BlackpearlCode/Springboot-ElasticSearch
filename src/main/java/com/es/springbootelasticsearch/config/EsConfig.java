package com.es.springbootelasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
@Configuration
public class EsConfig {


    Logger logger=  LoggerFactory.getLogger(EsConfig.class);
    @Value("${spring.elasticsearch.uris}")
    private String hosts;
    @Value("${spring.elasticsearch.port}")
    private int port;
    @Value("${spring.elasticsearch.username}")
    private String userName;
    @Value("${spring.elasticsearch.password}")
    private String password;

    @Bean
    public ElasticsearchAsyncClient elasticsearchClient()  {
        HttpHost[] httpHosts=toHttpHost();
        //无验证信息
        //RestClient.builder(httpHosts).build();
        //有验证信息
        final CredentialsProvider credentialsProvider=new BasicCredentialsProvider();
        //增加身份认证
        credentialsProvider.setCredentials(
                AuthScope.ANY,new UsernamePasswordCredentials(userName,password)
        );
        //添加ca证书
        Path caCertificatePath = Paths.get("src/main/resources/java-ca.crt");
        ElasticsearchAsyncClient client = null;
        try{
            //
            CertificateFactory factory=CertificateFactory.getInstance("X.509");
            Certificate trustedCa;
            //读取ca证书信息
            InputStream inputStream = Files.newInputStream(caCertificatePath);
            trustedCa = factory.generateCertificate(inputStream);
            //获取客户端的信任证书
            KeyStore trustStore=KeyStore.getInstance("PKCS12");
            trustStore.load(null,null);
            //将“ca”与客户端证书关联
            trustStore.setCertificateEntry("ca",trustedCa);
            //设置ssl连接
            SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadKeyMaterial(trustStore, null);
            final SSLContext sslContext = sslContextBuilder.build();
            RestClientBuilder builder = RestClient.builder(httpHosts);
            builder.setRequestConfigCallback(
                    new RestClientBuilder.RequestConfigCallback() {
                        @Override
                        public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                            //设置es响应时间为5s,设置连接es超时时间为6s
                            return builder.setSocketTimeout(6000).setConnectTimeout(5000);
                        }
                    }
            );
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    return httpAsyncClientBuilder
                            //设置连接时的验证信息
                            .setDefaultCredentialsProvider(credentialsProvider)
                            //设置主机名验证器
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                            //设置ssl上下文
                            .setSSLContext(sslContext);
                }
            });
            //创建客户端
            RestClient restClient = builder.build();
            RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            //创建异步客户端对象
            client = new ElasticsearchAsyncClient(transport);
            return client;
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (CertificateException e) {
            logger.error(e.getMessage());
        } catch (KeyStoreException e) {
            logger.error(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        } catch (UnrecoverableKeyException e) {
            logger.error(e.getMessage());
        } catch (KeyManagementException e) {
            logger.error(e.getMessage());
        }
        return client;
    }

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
