package dev.aulait.bt.core.infrastructure.command;

public enum TranslationEngine {
  MINHON,
  AWS;

  public static TranslationEngine parse(String engine) {
    return TranslationEngine.valueOf(engine.toUpperCase());
  }
}
