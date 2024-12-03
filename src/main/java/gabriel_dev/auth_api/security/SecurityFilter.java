package gabriel_dev.auth_api.security;

import gabriel_dev.auth_api.model.User;
import gabriel_dev.auth_api.repository.UserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    TokenService tokenService;
    UserRepository userRepository;

    @Autowired
    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }


    // aplica filtro em cada requisição, em busca do token de autenticação
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        var loginToken = tokenService.validateToken(token);

        // se o token for válido, busca usuário no bd e configura sua autenticação
        if (loginToken != null) {
            User user = userRepository.findByEmail(loginToken).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            // não há roles nessa aplicação simples (padrão ROLE_USER)
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            // através do usuário enontrado, cria um objeto de autenticação que será armazenado no contexto de segurança do Spring
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // pega token no header, retorna null se não achar
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

}
