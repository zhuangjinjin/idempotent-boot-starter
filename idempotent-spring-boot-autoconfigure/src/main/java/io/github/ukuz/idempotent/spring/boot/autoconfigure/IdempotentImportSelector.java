package io.github.ukuz.idempotent.spring.boot.autoconfigure;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;

/**
 * @author ukuz90
 * @since 2019-05-20
 */
public class IdempotentImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        ArrayList<String> list = new ArrayList<>();
        list.add("io.github.ukuz.idempotent.spring.boot.autoconfigure.core.IdempotentResponseBodyAdvice");
        return list.toArray(new String[0]);
    }
}
