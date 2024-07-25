package dev.aulait.bt.core.domain.translation;

import dev.aulait.bt.core.infrastructure.command.TranslationEngine;
import dev.aulait.bt.core.infrastructure.command.TranslationMode;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslationSpecResolver {

  public static Stream<TranslationSpec> toSpecs(
      String source,
      String target,
      TranslationMode mode,
      String filePattern,
      TranslationEngine engine) {

    Path inPath = Path.of(source);
    Path outPath = Path.of(target);

    // TODO Validation
    if (inPath.toFile().isFile()) {
      return Stream.of(new TranslationSpec(inPath, outPath, mode, true, engine));
    } else {
      return toDirSpecs(inPath, outPath, mode, filePattern, engine);
    }
  }

  static Stream<TranslationSpec> toDirSpecs(
      Path inDir, Path outDir, TranslationMode mode, String filePattern, TranslationEngine engine) {

    Pattern filePatternObj =
        filePattern == null
            ? null
            : Pattern.compile(filePattern.replace(".", "\\.").replace("*", ".*"));

    try {
      return Files.walk(inDir)
          .filter(path -> path.toFile().isFile())
          .map(
              inFile -> {
                boolean isTarget =
                    filePatternObj == null
                        ? true
                        : filePatternObj.matcher(inFile.getFileName().toString()).matches();
                Path outFile = outDir.resolve(inDir.relativize(inFile));
                return new TranslationSpec(inFile, outFile, mode, isTarget, engine);
              })
          .collect(Collectors.toList())
          .stream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
