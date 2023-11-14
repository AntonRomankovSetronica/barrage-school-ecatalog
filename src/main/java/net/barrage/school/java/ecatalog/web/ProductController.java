package net.barrage.school.java.ecatalog.web;

import lombok.extern.slf4j.Slf4j;
import net.barrage.school.java.ecatalog.app.ProductService;
import net.barrage.school.java.ecatalog.app.ProductServiceImpl;
import net.barrage.school.java.ecatalog.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Source;
import java.util.List;
import java.util.Optional;

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

    // I know that we would implement this differently but I'm curious.
    // In case I want to go with RequestBody instead of Param, is it necessary to
    // provide object? For example @RequestBody SearchForm searchForm, and that
    // searchForm object would have name and description inside it so we can provide that data in json.
    // Or is there a simpler way of doing this?
    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam("q") String query
    ) {
        var products = productService.listProductsByLetter(query);
        log.trace("listProduct -> {}", products);
        return products;
    }
}
