package com.meituan.thriftclient.config;

import com.meituan.thriftclient.interfaces.Student;
import com.meituan.thriftclient.interfaces.StudentService;
import org.I0Itec.zkclient.ZkClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author mazhe
 * @date 2023/8/24 14:23
 */
@Configuration
public class ZooKeeperConfig {

    @Value("${service.name}")
    String serviceName;
    @Value("${zookeeper.server.list}")
    String serverList;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    public static List<String> serviceList = new ArrayList<>();

    @PostConstruct
    private void init() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                startZooKeeper();
            }
        });
    }

    private void startZooKeeper() {
        List<String> curChildren = new ArrayList<>();
        String servicePath = "/" + serviceName;
        ZkClient zkClient = new ZkClient(serverList);
        boolean serviceExists = zkClient.exists(servicePath);

        if (serviceExists) {
            curChildren = zkClient.getChildren(servicePath);
        } else {
            throw new RuntimeException("service not exist!");
        }

        for (String instanceName : curChildren) {
            if (!serviceList.contains(instanceName)) {
                serviceList.add(instanceName);
            }
        }
        System.out.println("可供选择服务:" + serviceList.stream().collect(Collectors.joining(",")));

        zkClient.subscribeChildChanges(servicePath, (parentPath, curChildren1) -> {
            for (String instanceName : curChildren1) {
                if (!serviceList.contains(instanceName)) {
                    serviceList.add(instanceName);
                }
            }
            for (String instanceName : serviceList) {
                if (!curChildren1.contains(instanceName)) {
                    serviceList.remove(instanceName);
                }
            }
            System.out.println(parentPath + "事件触发！");
        });
    }

    public StudentService.Client createStudentService(String serviceInstanceName) {
        String ip = serviceInstanceName.split("-")[1];
        String port = serviceInstanceName.split("-")[2];
        TSocket transport = new TSocket(ip, Integer.parseInt(port), 30000);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        return new StudentService.Client(new TCompactProtocol(new TFramedTransport(transport)));
    }

}
