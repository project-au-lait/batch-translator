package dev.aulait.bt.core.domain.translation;

import dev.aulait.bt.core.infrastructure.command.TranslationEngine;
import dev.aulait.bt.core.infrastructure.config.AtConfig;
import dev.aulait.bt.core.infrastructure.web.ApacheHttpWebClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicTranslatorFactory {

  private static final AtConfig config = AtConfig.load();
  private static final MinhonTranslator minhonTranslator =
      new MinhonTranslator(new ApacheHttpWebClient(config), config);
  private static final AwsTranslator awsTranslator = new AwsTranslator();
  private static final GcpTranslator gcpTranslator = new GcpTranslator();

  public static Translator createTranslator(TranslationEngine engine) {
    return createTranslator(String.valueOf(engine));
  }

  public static Translator createTranslator(String engineName) {
    switch (engineName.toUpperCase()) {
      case "MINHON":
        return minhonTranslator;
      case "AWS":
        return awsTranslator;
      case "GCP":
        return gcpTranslator;
      default:
        return minhonTranslator;
    }
  }
}
