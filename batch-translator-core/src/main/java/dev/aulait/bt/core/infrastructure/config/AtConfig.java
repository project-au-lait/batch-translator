package dev.aulait.bt.core.infrastructure.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
      throw new UncheckedIOException(e);
    }

    setApiKey(prop.getProperty("api_key"));
    setApiSecret(prop.getProperty("api_secret"));
    setUser(prop.getProperty("name"));
  }
}
