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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    void get_products_from_merchant() {
        // Here we get just some first merchant
        var m = merchantRepository.findAll().iterator().next();
        // and try to access its products (products aren't fetched from db yet)

        // getProducts returns Set<Product>, but it's not a HashSet, it's PersistenceSet which is a proxy.
        Set<Product> products = m.getProducts();
        // Whenever u try to access it proxy will query the db asking for products for this merchant.
        // Without having session (@Transactional(readOnly = true)) the proxy can't understand what connection to use to make a db query.
        products.forEach(System.out::println);
    }

    /* If you want to understand why do we need equals and hashCode - consider following test! */
    @Test
    void understanding_EqualsAndHashCode() {
        Set<Product> products = new HashSet<>();
        Product beer = new Product()
                .setId(new UUID(0, 1))
                .setName("Beer");
        Product cheeps = new Product()
                .setId(new UUID(0, 1))
                .setName("Cheeps");

        // We put 2 different objects to our products set here...
        products.add(beer);
        products.add(cheeps);

        // How do u think what size products will have now? Why?
        assertEquals(2, products.size());

        // Are these two objects equals or not? Why?
        assertNotEquals(beer, cheeps);

        // More or less good explanation of how hashmap & hashset works - https://www.javatpoint.com/working-of-hashmap-in-java
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