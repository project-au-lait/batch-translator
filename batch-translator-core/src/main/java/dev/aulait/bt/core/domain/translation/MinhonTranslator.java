package dev.aulait.bt.core.domain.translation;

import com.jayway.jsonpath.JsonPath;
import dev.aulait.bt.core.infrastructure.config.AtConfig;
import dev.aulait.bt.core.infrastructure.web.WebClient;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MinhonTranslator implements Translator {

  private final WebClient webClient;

  private final AtConfig config;

  public String ja2en(String text) {
    String accessToken = getAccessToken();
    return translate(accessToken, text, "https://mt-auto-minhon-mlt.ucri.jgn-x.jp/api/mt/generalNT_ja_en/");
  }

  @Override
  public String en2ja(String text) {
    String accessToken = getAccessToken();
    return translate(accessToken, text, "https://mt-auto-minhon-mlt.ucri.jgn-x.jp/api/mt/generalNT_en_ja/");
  }

  private String getAccessToken() {
    Map<String, String> params = new HashMap<>();

    params.put("grant_type", "client_credentials");
    params.put("client_id", config.getApiKey());
    params.put("client_secret", config.getApiSecret());

    String response = webClient.post("https://mt-auto-minhon-mlt.ucri.jgn-x.jp/oauth2/token.php", params);
    return JsonPath.read(response, "$.access_token");
  }

  private String translate(String accessToken, String text, String apiUrl) {
    Map<String, String> params = new HashMap<>();

    params.put("access_token", accessToken);
    params.put("key", config.getApiKey());
    params.put("name", config.getUser());
    params.put("type", "json");
    params.put("text", text);

    String response = webClient.post(apiUrl, params);
    String translatedText = JsonPath.read(response, "$.resultset.result.text");

    log.debug("Original Text: {}", text);
    log.debug("Translated Text: {}", translatedText);

    return translatedText;
  }
}
