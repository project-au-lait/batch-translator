package dev.aulait.bt.core.domain.translation;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GcpTranslator implements Translator {

  @Override
  public String ja2en(String text) {
    return translate(text, "ja", "en");
  }

  @Override
  public String en2ja(String text) {
    return translate(text, "en", "ja");
  }

  private String translate(String text, String sourceLang, String targetLang) {
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    String translatedText =
        translate
            .translate(
                text,
                TranslateOption.format("text"),
                TranslateOption.sourceLanguage(sourceLang),
                TranslateOption.targetLanguage(targetLang))
            .getTranslatedText();

    log.debug("Original Text: {}", text);
    log.debug("Translated Text: {}", translatedText);

    return translatedText;
  }
}
