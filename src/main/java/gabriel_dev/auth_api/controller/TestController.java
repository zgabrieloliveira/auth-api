package gabriel_dev.auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// classe de teste da aplicação
// a requisição só vai ser aceita se haver autenticação prévia
// em virtude das configurações implementadas em SecurityFilterChain

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Autenticado!");
    }
}
