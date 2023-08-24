package com.meituan.thriftclient;

import com.meituan.thriftclient.interfaces.Student;
import com.meituan.thriftclient.provider.StudentServiceProvider;
import org.apache.thrift.TException;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mazhe
 * @date 2023/8/24 14:47
 */
@RestController
public class StudentController {
    @Resource
    StudentServiceProvider studentServiceProvider;

    @RequestMapping("/get")
    public Student getStudentByName(String name) throws TException {
        return studentServiceProvider.getBalanceUserService().getStudentByName(name);
    }

    @PostMapping("/save")
    public Student save(@RequestBody Student student) throws TException {
        studentServiceProvider.getBalanceUserService().save(student);
        return student;
    }
}
