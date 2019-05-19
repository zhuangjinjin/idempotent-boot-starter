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
 * @date 2019-05-19
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
