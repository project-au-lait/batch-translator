package dev.aulait.bt.core.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class HtmlParagraphResolver implements ParagraphResolver {

  private static final List<String> INNER_IGNORE_TAGS =
      Arrays.asList("style", "script", "colgroup", "code");

  @Override
  public List<Paragraph> resolve(Path file) {

    List<String> lines = null;

    try {
      lines = Files.readAllLines(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    List<Paragraph> paragraphs = new ArrayList<>();
    Pattern tagPattern = Pattern.compile("<[^>]+>");
    String openIgnoneTag = "";

    Paragraph paragraph = new Paragraph();

    for (String line : lines) {

      // タグ要素がなければlineごとparagraphへ
      // タグ要素があっても、無視タグ内で閉じタグがない場合もlineごとparagraphへ
      if (!line.contains("<")
          || (!openIgnoneTag.isEmpty() && !line.contains("</" + openIgnoneTag))) {
        paragraph.setIgnored(!openIgnoneTag.isEmpty());
        paragraph.append(line);
        continue;
      }

      // タグがある場合はタグを翻訳無視、innnerTextを翻訳対象
      // 無視タグ出現の場合は閉じタグ出現まで翻訳無視
      int lineCursor = 0;
      Matcher matcher = tagPattern.matcher(line);

      while (matcher.find()) {
        String tag = matcher.group();
        String tagName = getTagName(tag);

        // タグ前のテキスト検出
        String beforeTagText = line.substring(lineCursor, matcher.start());
        if (!beforeTagText.isBlank()) {
          paragraph.setIgnored(!openIgnoneTag.isEmpty());
          if (!paragraph.isIgnored()) {
            beforeTagText = beforeTagText.replaceAll("(@[A-Za-z]*)(\\S)", "$1 $2");
          }
          if (lineCursor == 0) {
            paragraph.append(beforeTagText);
          } else {
            paragraph.inlineAppend(beforeTagText);
          }
          if (openIgnoneTag.isEmpty() && !paragraph.getText().isEmpty()) {
            paragraphs.add(paragraph);
            paragraph = new Paragraph();
          }
        }

        // 無視タグ開始
        if (openIgnoneTag.isEmpty() && INNER_IGNORE_TAGS.contains(tagName) && !tag.contains("/")) {
          openIgnoneTag = tagName;
          if (!paragraph.getText().isEmpty()) {
            paragraphs.add(paragraph);
            paragraph = new Paragraph();
          }
        }

        // 無視タグ終了
        if (tag.contains("/") && openIgnoneTag.equals(tagName)) {
          openIgnoneTag = "";
        }

        // タグの処理
        if (!paragraph.isIgnored() && !paragraph.getText().isEmpty()) {
          paragraphs.add(paragraph);
          paragraph = new Paragraph();
        }
        paragraph.setIgnored(true);
        paragraph.inlineAppend(tag);
        if (openIgnoneTag.isEmpty() && !paragraph.getText().isEmpty()) {
          paragraphs.add(paragraph);
          paragraph = new Paragraph();
        }

        lineCursor = matcher.end();
      }

      // タグ後のテキスト検出
      String afterTagText = line.substring(lineCursor, line.length());
      if (!afterTagText.isEmpty()) {
        if (openIgnoneTag.isEmpty()) {
          paragraph = new Paragraph();
        }

        paragraph.setIgnored(!openIgnoneTag.isEmpty());

        if (!paragraph.isIgnored()) {
          afterTagText = afterTagText.replaceAll("(@[A-Za-z]*)(\\S)", "$1 $2");
        }

        paragraph.inlineAppend(afterTagText);
      }
    }

    paragraphs.forEach(p -> log.info("paragraph: {}", p));
    return paragraphs;
  }

  @Override
  public String correct(Paragraph paragraph) {
    return correct(
        paragraph.getText(),
        paragraph.getTranslatedText(),
        paragraph.getEscapePrefix(),
        paragraph.isIgnored());
  }

  public String correct(
      String originalText, String translatedText, String escapePrefix, boolean ignored) {

    if (ignored) {
      return originalText;
    }

    // 置換用文字列を元に戻す
    if (StringUtils.isNotBlank(translatedText)) {
      translatedText = translatedText.replace('“', '\"').replace('”', '\"');
    }

    if (escapePrefix.isEmpty()) {
      return translatedText;
    }

    StringBuilder correctText = new StringBuilder();
    correctText.append(escapePrefix);
    correctText.append(" ");
    correctText.append(translatedText);
    return correctText.toString();
  }

  private static String getTagName(String tag) {
    Pattern pattern = Pattern.compile("</?([a-zA-Z0-9]+)");
    Matcher matcher = pattern.matcher(tag);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }
}
