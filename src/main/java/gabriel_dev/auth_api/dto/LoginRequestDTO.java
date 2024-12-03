package gabriel_dev.auth_api.dto;

public record LoginRequestDTO (
   String email,
   String password
) {}
