package net.barrage.school.java.ecatalog.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class ProductServiceImplTest {

    @Autowired
    ProductServiceImpl impl;

    @Test
    void products_are_not_empty() {
        assertFalse(impl.listProducts().isEmpty(), "Expect listProducts() to return something.");
    }
}