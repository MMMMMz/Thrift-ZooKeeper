package com.meituan.thriftclient.config;

import com.meituan.thriftclient.interfaces.StudentService;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author mazhe
 * @date 2023/8/24 15:08
 */
public class ThriftClient {
    private TTransport tTransport;

    private TProtocol tProtocol;

    private StudentService.Client client;

    @Resource
    ZooKeeperConfig zooKeeperConfig;

    /**
     * 初始化
     */
    private void init() {
        List<String> serviceList = ZooKeeperConfig.serviceList;
        int rand = new Random().nextInt(serviceList.size());
        String serviceInstanceName = serviceList.get(rand);
        System.out.println("选择的服务:" + serviceInstanceName);
        String ip = serviceInstanceName.split("-")[1];
        String port = serviceInstanceName.split("-")[2];
        System.out.println("ip: " + ip + " port: " + port);
        tTransport = new TFramedTransport(new TSocket(ip, Integer.parseInt(port)));
        //协议对象 这里使用协议对象需要和服务器的一致
        tProtocol = new TCompactProtocol(tTransport);
        client = new StudentService.Client(tProtocol);
    }

    public StudentService.Client getService() {
        return client;
    }

    public void open() throws TTransportException {
        if (null != tTransport && !tTransport.isOpen()) {
            tTransport.open();
        }
    }

    public void close() {
        if (null != tTransport && tTransport.isOpen()) {
            tTransport.close();
        }
    }
}
