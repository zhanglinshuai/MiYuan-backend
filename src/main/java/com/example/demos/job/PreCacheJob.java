package com.example.demos.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demos.pojo.domain.User;
import com.example.demos.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;
    /**
     * 需要预热的对象id
     */
    private List<Long> PreCacheUserId = Arrays.asList(1L);

    /**
     * 每天预热缓存
     * 定时每天12点02分执行定时任务
     */
    @Scheduled(cron = "0 2 0 * * *")
    public void doCacheRecommendUser() {
        for (Long userId : PreCacheUserId) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            String redisKey = String.format("MiYuan:user:recommend:%s", userId);
            Page<User> userPage = userService.page(new Page<>(1, 20), userQueryWrapper);
            //读完数据库后写缓存
            try {
                redisTemplate.opsForValue().set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("redis write error", e);
            }
        }
    }


}
