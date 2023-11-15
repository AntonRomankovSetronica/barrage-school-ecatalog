package net.barrage.school.java.ecatalog.app;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

class JsonProductSourceTest {

    @Test
    @SneakyThrows
    public void foo() {
        byte buf[] = new byte[1024];
        new URL("https://www.dropbox.com/scl/fi/mhq4vmv42x1hy5k430ae3/products.json?rlkey=00w6n2cfjk8p5wwizz6c4tckr&dl=0")
                .openStream().read(buf);
        System.out.println(new String(buf, StandardCharsets.UTF_8));
    }
}