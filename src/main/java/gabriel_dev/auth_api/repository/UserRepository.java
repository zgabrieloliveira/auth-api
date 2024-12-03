package gabriel_dev.auth_api.repository;

import gabriel_dev.auth_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


// @Repository indica que essa interface responsável por manipular a tabela User do DB via JPA
// deve ser uma interface que herda um JPARepository, passando a tabela a ser manipulada e o tipo da sua chave primária (Id)

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findByEmail(String email);
}
