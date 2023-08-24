package com.meituan.thriftserver.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mazhe
 * @date 2023/8/24 12:53
 */
@Configuration
public class ZooKeeperConfig {

    @Value("${service.name}")
    String serviceName;

    @Value("${service.port}")
    String servicePort;

    @Value("${zookeeper.server.list}")
    String serverList;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                registerService();
            }
        });
    }

    /**
     * 注册服务
     * @return
     */
    public ZkClient registerService(){
        String servicePath = "/" + serviceName;
        ZkClient zkClient = new ZkClient(serverList);
        boolean rootExists = zkClient.exists(servicePath);
        if(!rootExists){
            zkClient.createPersistent(servicePath, true);
        }
        InetAddress address = null;
        try{
            address = InetAddress.getLocalHost();
        } catch (Exception e){
            e.printStackTrace();
        }
        String ip = address.getHostAddress().toString();
        String serviceInstance = System.nanoTime() + "-" + ip + "-" + servicePort;
        zkClient.createEphemeral(servicePath + "/" + serviceInstance);
        System.out.println("提供的服务为: " + servicePath + "/" + serviceInstance);
        return zkClient;
    }
}
