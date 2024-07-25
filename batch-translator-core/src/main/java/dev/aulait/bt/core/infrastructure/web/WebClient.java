package dev.aulait.bt.core.infrastructure.web;

import java.util.Map;

public interface WebClient {

  String post(String url, Map<String, String> params);
}
