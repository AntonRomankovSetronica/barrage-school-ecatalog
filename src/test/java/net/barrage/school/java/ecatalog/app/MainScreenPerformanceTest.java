package net.barrage.school.java.ecatalog.app;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static net.barrage.school.java.ecatalog.app.SecurityTest.BEARER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainScreenPerformanceTest {

    @SneakyThrows
    @Test
    void test1() {
        Flux.fromStream(IntStream.rangeClosed(1, 10).boxed())
                .concatMap(this::roundTest)
                .doOnNext(System.out::println)
                .blockLast();
    }


    Mono<Report> roundTest(int scenarioIndex) {
        Report report = new Report();
        report.setScenarioIndex(scenarioIndex);

        return testForScenario(new Scenario() {
            Instant ordersBegin;
            Instant mainScreensBegin;

            @Override
            public int getOrders() {
                return 1_000 * scenarioIndex;
            }

            @Override
            public void setOrdersBegin() {
                ordersBegin = Instant.now();
            }

            @Override
            public void setOrdersEnd() {
                report.setOrdersDuration(Duration.between(ordersBegin, Instant.now()));
            }

            @Override
            public Order getOrder(int orderIdx) {
                return Scenario.super.getOrder(orderIdx);
            }

            @Override
            public int getMainScreens() {
                return 10_000;
            }

            @Override
            public void setMainScreensBegin() {
                mainScreensBegin = Instant.now();
            }

            @Override
            public void setMainScreensEnd() {
                report.setMainScreensDuration(Duration.between(mainScreensBegin, Instant.now()));
            }

            @Override
            public Report getReport() {
                return report;
            }

            @Override
            public MainScreenRequest getMainScreenRequest(int requestIdx) {
                return Scenario.super.getMainScreenRequest(requestIdx);
            }
        });
    }

    /* MODEL */

    interface Scenario {

        default int getOrders() {
            return 10;
        }

        void setOrdersBegin();

        void setOrdersEnd();

        default int getMainScreens() {
            return 10;
        }

        void setMainScreensBegin();

        void setMainScreensEnd();

        Report getReport();

        default Order getOrder(int orderIdx) {
            return new Order(UUID.randomUUID(), List.of());
        }

        default MainScreenRequest getMainScreenRequest(int requestIdx) {
            return new MainScreenRequest(0, 0);
        }
    }

    record Order(UUID merchant, List<UUID> products) {
    }

    record MainScreenRequest(double lat, double lng) {
    }

    @ToString
    @Data
    static class Report {
        private int scenarioIndex;
        private int orders;
        private int mainScreens;
        private Duration ordersDuration;
        private Duration mainScreensDuration;
    }

    /* GENERAL ROUND FLOW */

    Mono<Report> testForScenario(Scenario scenario) {
        var orders = Flux.fromStream(IntStream.range(0, scenario.getOrders()).boxed())
                .doFirst(scenario::setOrdersBegin)
                .map(scenario::getOrder)
                .flatMap(order -> Mono.fromFuture(makeOrder(order)))
                .last()
                .map(last -> {
                    scenario.setOrdersEnd();
                    return scenario;
                });
        var mainScreens = Flux.fromStream(IntStream.range(0, scenario.getMainScreens()).boxed())
                .doFirst(scenario::setMainScreensBegin)
                .map(scenario::getMainScreenRequest)
                .flatMap(r -> Mono.fromFuture(requestMainScreen(r)))
                .last()
                .map(r -> {
                    scenario.setMainScreensEnd();
                    return scenario;
                });
        return orders
                .then(mainScreens)
                .map(Scenario::getReport);
    }

    /* REQUESTS */

    static final UriComponentsBuilder URI_BUILDER = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(8080);

    static final URI CREATE_ORDER_URI = URI_BUILDER.cloneBuilder()
            .path("/e-catalog/api/v1/order")
            .build().toUri();

    static final UriComponentsBuilder MAIN_SCREEN_URI_BUILDER = URI_BUILDER.cloneBuilder()
            .path("/e-catalog/api/v1/mobile/mainscreen");

    final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .executor(Executors.newFixedThreadPool(10))
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    CompletableFuture<Order> makeOrder(Order order) {
        var request = HttpRequest.newBuilder()
                .uri(CREATE_ORDER_URI)
                .header(HttpHeaders.AUTHORIZATION, BEARER)
                .POST(noBody())
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    assertEquals(200, response.statusCode());
                    updateStatsForOrder(order);
                    return order;
                });
    }

    CompletableFuture<Boolean> requestMainScreen(MainScreenRequest mainScreenRequest) {
        var request = HttpRequest.newBuilder()
                .uri(MAIN_SCREEN_URI_BUILDER.cloneBuilder()
                        .queryParam("lat", mainScreenRequest.lat())
                        .queryParam("lng", mainScreenRequest.lng())
                        .build().toUri())
                .header(HttpHeaders.AUTHORIZATION, BEARER)
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    assertEquals(200, response.statusCode());
                    return true;
                });
    }

    /* STATS */

    final Map<UUID, Map<UUID, AtomicInteger>> stats = new ConcurrentHashMap<>();

    void updateStatsForOrder(Order order) {
        var merchantStats = stats.computeIfAbsent(
                order.merchant(), k -> new ConcurrentHashMap<>());
        for (var p : order.products()) {
            merchantStats.computeIfAbsent(p, k -> new AtomicInteger(0))
                    .incrementAndGet();
        }
    }
}
