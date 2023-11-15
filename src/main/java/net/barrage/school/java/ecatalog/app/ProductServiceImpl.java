package net.barrage.school.java.ecatalog.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import net.barrage.school.java.ecatalog.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    // FYI - https://www.baeldung.com/jackson-object-mapper-tutorial
    private final ObjectMapper objectMapper;
    private final List<ProductSource> productSources;

    public ProductServiceImpl(
            ObjectMapper objectMapper,
            List<ProductSource> productSources) {
        this.objectMapper = objectMapper;
        this.productSources = productSources;
    }

    @SneakyThrows
    @Override
    public List<Product> listProducts() {
        var result = new ArrayList<Product>();
        for (var ps : productSources) {
            result.addAll(ps.getProducts());
        }
        return result;
    }
}
