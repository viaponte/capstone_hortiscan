package cl.hortiscan.hortiscan_demo.configuration;

import java.util.Map;

import lombok.Data;

@Data
public class OnlyOfficeConfig {
  private String documentType;
  private Map<String, Object> document;
  private Map<String, Object> editorConfig;
  private String token;
}




