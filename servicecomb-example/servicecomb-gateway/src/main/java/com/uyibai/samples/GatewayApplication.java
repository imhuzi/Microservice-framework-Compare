package com.uyibai.samples;


import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author: LiYang
 * @create: 2019-12-28 21:40
 * @Description:
 * 公共网关服务
 **/


@SpringBootApplication
@EnableZuulProxy
@EnableServiceComb
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
