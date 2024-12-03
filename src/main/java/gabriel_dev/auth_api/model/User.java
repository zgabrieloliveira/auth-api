package gabriel_dev.auth_api.model;

import jakarta.persistence.*;
import lombok.Data;

// @Entity determina que essa classe será uma tabela no DB
// @Table indica qual tabela ela representará

@Entity
@Table(name="user")
@Data
public class User {

    // chave primária da tabela,
    // id gerado como UUID (Universally Unique IDentifier), então deve ser string
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String email;
    private String password;
}
