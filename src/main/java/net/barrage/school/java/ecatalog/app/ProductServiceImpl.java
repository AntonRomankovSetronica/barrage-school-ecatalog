package net.barrage.school.java.ecatalog.app;

import lombok.SneakyThrows;
import net.barrage.school.java.ecatalog.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final List<ProductSource> productSources;

    public ProductServiceImpl(
            List<ProductSource> productSources) {
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

    @SneakyThrows
    @Override
    public List<Product> searchProducts(String query) {
        return listProducts().stream()
                .filter(sourceProduct -> {
                    var q = query.trim().toLowerCase();
                    return sourceProduct.getName().toLowerCase().contains(q) || sourceProduct.getDescription().toLowerCase().contains(q);
                })
                .toList();
    }
}
