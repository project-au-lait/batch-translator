package dev.aulait.bt.core.domain.assemblies;

import dev.aulait.bt.core.domain.file.AsciiDocParagraphResolver;
import dev.aulait.bt.core.domain.file.ParagraphGroup;
import dev.aulait.bt.core.domain.file.ParagraphResolver;
import dev.aulait.bt.core.domain.translation.AdocTranslator;
import dev.aulait.bt.core.domain.translation.Translator;
import dev.aulait.bt.core.infrastructure.command.TranslationEngine;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AdocTranslationAsbFactory extends TranslationAssembliesFactory {

  private final TranslationEngine engine;

  @Override
  public ParagraphResolver getParagraphResolver() {
    return new AsciiDocParagraphResolver();
  }

  @Override
  public ParagraphGroup getParagraphGroup() {
    return new ParagraphGroup(false);
  }

  @Override
  public Translator getTranslator() {
    return new AdocTranslator(this.engine);
  }
}
