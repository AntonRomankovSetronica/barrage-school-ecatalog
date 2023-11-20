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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
                .setName("Uncle"));
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

    // You can run this peace if u've already had something in db
    @Test
    // Transactional wraps this method with a session, we will need it to make "m.getProducts()" works.
    // U can comment it and check the error, should be: org.hibernate.LazyInitializationException: failed to lazily initialize a collection
    @Transactional(readOnly = true)
    public void get_products_from_merchant() {
        // Here we get just some first merchant
        var m = merchantRepository.findAll().iterator().next();
        // and try to access its products (products aren't fetched from db yet)

        // getProducts returns Set<Product>, but it's nt a HashSet, it's PersistenceSet which is a proxy.
        // Whenever u try to access it proxy will query the db asking for products for this merchant.
        // Without having session (@Transactional(readOnly = true)) the proxy can't understand what connection to use to make a db query.
        System.out.println(new HashSet<>(m.getProducts()));
    }
}

interface ProductRepository extends CrudRepository<Product, UUID> {
    List<Product> findByName(String name);
}

@Accessors(chain = true)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
class Product {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
}

interface MerchantRepository extends CrudRepository<Merchant, Long> {
}

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
class Merchant {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "merchant")
    private Set<Product> products;
}