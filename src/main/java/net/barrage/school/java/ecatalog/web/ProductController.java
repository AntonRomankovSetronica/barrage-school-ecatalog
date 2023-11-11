package net.barrage.school.java.ecatalog.web;

import lombok.extern.slf4j.Slf4j;
import net.barrage.school.java.ecatalog.app.ProductService;
import net.barrage.school.java.ecatalog.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/e-catalog/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> listProducts() {
        var products = productService.listProducts();
        log.trace("listProducts -> {}", products);
        return products;
    }

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam("q") String query
    ) {
        // TODO Implement a search method which filters products by having q in it's name or description
        throw new UnsupportedOperationException();
    }
}
