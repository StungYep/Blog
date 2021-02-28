package com.zhouyu.blog.advice;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.zhouyu.blog.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {
        //拦截所有SpringMVC可以handle的异常， 其他异常如404不能handle，需要做一个通用的Controller处理
        if(e instanceof CustomizeException) {
            model.addAttribute("message", e.getMessage());
        } else {
            model.addAttribute("message", "服务器冒烟了，要不你稍后试试！！！");
        }
        return new ModelAndView("error");
    }

    //得到请求的状态
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statuscode = (Integer) request.getAttribute("java.servlet.error.status_code");
        if(statuscode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statuscode);
    }
}
