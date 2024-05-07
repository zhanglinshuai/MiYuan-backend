package com.example.demos.once;

import com.example.demos.pojo.domain.User;
import com.example.demos.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 批量插入用户任务
 */
@Component
public class InsertUserUtils {

    @Resource
    private UserService userService;

    /**
     * 自定义线程池
     */
    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

    public void insertUser() {
        final int INSET_NUM = 100000;
        int batchSize = 10000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSET_NUM; i++) {
            User user = new User();
            user.setUsername("");
            user.setUserAccount("");
            user.setUserPassword("");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserRole(0);
            user.setPlanetCode("");
            user.setEmail("");
            user.setPhone("");
            user.setTags("");
            user.setProfile("");
            userList.add(user);
        }
        userService.saveBatch(userList, batchSize);
    }


    public void doConcurrencyInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSET_NUM = 1000000;
        int j = 0;
        int batchSize = 2500;
        //创建异步任务数组
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        //分成十组，一组1w条，开10个多线程
        for (int i = 0; i < 40; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("");
                user.setUserAccount("");
                user.setUserPassword("");
                user.setAvatarUrl("");
                user.setGender(0);
                user.setUserRole(0);
                user.setPlanetCode("");
                user.setEmail("");
                user.setPhone("");
                user.setTags("");
                user.setProfile("");
                userList.add(user);
                if(j%INSET_NUM==0){
                    break;
                }
            }
            //异步执行插入操作
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, batchSize);
            },executorService);
            //将异步任务加入异步任务数组
            futureList.add(future);
        }
        //异步执行，保证该语句执行完后才能执行下面的语句
        CompletableFuture.allOf(new CompletableFuture[]{}).join();
        stopWatch.stop();
        //输出执行完异步插入操作后所需的时间
        System.out.println(stopWatch.getTotalTimeMillis());
    }


}
