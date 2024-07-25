package dev.aulait.bt.core.domain.translation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SegmentSplitter {

  private SegmentSplitter() {}

  public static List<String> splitSegmentByLineBreak(String text, int maxTextByte) {
    // text が最大容量以下の場合は分割しない
    if (text.getBytes(StandardCharsets.UTF_8).length <= maxTextByte) {
      return new ArrayList<>((Arrays.asList(text)));
    }

    // text を改行で分割する
    List<String> texts = Arrays.asList(text.split("(?<=\r\n)|(?<=\r)|(?<=\n)"));
    StringBuilder sentence = new StringBuilder();
    List<String> results = new ArrayList<>();

    // 分割したtext を 最大容量以下 となるようにまとめる
    for (int i = 0; i < texts.size(); i++) {
      sentence.append(texts.get(i));
      int next = i + 1;
      if (next < texts.size()
          && (sentence.toString() + texts.get(next)).getBytes(StandardCharsets.UTF_8).length
              > maxTextByte) {
        results.add(sentence.toString());
        sentence.delete(0, sentence.length());
      }
    }

    if (StringUtils.isNotEmpty(sentence)) {
      results.add(sentence.toString());
    }

    return results;
  }
}
