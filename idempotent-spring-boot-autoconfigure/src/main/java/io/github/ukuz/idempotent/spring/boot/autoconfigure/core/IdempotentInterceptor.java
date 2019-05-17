package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.anotation.IdempotentEndPoint;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.constants.Constant;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            //处理幂等
            String uuid = getUuid(request);
            Idempotent idempotent = beanFactory.getBean(Idempotent.class);
            if (!idempotent.canAccess(uuid)) {
                //已经处理过
                IdempotentKey key = idempotent.getKey(uuid);
                if (key == IdempotentKey.UNKOWN || key.getState() == IdempotentKey.STATE_PROCEED) {
                    inProceed(response);
                } else if (key.getState() == IdempotentKey.STATE_FINISH) {
                    writeBody(key, response);
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //处理幂等结果
        String uuid = getUuid(request);
        Idempotent idempotent = beanFactory.getBean(Idempotent.class);
        IdempotentKey key = idempotent.getKey(uuid);
        if (key != IdempotentKey.UNKOWN && key.getState() != IdempotentKey.STATE_FINISH) {
            wrapResult(key, response, ex);
            idempotent.saveKey(key);
        }
    }

    private String getUuid(HttpServletRequest request) {
        return request.getHeader(Constant.X_REQ_SEQ_ID);
    }

    private void inProceed(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.ACCEPTED.value(), "正在处理中...");
        response.flushBuffer();
    }

    private void wrapResult(IdempotentKey key, HttpServletResponse response, Exception ex) {

    }

    private void writeBody(IdempotentKey key, HttpServletResponse response) throws IOException {
        if (key.getStatusCode() != HttpStatus.OK.value()) {
            response.sendError(key.getStatusCode(), key.getStatusMsg());
        }
        if (key.getHeader() != null) {
            key.getHeader().forEach(response::addHeader);
        }
        if (key.getPayload() != null) {
            StreamUtils.copy(key.getPayload(), response.getOutputStream());
        }
    }

}
