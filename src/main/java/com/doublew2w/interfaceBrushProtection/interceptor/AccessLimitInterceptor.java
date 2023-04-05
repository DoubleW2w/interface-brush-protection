package com.doublew2w.interfaceBrushProtection.interceptor;

import com.doublew2w.interfaceBrushProtection.annotation.AccessLimit;
import com.doublew2w.interfaceBrushProtection.constant.ResultCode;
import com.doublew2w.interfaceBrushProtection.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
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
        // 自定义注解 + 反射
        if (handler instanceof HandlerMethod) {
            // 访问的是接口方法，转化为待访问的目标方法对象
            HandlerMethod targetMethod = (HandlerMethod) handler;
            // 获取目标接口方法所在类的注解@AccessLimit
            AccessLimit targetClassAnnotation = targetMethod.getMethod().getDeclaringClass().getAnnotation(AccessLimit.class);
            // targetMethod.getClass()获得是 class org.springframework.web.method.HandlerMethod
            // AccessLimit targetClassAnnotation = targetMethod.getClass().getAnnotation(AccessLimit.class);

            // 定义标记位，标记此类是否加了@AccessLimit注解
            boolean isBrushForAllInterface = false;
            String ip = request.getRemoteAddr();
            String uri = request.getRequestURI();
            long second = 0L;
            long maxTime = 0L;
            long forbiddenTime = 0L;
            if (!Objects.isNull(targetClassAnnotation)) {
                log.info("目标接口方法所在类上有@AccessLimit注解");
                isBrushForAllInterface = true;
                second = targetClassAnnotation.second();
                maxTime = targetClassAnnotation.maxTime();
                forbiddenTime = targetClassAnnotation.forbiddenTime();
            }
            // 取出目标方法中的 AccessLimit 注解
            AccessLimit accessLimit = targetMethod.getMethodAnnotation(AccessLimit.class);
            // 判断此方法接口是否要进行防刷处理
            if (!Objects.isNull(accessLimit)) {
                // 需要进行防刷处理，接下来是处理逻辑
                second = accessLimit.second();
                maxTime = accessLimit.maxTime();
                forbiddenTime = accessLimit.forbiddenTime();
                if (isForbidden(second, maxTime, forbiddenTime, ip, uri)) {
                    throw new CommonException(ResultCode.ACCESS_FREQUENT);
                }
            } else {
                // 目标接口方法处无@AccessLimit注解，但还要看看其类上是否加了（类上有加，代表针对此类下所有接口方法都要进行防刷处理）
                if (isBrushForAllInterface && isForbidden(second, maxTime, forbiddenTime, ip, uri)) {
                    throw new CommonException(ResultCode.ACCESS_FREQUENT);
                }
            }
        }
        return true;
    }

    /**
     * 判断某用户访问某接口是否已经被禁用/是否需要禁用
     *
     * @param second        多长时间  单位/秒
     * @param maxTime       最大访问次数
     * @param forbiddenTime 禁用时长 单位/秒
     * @param ip            访问者ip地址
     * @param uri           访问的uri
     * @return ture为需要禁用
     */
    private boolean isForbidden(long second, long maxTime, long forbiddenTime, String ip, String uri) {
        //如果此ip访问此uri被禁用时的存在Redis中的 key
        String lockKey = StringUtils.joinWith("_", LOCK_PREFIX, uri, ip);
        Object isLock = redisTemplate.opsForValue().get(lockKey);
        // 判断此ip用户访问此接口是否已经被禁用
        if (Objects.isNull(isLock)) {
            // 还未被禁用
            String countKey = StringUtils.joinWith("_", COUNT_PREFIX, uri, ip);
            Object count = redisTemplate.opsForValue().get(countKey);
            if (Objects.isNull(count)) {
                // 首次访问
                log.info("首次访问");
                redisTemplate.opsForValue().set(countKey, 1, second, TimeUnit.SECONDS);
            } else {
                // 此用户前一点时间就访问过该接口，且频率没超过设置
                if ((Integer) count < maxTime) {
                    redisTemplate.opsForValue().increment(countKey);
                } else {
                    log.info("{}禁用访问{}", ip, uri);
                    // 禁用
                    redisTemplate.opsForValue().set(lockKey, 1, forbiddenTime, TimeUnit.SECONDS);
                    // 删除统计--已经禁用了就没必要存在了
                    redisTemplate.delete(countKey);
                    return true;
                }
            }
        } else {
            // 此用户访问此接口已被禁用
            return true;
        }
        return false;
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
