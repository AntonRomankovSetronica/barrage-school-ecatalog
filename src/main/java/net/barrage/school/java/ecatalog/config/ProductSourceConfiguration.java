package net.barrage.school.java.ecatalog.config;

import net.barrage.school.java.ecatalog.app.ProductSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ProductSourceConfiguration {

    @Autowired
    private ProductSourceProperties properties;

    @Autowired
    private List<ProductSource.Factory> productSourceFactories;

    @Bean
    public List<ProductSource> getProductSources() {
        return properties.getSources().stream()
                .map(this::createProductSourceFromFormat)
                .collect(Collectors.toList());
    }

    private ProductSource createProductSourceFromFormat(
            ProductSourceProperties.SourceProperty psp) {
        for (var f : productSourceFactories) {
            if (f.getSupportedFormats().contains(psp.getFormat())) {
                return f.create(psp);
            }
        }
        throw new UnsupportedOperationException();
    }
}
