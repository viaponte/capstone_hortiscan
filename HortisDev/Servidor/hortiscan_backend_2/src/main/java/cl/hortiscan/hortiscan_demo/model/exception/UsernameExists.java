package cl.hortiscan.hortiscan_demo.model.exception;

public class UsernameExists extends RuntimeException{
  public UsernameExists(String message){
    super(message);
  }
}
