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

    // FYI - https://www.baeldung.com/jackson-object-mapper-tutorial
    private final ObjectMapper objectMapper;

    @Value("${ecatalog.products.source-path}")
    private File productsSourceFile;

    // Does override in this case signals us that we simply implement the interface method
    // or is there any other reasoning behind this?
    @SneakyThrows
    @Override
    public List<Product> listProducts() {
        return objectMapper.readValue(productsSourceFile, SourceProductList.class).stream()
                .map(sourceProduct -> convert(sourceProduct))
                // I am getting suggestions to use this, so what would be the main reason to use upper example?
                //.map(this::convert)
                .toList();
    }

    @SneakyThrows
    @Override
    public List<Product> listProductsByLetter(String q) {
        return objectMapper.readValue(productsSourceFile, SourceProductList.class).stream()
                .map(sourceProduct -> convert(sourceProduct))
                .filter(product -> product.getName().contains(q) || product.getDescription().contains(q))
                .toList();
    }

    // What is the main reason of not implementing these functions this way, without converting it
    // to product? By changing return type of a function, making Sourceproduct public
    // I guess we can implement it this way and that response will be different (productMedia, etc.),
    // but let's say I want to return object with productMedia and notes, instead of description and image.
    // So is this just not correct way of doing it,or something else?

    //    @SneakyThrows
    //    public List<SourceProduct> listProductsByLetter(String q) {
    //        return objectMapper.readValue(productsSourceFile, SourceProductList.class).stream()
    //                .filter(product -> product.getName().contains(q) || product.getNotes().contains(q))
    //                .toList();
    //    }

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
