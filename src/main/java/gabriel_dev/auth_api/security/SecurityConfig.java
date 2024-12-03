package gabriel_dev.auth_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;


// essa classe amarra todas as configurações de segurança feitas no package security da aplicação
// construindo uma corrente de filtros até a requisição ser autenticada ou não

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private CustomUserDetailsService customUserDetailsService;
    private SecurityFilter securityFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, SecurityFilter securityFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.securityFilter = securityFilter;
    }

    // todas as requisições passarão por uma série de filtros de segurança a fim de bloquear acesso a recursos restritos
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // API REST (cada requisição passará por autenticação via JWT individualmente)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // apenas as páginas de login e de cadastro poderão ser acessadas sem autenticação
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/signup").permitAll()
                        .anyRequest().authenticated()
                )
                // o filtro customizado na classe SecurityFilter será aplicado antes do filtro padrão do Spring (UsernamePasswordAuthenticationFilter)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
