package dev.aulait.bt.core.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class HtmlParagraphResolver implements ParagraphResolver {

  private static final String TAG_BR = "\\<br\\>";
  private static final String REGEX_LINEBREAK = "\\r\\n|\\r|\\n";
  private static final String REGEX_P_TABLEBLOCK =
      "(^.*\\<p class\\=\"tableblock\"\\>)(.*)(\\<\\/p\\>.*)";
  private static final Pattern PATTERN_P_TABLEBLOCK = Pattern.compile(REGEX_P_TABLEBLOCK);

  @Override
  public List<Paragraph> resolve(Path file) {

    List<String> lines = null;

    try {
      lines = Files.readAllLines(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    List<Paragraph> paragraphs = new ArrayList<>();
    Paragraph paragraph = new Paragraph();
    // 処理中のテキストがthタグ、もしくは、tdタグ内に存在するかを判定するフラグ
    boolean inTableData = false;
    boolean inTag = false;
    // thタグ、tdタグ内のテキストを一時的に格納する変数
    StringBuilder tableData = new StringBuilder();

    for (String line : lines) {

      // 画像は翻訳不要。srcが大きすぎる場合にAPIリクエスト文字列のサーズオーバーを防ぐため
      if (line.contains("<img")) {
        paragraph = resetParagraph(paragraphs, paragraph);
        paragraph.setIgnored(true);
        inTag = true;
      }
      // 以下のタグが次にくるまで翻訳しない
      // ・</style>
      // ・</script>
      // ・</colgroup>
      if (line.contains("<style>") || line.contains("<script>") || line.contains("<colgroup>")) {
        if (paragraph.getText() != null && !paragraph.getText().isEmpty()) {
          paragraph = resetParagraph(paragraphs, paragraph);
        }
        paragraph.setIgnored(true);
      }

      if (paragraph.isIgnored()
          && (line.contains("</style>")
              || line.contains("</script>")
              || line.contains("</colgroup>"))
              || (inTag && line.contains(">"))) {
        paragraph.append(line);
        paragraph = resetParagraph(paragraphs, paragraph);
        inTag = false;
        continue;
      }
      // コードブロックの最後でparagraphに追加
      if (line.contains("</code>")) {
        paragraph.append(line);
        paragraphs.add(paragraph);
        paragraph = new Paragraph();
        continue;
      }
      if (!paragraph.isIgnored()) {
        // 翻訳時の記法崩れ防止のため、thタグ、tdタグを1行にまとめて整形する
        if (line.contains("</th>") || line.contains("</td>")) {
          String tableDataStr = (tableData.append(line.replaceAll(REGEX_LINEBREAK, ""))).toString();
          // 暫定対応
          // th,tdタグ内の <p class="tableblock"></p> に囲まれたテキストを翻訳した際、
          // テキストがtdタグ外に出力される事象を防ぐため、テキストを置換用文字列で囲む
          if (tableDataStr.contains("<p class=\"tableblock\">")) {
            Matcher matcher = PATTERN_P_TABLEBLOCK.matcher(tableDataStr);
            if (matcher.matches()) {
              // 暫定対応
              // tdタグ内のpタグ内のテキストにbrタグが存在する場合、
              // brタグがtdタグの外へ出力される事象を防ぐため、brタグを削除する
              if (tableDataStr.contains("<br>")) {
                tableDataStr = tableDataStr.replaceAll(TAG_BR, "");
              }
            }
          }
          paragraph.append(tableDataStr);
          tableData.delete(0, tableData.length());
          inTableData = false;
          continue;
        }
        // 空行は翻訳しない
        if (!paragraph.isIgnored() && line.isBlank()) {
          paragraphs.add(paragraph);
          paragraph = getEmptyParagraph();
          paragraph = resetParagraph(paragraphs, paragraph);
          continue;
        }
        // コードブロックは翻訳されないようにする
        if (!paragraph.isIgnored() && line.contains("<code")) {
          paragraphs.add(paragraph);
          paragraph = new Paragraph();
          paragraph.append(line);
          paragraph.setIgnored(true);
          // コードブロックが1行で完結した場合にはそのままparagraphsに追加
          if (line.contains("</code")) {
            paragraphs.add(paragraph);
            paragraph = new Paragraph();
          }
          continue;
        }
        // 翻訳時の記法崩れ防止のため、thタグ、tdタグは1行にまとめる
        if (inTableData || line.contains("<th") || line.contains("<td")) {
          tableData.append(line.replaceAll(REGEX_LINEBREAK, ""));
          inTableData = true;
          continue;
        }
        // テキスト内に@XxxYyyの文字列が存在し、後ろに空白がない場合翻訳漏れが発生するため空白を追加
        line = line.replaceAll("(@[A-Za-z]*)(\\S)", "$1 $2");
      }
      paragraph.append(line);
    }
    paragraphs.add(paragraph);

    return paragraphs;
  }

  private Paragraph getEmptyParagraph() {
    Paragraph paragraph = new Paragraph();
    paragraph.setIgnored(true);
    return paragraph;
  }

  private Paragraph resetParagraph(List<Paragraph> paragraphs, Paragraph paragraph) {
    if (!paragraph.getText().isEmpty() || paragraph.isIgnored()) {
      paragraphs.add(paragraph);
    }
    return new Paragraph();
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
      translatedText =
          translatedText
              .replace('“', '\"').replace('”', '\"');
    }

    if (escapePrefix.isEmpty()) {
      return getCorrectHtml(originalText, translatedText);
    }

    StringBuilder correctText = new StringBuilder();
    correctText.append(escapePrefix);
    correctText.append(" ");
    correctText.append(translatedText);
    return correctText.toString();
  }

  private String getCorrectHtml(String originalText, String translatedText) {
    // 元のHTMLのタグ構造をListで取得
    List<String> tags = extractTags(originalText);

    StringBuilder correctedHtml = new StringBuilder();
    int index = 0;

    // 翻訳後のテキストを元のHTMLのタグ構造になるように組み直す
    for (String originalTag : tags) {
        String tagType = getTagType(originalTag);

        int tagStartIndex = findTagIndex(translatedText, tagType, index);
        if (tagStartIndex != -1) {
            int tagEndIndex = translatedText.indexOf(">", tagStartIndex) + 1;

            // タグとその内包するテキストをappend
            correctedHtml.append(originalTag);
            String textWithinTag = getTextWithinTag(translatedText, tagEndIndex);
            correctedHtml.append(textWithinTag);

            // appendしたタグおよびテキストは翻訳後HTMLから削除
            translatedText = translatedText.substring(0, tagStartIndex) + translatedText.substring(tagEndIndex + textWithinTag.length());
        } else {
            correctedHtml.append(originalTag).append("\n");
        }
    }
    return correctedHtml.toString();
  }

  private List<String> extractTags(String html) {
    ArrayList<String> tags = new ArrayList<>();
    Pattern pattern = Pattern.compile("<[^>]+>");
    Matcher matcher = pattern.matcher(html);

    while (matcher.find()) {
        tags.add(matcher.group());
    }

    return tags;
  }

  private String getTextWithinTag(String html, int startIndex) {
    int nextTagStart = html.indexOf("<", startIndex);
    if (nextTagStart == -1) {
        return html.substring(startIndex);
    }
    return html.substring(startIndex, nextTagStart);
  }

  private static String getTagType(String tag) {
    Pattern pattern = Pattern.compile("</?([a-zA-Z0-9]+)");
    Matcher matcher = pattern.matcher(tag);
    if (matcher.find()) {
        return matcher.group(1);
    }
    return "";
  }

  private int findTagIndex(String html, String tagType, int startIndex) {
    Pattern pattern = Pattern.compile("<\\/?\\s*" + tagType + "[^>]*>");
    Matcher matcher = pattern.matcher(html);
    if (matcher.find(startIndex)) {
        return matcher.start();
    }
    return -1;
  }

}
