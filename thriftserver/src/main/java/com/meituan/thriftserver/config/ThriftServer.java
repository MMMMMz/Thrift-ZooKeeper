package com.meituan.thriftserver.config;

import com.meituan.thriftserver.interfaces.Student;
import com.meituan.thriftserver.interfaces.StudentService;
import com.meituan.thriftserver.service.StudentServiceImpl;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mazhe
 * @date 2023/8/24 13:03
 */
@Configuration
public class ThriftServer {
    @Value("${service.port}")
    String servicePort;

    /**
     * 线程池最大线程数
     */
    @Value("${server.thrift.max-thread-pool}")
    private Integer maxThreadPool;

    @Value("${server.thrift.min-thread-pool}")
    private Integer minThreadPool;

    /**
     * 业务服务对象
     */
    @Resource
    private StudentServiceImpl studentService;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    /**
     * 开启服务
     * 包含如下步骤：
     * 1. 创建一个socket 用于网络传输
     * 2. 服务模型相关参数设置（最小的工作线程数、最大的工作线程数等）
     * 3. 创建 processor 用于处理业务逻辑
     * 4. 设置协议工厂(protocolFactory)，指定传输格式
     * 5. 设置传输工厂(transportFactory)，指定数据传输方式
     * 6. 设置处理器工厂(processorFactory)，处理业务逻辑
     * 7. 根据参数实例化server
     * 8. 启动server
     */
    public void start(){
        try{
            TNonblockingServerSocket socket = new TNonblockingServerSocket(Integer.parseInt(servicePort));
            /**
             *  THsHaServer： 一个高可用的server
             *  minWorkerThreads： 最小的工作线程
             *  maxWorkerThreads： 最大的工作线程
             */
            //如果这里Args不使用executorService指定线程池的话，创建THsHaServer会创建一个默认的LinkedBlockingQueue
            THsHaServer.Args arg = new THsHaServer.Args(socket).minWorkerThreads(minThreadPool).maxWorkerThreads(maxThreadPool);
            //Processor处理区  用于处理业务逻辑
            //泛型就是实现的业务
            StudentService.Processor<StudentServiceImpl> processor = new StudentService.Processor<>(studentService);
            /**---------------thrift传输协议------------------------------
             1. TBinaryProtocol      二进制传输协议
             2. TCompactProtocol     压缩协议 他是基于TBinaryProtocol二进制协议在进一步的压缩，使得体积更小
             3. TJSONProtocol        Json格式传输协议
             4. TSimpleJSONProtocol  简单JSON只写协议，生成的文件很容易通过脚本语言解析，实际开发中很少使用
             5. TDebugProtocol       简单易懂的可读协议，调试的时候用于方便追踪传输过程中的数据
             -----------------------------------------------------------*/
            //设置工厂
            //协议工厂 TCompactProtocol压缩工厂  二进制压缩协议
            arg.protocolFactory(new TCompactProtocol.Factory());
            /**---------------thrift数据传输方式------------------------------
             1. TSocker            阻塞式Scoker 相当于Java中的ServerSocket
             2. TFrameTransport    以frame为单位进行数据传输，非阻塞式服务中使用
             3. TFileTransport     以文件的形式进行传输
             4. TMemoryTransport   将内存用于IO,Java实现的时候内部实际上是使用了简单的ByteArrayOutputStream
             5. TZlibTransport     使用zlib进行压缩，与其他传世方式联合使用；java当前无实现所以无法使用
             -----------------------------------------------------------*/
            //传输工厂 更加底层的概念
            arg.transportFactory(new TFramedTransport.Factory());
            //设置处理器(Processor)工厂
            arg.processorFactory(new TProcessorFactory(processor));
            TServer server = new THsHaServer(arg);
            System.out.println("thrift server has been started; port: " + servicePort);
            server.serve();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
