package dev.aulait.bt.core.infrastructure.web;

import dev.aulait.bt.core.infrastructure.config.AtConfig;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@RequiredArgsConstructor
public class ApacheHttpWebClient implements WebClient {

  private final AtConfig config;

  private OAuthConsumer consumer;

  @Override
  public String post(String url, Map<String, String> params) {

    HttpPost request = new HttpPost(url);

    List<NameValuePair> urlParameters =
        params.entrySet().stream()
            .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

    request.setEntity(new UrlEncodedFormEntity(urlParameters, StandardCharsets.UTF_8));

    sign(request);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(request)) {

      return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  synchronized void sign(HttpRequest request) {
    if (consumer == null) {
      consumer = new CommonsHttpOAuthConsumer(config.getApiKey(), config.getApiSecret());
    }

    try {
      consumer.sign(request);
    } catch (OAuthMessageSignerException
        | OAuthExpectationFailedException
        | OAuthCommunicationException e) {
      throw new IllegalStateException(e);
    }
  }
}
