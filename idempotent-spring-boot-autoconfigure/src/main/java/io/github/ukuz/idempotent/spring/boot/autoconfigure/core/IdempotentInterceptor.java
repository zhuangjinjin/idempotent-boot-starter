/*
 * Copyright 2019 ukuz90
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.anotation.IdempotentEndPoint;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.constants.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @author ukuz90
 * @since 2019-05-17
 */
public class IdempotentInterceptor implements HandlerInterceptor {

    private BeanFactory beanFactory;

    private static final Logger logger = LoggerFactory.getLogger(IdempotentInterceptor.class);

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
        if (!method.getBeanType().isAnnotationPresent(RestController.class)) {
            //非RestController请求不处理
            return true;
        }
        if (method.getBeanType().isAnnotationPresent(IdempotentEndPoint.class) || method.getMethodAnnotation(IdempotentEndPoint.class) != null) {
            //处理幂等
            String uuid = getUuid(request);
            if (logger.isDebugEnabled()) {
                logger.debug("{}: {}", Constant.X_REQ_SEQ_ID, uuid);
            }
            Idempotent idempotent = beanFactory.getBean(Idempotent.class);
            if (!idempotent.canAccess(uuid)) {
                //已经处理过
                IdempotentKey key = idempotent.getKey(uuid);
                if (key == IdempotentKey.UNKOWN || key.getState() == IdempotentKey.STATE_PROCEED) {
                    inProceed(response, uuid);
                } else if (key.getState() == IdempotentKey.STATE_FINISH) {
                    writeBody(key, response, uuid, method);
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
        if (key != IdempotentKey.UNKOWN) {
            processResult(key, response, ex);
            idempotent.saveKey(key);
        }
    }

    private String getUuid(HttpServletRequest request) {
        return request.getHeader(Constant.X_REQ_SEQ_ID);
    }

    private void inProceed(HttpServletResponse response, String uuid) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.debug("retry and get result, [{}:{}] waiting...", Constant.X_REQ_SEQ_ID, uuid);
        }
        response.sendError(HttpStatus.ACCEPTED.value(), "正在处理中...");
        response.flushBuffer();
    }

    private void processResult(IdempotentKey key, HttpServletResponse response, Exception ex) {
        //设置状态码
        if (key.getStatusCode() == 0) {
            key.setStatusCode(response.getStatus());
        }
        //设置头信息
        if (CollectionUtils.isEmpty(key.getHeader())) {
            Collection<String> headerNames = response.getHeaderNames();
            if (!headerNames.isEmpty()) {
                Map<String, String> map = new HashMap<>(headerNames.size());
                headerNames.forEach(headerName ->
                        map.put(headerName, response.getHeader(headerName))
                );
                key.setHeader(map);
            }
        }
    }

    private void writeBody(IdempotentKey key, HttpServletResponse response, String uuid, HandlerMethod method) throws IOException, ClassNotFoundException {
        if (key.getStatusCode() != HttpStatus.OK.value() && key.getStatusCode() > 0) {
            response.sendError(key.getStatusCode(), key.getStatusMsg());
        }
        if (key.getHeader() != null) {
            key.getHeader().forEach(response::addHeader);
        }

        if (!StringUtils.isEmpty(key.getPayload())) {
            //写内容
            if (logger.isInfoEnabled()) {
                logger.info("retry and get result, [{}:{}] body: {}", Constant.X_REQ_SEQ_ID, uuid, new String(key.getPayload().getBytes()));
            }
            response.getOutputStream().write(key.getPayload().getBytes());
            response.getOutputStream().flush();
        }

    }

}
