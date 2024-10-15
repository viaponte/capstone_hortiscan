package cl.hortiscan.hortiscan_demo.model.auth;

import lombok.Data;

@Data
public class AuthResponse {
  private String jtw;

  public AuthResponse(String jtw) {
    this.jtw = jtw;
  }
}
