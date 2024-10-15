package cl.hortiscan.hortiscan_demo.model.auth;

import lombok.Data;

@Data
public class AuthRequest {
  private String username;
  private String password;
}
