package dev.aulait.bt.core.infrastructure.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestResourceUtils {

  public static Path res2path(Object owner, String resource) {
    try {
      return Path.of(owner.getClass().getResource(resource).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
