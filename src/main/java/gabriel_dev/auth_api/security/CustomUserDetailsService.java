package gabriel_dev.auth_api.security;

import gabriel_dev.auth_api.model.User;
import gabriel_dev.auth_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// essa classe tem como função encapsular a lógica de busca de usuário através de suas credenciais

@Service
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // busca credenciais do usuário através do seu email passado no parâmetro
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // retorna usuário com email, senha e lista de roles (vazia no caso dessa aplicação)
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
