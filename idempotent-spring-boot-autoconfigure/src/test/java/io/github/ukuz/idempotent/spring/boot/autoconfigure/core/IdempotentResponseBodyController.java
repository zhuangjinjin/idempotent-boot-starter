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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ukuz90
 * @since 2019-05-19
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
