package dev.aulait.bt.core.infrastructure.util;

import java.util.Scanner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtils {

  public static String res2str(Object owner, String resourceName) {
    StringBuilder sb = new StringBuilder();

    try (Scanner scanner = new Scanner(owner.getClass().getResourceAsStream(resourceName))) {
      while (scanner.hasNextLine()) {
        sb.append(scanner.nextLine());
        sb.append(System.lineSeparator());
      }
    }

    return sb.toString();
  }
}
