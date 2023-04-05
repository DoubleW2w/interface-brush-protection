package com.doublew2w.interfaceBrushProtection.controller;

import com.doublew2w.interfaceBrushProtection.annotation.AccessLimit;
import com.doublew2w.interfaceBrushProtection.constant.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 注解请求控制器类
 *
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 14:57
 * @project interface-brush-protection
 */
@RestController
@Validated
@Slf4j
@RequestMapping("/anno")
public class AnnoPassController {

    @AccessLimit(second = 3L, maxTime = 3L, forbiddenTime = 110L)
    @GetMapping("/pass/get/{id}")
    public Result passGet(@PathVariable("id") Integer id) {
        log.info("执行passGet方法，id为{}", id);
        return Result.success();
    }

    @AccessLimit(second = 4L, maxTime = 3L, forbiddenTime = 110L)
    @PutMapping("/pass/put")
    public Result passPut() {
        log.info("执行 passPut 方法");
        return Result.success();
    }


    @AccessLimit(second = 5L, maxTime = 2L, forbiddenTime = 100L)
    @DeleteMapping("/pass/delete")
    public Result passDelete() {
        log.info("执行 passDelete 方法");
        return Result.success();
    }
}
