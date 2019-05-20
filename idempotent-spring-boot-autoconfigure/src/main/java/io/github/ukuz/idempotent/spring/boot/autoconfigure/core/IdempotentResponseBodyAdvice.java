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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.anotation.IdempotentEndPoint;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.constants.Constant;
import io.github.ukuz.idempotent.spring.boot.autoconfigure.exception.StoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * @author ukuz90
 * @since 2019-05-19
 */
@ControllerAdvice
public class IdempotentResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private Idempotent idempotent;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (idempotent == null) {
            return false;
        }
        if (returnType.getMethodAnnotation(GetMapping.class) != null) {
            //GET请求是天然幂等的
            return false;
        }
        if (returnType.getDeclaringClass().isAnnotationPresent(IdempotentEndPoint.class)
                || returnType.getMethod().isAnnotationPresent(IdempotentEndPoint.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        List<String> uuid = request.getHeaders().get(Constant.X_REQ_SEQ_ID);
        if (uuid == null || uuid.isEmpty()) {
            return body;
        }

        try {
            IdempotentKey key = idempotent.getKey(uuid.get(0));
            if (body instanceof String) {
                key.setPayload(body.toString());
            } else if (body instanceof ResponseEntity) {
                ObjectMapper objectMapper = new ObjectMapper();
                String str = objectMapper.writeValueAsString(((ResponseEntity) body).getBody());
                key.setPayload(str);
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                String str = objectMapper.writeValueAsString(body);
                key.setPayload(str);
            }
            idempotent.saveKey(key);
        } catch (StoreException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return body;

    }
}
