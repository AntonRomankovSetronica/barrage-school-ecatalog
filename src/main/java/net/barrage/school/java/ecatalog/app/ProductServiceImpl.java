package net.barrage.school.java.ecatalog.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
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

    // FYI - https://www.baeldung.com/jackson-object-mapper-tutorial
    private final ObjectMapper objectMapper;

    @Value("${ecatalog.products.source-path}") // found in resources/application.yaml
    private File productsSourceFile;

    @SneakyThrows
    @Override
    public List<Product> listProducts() {
        return objectMapper.readValue(productsSourceFile, SourceProductList.class).stream()
                .map(sourceProduct -> convert(sourceProduct))
                .toList();
    }

    @SneakyThrows
    @Override
    public List<Product> searchProducts(String query) {
        return objectMapper.readValue(productsSourceFile, SourceProductList.class).stream()
                .filter(sourceProduct -> {
                    var q = query.trim().toLowerCase();
                    return sourceProduct.getName().toLowerCase().contains(q) || sourceProduct.getNotes().toLowerCase().contains(q);
                })
                .map(sourceProduct -> convert(sourceProduct)) // is it better to use filter + mapping or is it better to use function like reduce to create new list directly?
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
