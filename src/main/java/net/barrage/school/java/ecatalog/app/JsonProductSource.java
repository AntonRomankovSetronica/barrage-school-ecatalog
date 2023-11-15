package net.barrage.school.java.ecatalog.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.barrage.school.java.ecatalog.config.ProductSourceProperties;
import net.barrage.school.java.ecatalog.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class JsonProductSource implements ProductSource {
    private final ProductSourceProperties.SourceProperty property;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(JsonProductSource.class);

    @RequiredArgsConstructor
    @Component
    public static class Factory implements ProductSource.Factory {
        private final ObjectMapper objectMapper;

        @Override
        public Set<String> getSupportedFormats() {
            return Set.of("json");
        }

        @Override
        public ProductSource create(ProductSourceProperties.SourceProperty psp) {
            return new JsonProductSource(psp, objectMapper);
        }
    }

    @Override
    public List<Product> getProducts() {
        try {
            return objectMapper.readValue(new URL(property.getUrl()).openStream(), SourceProductList.class).stream()
                    .map(sourceProduct -> convert(sourceProduct))
                    .toList();
        } catch (Exception e1) {
            log.warn("Oops!", e1);
            throw new RuntimeException(e1);
        }
    }


    private Product convert(SourceProduct sourceProduct) {
        var product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(sourceProduct.getName());
        product.setDescription(sourceProduct.getNotes());
        product.setImage(Optional.ofNullable(sourceProduct.productMedia)
                .flatMap(media -> media.stream().findFirst())
                .orElse(null));
        product.setPrice(sourceProduct.getPrice());
        return product;
    }

    static class SourceProductList extends ArrayList<SourceProduct> {
    }

    @Data
    static class SourceProduct {
        private String name;
        private String notes;
        private List<String> productMedia;
        private double price;
    }
}
