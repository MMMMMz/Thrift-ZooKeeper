package com.meituan.thriftserver.service;

import com.meituan.thriftserver.interfaces.DataException;
import com.meituan.thriftserver.interfaces.Student;
import com.meituan.thriftserver.interfaces.StudentService;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

/**
 * @author mazhe
 * @date 2023/8/24 13:01
 */
@Service
public class StudentServiceImpl implements StudentService.Iface {
    @Override
    public Student getStudentByName(String name) throws DataException, TException {
        System.out.println("服务端收到客户端获取用户名:" + name + "信息");
        Student student = new Student();
        student.setName(name);
        student.setAge(20);
        student.setSex("男");
        student.setAddress("北京望京");

        //模拟耗时
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("模拟获取成功并返回：" + student);
        return student;
    }

    @Override
    public void save(Student student) throws DataException, TException {
        System.out.println("服务端收到客户端请求保存学生信息：" + student);
        //模拟耗时
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("模拟保存成功!!!");
    }
}
