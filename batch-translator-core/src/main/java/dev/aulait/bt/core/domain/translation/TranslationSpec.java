package dev.aulait.bt.core.domain.translation;

import dev.aulait.bt.core.infrastructure.command.TranslationEngine;
import dev.aulait.bt.core.infrastructure.command.TranslationMode;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationSpec {

  private Path inputFile;

  private Path outputFile;

  private TranslationMode mode;

  private boolean target;

  private TranslationEngine engine;
}
