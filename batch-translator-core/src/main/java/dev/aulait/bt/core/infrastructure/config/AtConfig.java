package dev.aulait.bt.core.infrastructure.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class AtConfig {

  private static final String FILE_NAME = "batch-translator.properties";

  private String apiKey;

  private String apiSecret;

  private String user;

  public static AtConfig load() {
    AtConfig config = new AtConfig();
    URL configUrl = resolve();
    config.loadFromPropertyFile(configUrl);
    return config;
  }

  static URL resolve() {
    // TODO if file doesn't exit.
    Path configPath =
        Path.of(System.getProperty("user.home")).resolve(".aulait").resolve(FILE_NAME);

    try {
      return configPath.toUri().toURL();
    } catch (MalformedURLException e) {
      throw new IllegalStateException(e);
    }
  }

  void loadFromPropertyFile(URL configUrl) {
    log.info("Read config: {}", configUrl);

    Properties prop = new Properties();

    try {
      prop.load(configUrl.openStream());
    } catch (IOException e) {
      log.debug("Property file could not be loaded.", e);
    }

    setApiKey(prop.getProperty("api_key"));
    setApiSecret(prop.getProperty("api_secret"));
    setUser(prop.getProperty("name"));
  }

  public void ckProp4Minhon() {
    if (StringUtils.isAnyBlank(apiKey, apiSecret, user)) {
      throw new IllegalArgumentException("Required property is not set for engine:minhon.");
    }
  }
}
