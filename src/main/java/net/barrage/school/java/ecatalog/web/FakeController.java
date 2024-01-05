package net.barrage.school.java.ecatalog.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@ConditionalOnProperty(value = "test.FakeController", havingValue = "true")
@RestController
public class FakeController {

    @PostMapping("/e-catalog/api/v1/order")
    public String createOrder() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("order from {}", user);
        return "ok";
    }

    @GetMapping("/e-catalog/api/v1/mobile/mainscreen")
    public String mainScreen(
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        log.info("main screen at ({},{})", lat, lng);
        return "ok";
    }
}
