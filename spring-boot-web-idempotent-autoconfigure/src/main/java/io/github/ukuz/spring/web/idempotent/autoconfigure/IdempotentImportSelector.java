package io.github.ukuz.spring.web.idempotent.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public class IdempotentImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<String> imports = new ArrayList<>();
        imports.add("io.github.ukuz.spring.web.idempotent.autoconfigure.core.StoreFactoryBean");
        return imports.toArray(new String[0]);
    }

}
