package dev.aulait.bt.core.infrastructure.util;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileTypeUtils {

  public static String path2fileType(Path path) {
    String filePath = path.toString();
    return filePath.substring(filePath.lastIndexOf(".") + 1);
  }
}
