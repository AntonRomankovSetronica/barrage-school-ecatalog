package net.barrage.school.java.ecatalog.app;

import net.barrage.school.java.ecatalog.config.ProductSourceProperties;
import net.barrage.school.java.ecatalog.model.Product;

import java.util.List;
import java.util.Set;

public interface ProductSource {

    interface Factory {
        Set<String> getSupportedFormats();

        ProductSource create(ProductSourceProperties.SourceProperty psp);
    }

    List<Product> getProducts();
}
