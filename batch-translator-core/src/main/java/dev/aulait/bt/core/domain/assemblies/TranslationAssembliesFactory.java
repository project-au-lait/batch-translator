package dev.aulait.bt.core.domain.assemblies;

import dev.aulait.bt.core.domain.file.ParagraphGroup;
import dev.aulait.bt.core.domain.file.ParagraphResolver;
import dev.aulait.bt.core.domain.translation.Translator;
import dev.aulait.bt.core.infrastructure.command.TranslationEngine;
import dev.aulait.bt.core.infrastructure.util.FileTypeUtils;
import java.nio.file.Path;

public abstract class TranslationAssembliesFactory {

  public abstract ParagraphResolver getParagraphResolver();

  public abstract ParagraphGroup getParagraphGroup();

  public abstract Translator getTranslator();

  public static TranslationAssembliesFactory createTranslationAssemblies(
      Path file, TranslationEngine engine) {
    String fileType = FileTypeUtils.path2fileType(file);
    switch (fileType) {
      case "md":
        return new MarkdownTranslationAsbFactory(engine);
      case "adoc":
        return new AdocTranslationAsbFactory(engine);
      case "html":
        return new HtmlTranslationAsbFactory(engine);
      default:
        return new GenericTranslationAsbFactory(engine);
    }
  }
}
