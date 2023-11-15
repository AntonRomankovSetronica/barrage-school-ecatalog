package net.barrage.school.java.ecatalog.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class XmlProductSource implements ProductSource {
    private final ProductSourceProperties.SourceProperty property;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(XmlProductSource.class);

    @RequiredArgsConstructor
    @Component
    public static class Factory implements ProductSource.Factory {
        private final ObjectMapper objectMapper;

        @Override
        public Set<String> getSupportedFormats() {
            return Set.of("xml");
        }

        @Override
        public ProductSource create(ProductSourceProperties.SourceProperty psp) {
            return new XmlProductSource(psp, objectMapper);
        }
    }

    @Override
    public List<Product> getProducts() {
        try {
            ObjectMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(new URL(property.getUrl()).openStream(), SourceProductList.class).stream()
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
        product.setName(String.valueOf(sourceProduct.getTitle()));
        product.setDescription(sourceProduct.getDescription());
        product.setImage(null);
        product.setPrice(sourceProduct.getPrice());
        return product;
    }

    static class SourceProductList extends ArrayList<SourceProduct> {
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SourceProduct {
        private String title;
        private String description;
        private double price;
    }
}