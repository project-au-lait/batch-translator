package dev.aulait.bt.core.domain.translation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

@RequiredArgsConstructor
public class AwsTranslator implements Translator {

  private static final TranslateClient translateClient =
      TranslateClient.builder()
          .credentialsProvider(ProfileCredentialsProvider.create())
          .region((new AwsProfileRegionProvider()).getRegion())
          .build();

  @Override
  public String ja2en(String text) {
    return translate(text, "ja", "en");
  }

  @Override
  public String en2ja(String text) {
    return translate(text, "en", "ja");
  }

  private String translate(String text, String sourceLanguageCode, String targetLanguageCode) {
    if (StringUtils.isEmpty(text)) {
      return "";
    }
    // テキストを Amazon Translate が翻訳可能なサイズに分割する.
    List<String> sentences = SegmentSplitter.splitSegmentByLineBreak(text, 5000);
    List<TranslateTextRequest> requests = new ArrayList<>();
    sentences.stream()
        .map(
            sentence ->
                TranslateTextRequest.builder()
                    .text(sentence)
                    .sourceLanguageCode(sourceLanguageCode)
                    .targetLanguageCode(targetLanguageCode)
                    .build())
        .forEachOrdered(requests::add);

    List<TranslateTextResponse> results = new ArrayList<>();
    requests.stream().map(translateClient::translateText).forEachOrdered(results::add);

    StringBuilder translatedText = new StringBuilder();
    results.forEach(result -> translatedText.append(result.translatedText()));

    return translatedText.toString();
  }
}
