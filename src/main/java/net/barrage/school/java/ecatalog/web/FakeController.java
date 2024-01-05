package net.barrage.school.java.ecatalog.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/* TODO Disable fake property in your implementation! */
@Slf4j
@ConditionalOnProperty(value = "ecatalog.fake", havingValue = "true")
@RestController
public class FakeController {

    @SneakyThrows
    @PostMapping("/e-catalog/api/v1/order")
    public String createOrder() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("order from {}", user);
        Thread.sleep(100);
        return "ok";
    }

    @SneakyThrows
    @GetMapping("/e-catalog/api/v1/mobile/mainscreen")
    public MainScreen mainScreen(
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        log.info("main screen at ({},{})", lat, lng);
        Thread.sleep(50);
        return MAIN_SCREEN;
    }

    @Data
    @Builder
    static class MainScreen {
        @Singular
        private List<MsMerchant> merchants;
    }

    @Data
    @Builder
    static class MsMerchant {
        private String name;

        @Singular
        @JsonProperty("popular-products")
        private List<MsProduct> products;
    }

    @Data
    @Accessors(chain = true)
    static class MsProduct {
        private String name;
        private String image;
    }

    public static final MainScreen MAIN_SCREEN;

    static {
        var msBuilder = MainScreen.builder();
        for (int i = 1; i <= 3; i++) {
            var mb = MsMerchant.builder();
            mb.name("M" + i);
            for (int j = 1; j <= 3; j++) {
                mb.product(new MsProduct()
                        .setName("P" + i + j)
                        .setImage("I" + i + j));
            }
        }
        MAIN_SCREEN = msBuilder.build();
    }
}
