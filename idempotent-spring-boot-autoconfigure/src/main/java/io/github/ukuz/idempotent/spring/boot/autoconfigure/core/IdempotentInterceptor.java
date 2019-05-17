package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.anotation.IdempotentEndPoint;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ukuz90
 * @date 2019-05-17
 */
public class IdempotentInterceptor implements HandlerInterceptor {

    private BeanFactory beanFactory;

    public IdempotentInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        if (WebContentGenerator.METHOD_GET.equals(request.getMethod())) {
            //GET请求是天然幂等的
            return true;
        }
        if (!method.getClass().isAnnotationPresent(RestController.class)) {
            //非RestController请求不处理
            return true;
        }
        if (method.getClass().isAnnotationPresent(IdempotentEndPoint.class) || method.getMethodAnnotation(IdempotentEndPoint.class) != null) {
            //处理

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
