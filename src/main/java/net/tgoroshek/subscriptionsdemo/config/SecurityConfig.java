package net.tgoroshek.subscriptionsdemo.config;

import net.tgoroshek.subscriptionsdemo.controller.Router;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Для демонстрации включена самая удобная для работы в postman аутентификация. CSRF и CORS отключены
        http
                .httpBasic(
                        Customizer.withDefaults())
                .csrf(
                        c -> c.disable())
                .cors(
                        c -> c.disable());

        http.authorizeHttpRequests(
                r -> r.requestMatchers(Router.Users.REGISTER).permitAll()
                        .anyRequest().authenticated()
        );

        return http.build();
    }

}
