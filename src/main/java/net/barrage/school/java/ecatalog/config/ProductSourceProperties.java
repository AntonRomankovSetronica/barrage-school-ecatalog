package net.barrage.school.java.ecatalog.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "ecatalog.products")
public class ProductSourceProperties {

    @Data
    @NoArgsConstructor
    public static class SourceProperty {
        String name;
        String format;
        String url;
    }

    private List<SourceProperty> sources;
}
