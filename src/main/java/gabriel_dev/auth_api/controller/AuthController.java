package gabriel_dev.auth_api.controller;

import gabriel_dev.auth_api.dto.LoginRequestDTO;
import gabriel_dev.auth_api.dto.ResponseDTO;
import gabriel_dev.auth_api.dto.SignupRequestDTO;
import gabriel_dev.auth_api.model.User;
import gabriel_dev.auth_api.repository.UserRepository;
import gabriel_dev.auth_api.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody LoginRequestDTO inputData) {
        // busca usuário no banco via JPA
        User user = this.userRepository.findByEmail(
                inputData.email()).orElseThrow(
                        () -> new UsernameNotFoundException("User not found")
        );

        // senha correta, gera token e manda na resposta dentro do DTO pertinente
        if (passwordEncoder.matches(inputData.password(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }
        // senha incorreta = 403 Bad Request
        return ResponseEntity.badRequest().build();

    }

    @PostMapping("signup")
    public ResponseEntity login(@RequestBody SignupRequestDTO inputData) {
        // verifica se cadastro ainda não foi realizado
        Optional<User> user = this.userRepository.findByEmail(
                inputData.email());

        // usuário já cadastrado = 403 Bad Request
        if (user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        // caso contrário, cria novo usuário
        User newUser = new User();
        newUser.setName(inputData.name());
        newUser.setEmail(inputData.email());
        newUser.setPassword(passwordEncoder.encode(inputData.password()));
        // salva no db via JPA
        this.userRepository.save(newUser);
        // gera jwt
        String token = tokenService.generateToken(newUser);
        return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));


    }


}
