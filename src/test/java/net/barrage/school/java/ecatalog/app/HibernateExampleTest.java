package net.barrage.school.java.ecatalog.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ActiveProfiles("db")
@SpringBootTest
public class HibernateExampleTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    ProductService productService;

    @Test
    public void save_products_to_db() {
        var uncle = merchantRepository.save(new Merchant()
                .setName("Uncle " + LocalTime.now()));
        var allProducts = productService.listProducts().stream()
                .map(p -> new Product()
                        .setMerchant(uncle)
                        .setId(p.getId())
                        .setName(p.getName())
                        .setDescription(p.getDescription())
                        .setImageUrl(p.getImage()))
                .toList();
        productRepository.saveAll(allProducts);
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

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
}

interface MerchantRepository extends CrudRepository<Merchant, Long> {
}

@Data
@Entity
@Accessors(chain = true)
class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "merchant")
    private Set<Product> products;
}