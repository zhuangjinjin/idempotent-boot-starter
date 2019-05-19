package io.github.ukuz.idempotent.spring.boot.autoconfigure.core;

import io.github.ukuz.idempotent.spring.boot.autoconfigure.anotation.IdempotentEndPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ukuz90
 * @date 2019-05-19
 */
@RestController
@RequestMapping("/idempotent/")
public class IdempotentResponseBodyController {

    private AtomicInteger balance = new AtomicInteger(100);

    @PutMapping("pay")
    @IdempotentEndPoint
    public ResponseEntity<Integer> pay(int payMoney) {
        int result = balance.addAndGet(-1 * payMoney);
        return new ResponseEntity<Integer>(result, HttpStatus.OK);
    }

}
