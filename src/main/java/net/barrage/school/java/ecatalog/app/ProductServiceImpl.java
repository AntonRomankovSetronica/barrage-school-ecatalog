package net.barrage.school.java.ecatalog.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.barrage.school.java.ecatalog.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ObjectMapper objectMapper;

    @Value("${ecatalog.products.source-path}")
    private File productsSourceFile;

    @SneakyThrows
    @Override
    public List<Product> listProducts() {
        return objectMapper.readValue(productsSourceFile, SourceProductList.class).stream()
                .map(sourceProduct -> convert(sourceProduct))
                .toList();
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
