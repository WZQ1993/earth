package com.ziqingwang.feature.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class EsConfig{

    @Value("${app.elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${app.elasticsearch.port}")
    private int port;

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(elasticsearchHost,port)));
    }
}
