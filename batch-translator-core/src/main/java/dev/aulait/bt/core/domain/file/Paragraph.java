package dev.aulait.bt.core.domain.file;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class Paragraph {

  @Setter(AccessLevel.PRIVATE)
  private StringBuilder text = new StringBuilder();

  private boolean ignored = false;
  private String escapePrefix = "";
  private String translatedText = "";

  public void append(String line) {
    if (text.length() > 0) {
      text.append(System.lineSeparator());
    }
    text.append(line);
  }

  public String getText() {
    return text.toString();
  }
}
