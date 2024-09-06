package dev.aulait.bt.core.domain.translation;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GcpTranslator implements Translator {

  private static final Translate translate =
      TranslateOptions.newBuilder().setApiKey("xxxxx").build().getService();

  public String ja2en(String text) {
    return translate(text, "ja", "en");
  }

  @Override
  public String en2ja(String text) {
    return translate(text, "en", "ja");
  }

  private String translate(String text, String sourceLang, String targetLang) {
    String translatedText =
        translate
            .translate(
                text,
                TranslateOption.format("html"),
                Translate.TranslateOption.model("nmt"),
                TranslateOption.sourceLanguage(sourceLang),
                TranslateOption.targetLanguage(targetLang))
            .getTranslatedText();

    log.debug("Original Text: {}", text);
    log.debug("Translated Text: {}", translatedText);

    return translatedText;
  }
}
