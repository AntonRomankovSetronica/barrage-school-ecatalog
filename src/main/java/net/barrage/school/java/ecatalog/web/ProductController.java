package net.barrage.school.java.ecatalog.web;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.barrage.school.java.ecatalog.app.ProductService;
import net.barrage.school.java.ecatalog.model.Product;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/e-catalog/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final MeterRegistry meterRegistry;

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final Counter listProductsCounter = meterRegistry
            .counter("ecatalog.products.list_counter");

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public List<Product> listProducts() {
        getListProductsCounter().increment();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("user = {}", authentication);
        var products = productService.listProducts();
        return products;
    }

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam("q") String query
    ) {
        var sec = SecurityContextHolder.getContext();
        // TODO Implement a search method which filters products by having q in it's name or description
        throw new UnsupportedOperationException("Search is not yet implemented :( ");
    }
}
