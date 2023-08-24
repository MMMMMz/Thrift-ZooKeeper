package com.meituan.thriftclient.provider;

import com.meituan.thriftclient.config.ZooKeeperConfig;
import com.meituan.thriftclient.interfaces.Student;
import com.meituan.thriftclient.interfaces.StudentService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

/**
 * @author mazhe
 * @date 2023/8/23 20:47
 */

@Component
public class StudentServiceProvider {

//    public StudentService.Client getBalanceUserService() {
//        Map<String, StudentService.Client> serviceMap = ZooKeeperConfig.serviceMap;
//        //以负载均衡的方式获取服务实例
//        for (Map.Entry<String, StudentService.Client> entry : serviceMap.entrySet()) {
//            System.out.println("可供选择服务:" + entry.getKey());
//        }
//        int rand = new Random().nextInt(serviceMap.size());
//        String[] mKeys = serviceMap.keySet().toArray(new String[serviceMap.size()]);
//        return serviceMap.get(mKeys[rand]);
//    }
}
