package com.doublew2w.interfaceBrushProtection.interceptor;

import com.doublew2w.interfaceBrushProtection.constant.ResultCode;
import com.doublew2w.interfaceBrushProtection.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 13:07
 * @project interface-brush-protection
 */
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 时间段内
     */
    @Value("${interfaceAccess.second}")
    private Long second = 10L;
    /**
     * 访问次数
     */
    @Value("${interfaceAccess.time}")
    private Long time = 3L;
    /**
     * 限制时长
     */
    @Value("${interfaceAccess.lockTime}")
    private Long lockTime = 60L;
    /**
     * 锁住时的key前缀
     */
    public static final String LOCK_PREFIX = "LOCK";
    /**
     * 统计次数时的key前缀
     */
    public static final String COUNT_PREFIX = "COUNT";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteHost();
        String uri = request.getRequestURI();
        String countKey = StringUtils.joinWith("_", COUNT_PREFIX, ip, uri);
        String lockKey = StringUtils.joinWith("_", LOCK_PREFIX, ip, uri);
        Object isLock = redisTemplate.opsForValue().get(lockKey);
        if(Objects.isNull(isLock)){
            Object count = redisTemplate.opsForValue().get(countKey);
            if(Objects.isNull(count)){
                // 首次访问
                log.info("首次访问");
                redisTemplate.opsForValue().set(countKey,1,second, TimeUnit.SECONDS);
            }else{
                // 此用户前一点时间就访问过该接口
                if((Integer)count < time) {
                    // 放行，访问次数 + 1
                    redisTemplate.opsForValue().increment(countKey);
                }else{
                    log.info("{}禁用访问{}",ip, uri);
                    // 禁用
                    redisTemplate.opsForValue().set(lockKey, 1,lockTime, TimeUnit.SECONDS);
                    // 删除统计
                    redisTemplate.delete(countKey);
                    throw new CommonException(ResultCode.ACCESS_FREQUENT);
                }
            }
        }else{
            // 此用户访问此接口已被禁用
            throw new CommonException(ResultCode.ACCESS_FREQUENT);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
