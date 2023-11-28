package net.barrage.school.java.ecatalog.config;

import com.nimbusds.jwt.JWTParser;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.decoder(jwtDecoder())));
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return new JwtDecoder() {
            @Override
            @SneakyThrows
            public Jwt decode(String token) throws JwtException {
                var jwt = JWTParser.parse(token);
                var now = Instant.now();
                return new Jwt(
                        token,
                        now.minus(1, ChronoUnit.HOURS),
                        now.plus(1, ChronoUnit.HOURS),
                        jwt.getHeader().toJSONObject(),
                        jwt.getJWTClaimsSet().getClaims()
                );
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> Optional.ofNullable(jwt.getClaim("b-roles"))
                .map(roles -> Arrays.asList(roles.toString().split(",")))
                .orElseGet(List::of).stream()
                .map(r -> new SimpleGrantedAuthority(r.trim()))
                .collect(Collectors.toSet())
        );
        return converter;
    }
}
