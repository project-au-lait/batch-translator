package dev.aulait.bt.core.domain.translation;

import dev.aulait.bt.core.infrastructure.command.TranslationMode;

public interface Translator {

  default String translate(TranslationMode mode, String text) {
    switch (mode) {
      case JA2EN:
        return ja2en(text);
      case EN2JA:
        return en2ja(text);
      default:
        return "NOP";
    }
  }

  String ja2en(String text);

  String en2ja(String text);
}
