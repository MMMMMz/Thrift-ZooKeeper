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

    public static Map<String, StudentService.Client> serviceMap = new HashMap<>();

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
            if (!serviceMap.containsKey(instanceName)) {
                serviceMap.put(instanceName, createStudentService(instanceName));
            }
        }

        zkClient.subscribeChildChanges(servicePath, (parentPath, curChildren1) -> {
            for (String instanceName : curChildren1) {
                if (!serviceMap.containsKey(instanceName)) {
                    serviceMap.put(instanceName, createStudentService(instanceName));
                }
            }
            for (Map.Entry<String, StudentService.Client> entry : serviceMap.entrySet()) {
                if (!curChildren1.contains(entry.getKey())) {
                    StudentService.Client c = serviceMap.get(entry.getKey());
                    try {
                        c.getInputProtocol().getTransport().close();
                        c.getOutputProtocol().getTransport().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    serviceMap.remove(entry.getKey());
                }
            }
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
