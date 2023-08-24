package com.meituan.thriftclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author mazhe
 * @date 2023/8/24 15:07
 */
@Configuration
public class ThriftClientConfig {
    @Bean(initMethod = "init")
    //每次请求实例化一个新的ThriftClient连接对象
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ThriftClient init() {
        ThriftClient thriftClient = new ThriftClient();
        return thriftClient;
    }
}
