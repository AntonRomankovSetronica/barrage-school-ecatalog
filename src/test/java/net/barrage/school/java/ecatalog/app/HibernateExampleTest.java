package net.barrage.school.java.ecatalog.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

@ActiveProfiles("db")
@SpringBootTest
public class HibernateExampleTest {

    @Autowired
    ProductRepository repository;

    @Autowired
    ProductService productService;

    @Test
    public void save_products_to_db() {
        var allProducts = productService.listProducts().stream()
                .map(p -> new Product()
                        .setId(p.getId())
                        .setName(p.getName())
                        .setDescription(p.getDescription())
                        .setImageUrl(p.getImage()))
                .toList();
        repository.saveAll(allProducts);
    }
}

interface ProductRepository extends CrudRepository<Product, UUID> {
    List<Product> findByName(String name);
}

@Accessors(chain = true)
@Data
@Entity
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;
}