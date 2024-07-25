package dev.aulait.bt.core.infrastructure.command;

public enum TranslationMode {
  EN2JA,
  JA2EN;

  public static TranslationMode parse(String mode) {
    return TranslationMode.valueOf(mode.toUpperCase());
  }

  public String getTargetLang() {
    return name().substring(name().indexOf("2") + 1).toLowerCase();
  }
}
