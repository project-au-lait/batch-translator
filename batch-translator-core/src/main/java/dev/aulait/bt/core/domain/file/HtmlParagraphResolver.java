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

  private static final List<String> IGNORE_TAGS =
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
    Paragraph paragraph = new Paragraph();

    String openIgnoreTag = "";

    for (String line : lines) {
      // If there is no tag element, put the whole line into the paragraph.
      // Even if there is a tag element, if the translation-ignore tag is being expanded and there
      // is no closing tag, the entire line is placed in the paragraph.
      if (StringUtils.containsNone(line, "<")
          || (StringUtils.isNotEmpty(openIgnoreTag)
              && !containsCloseIgnoreTag(line, openIgnoreTag))) {
        paragraph = prepareParagraph(paragraphs, paragraph, StringUtils.isNotEmpty(openIgnoreTag));
        appendParagraph(paragraph, line, true);
        continue;
      }

      // Tags ignored for translation, non-tag Text targeted for translation.
      // If translation-ignored tags appear, all translation is ignored until the closing tag
      // appears.
      Matcher matcher = Pattern.compile("<[^>]+>").matcher(line);
      int lineCursor = 0;

      while (matcher.find()) {
        // text before tag
        String textBeforeTag = line.substring(lineCursor, matcher.start());
        if (StringUtils.isNotEmpty(textBeforeTag)) {
          paragraph =
              prepareParagraph(paragraphs, paragraph, StringUtils.isNotEmpty(openIgnoreTag));
          appendParagraph(paragraph, textBeforeTag, lineCursor == 0);
        }

        // open or close translation ignore tag
        openIgnoreTag = ignoreTagSet(matcher.group(), openIgnoreTag);

        // tag
        paragraph = prepareParagraph(paragraphs, paragraph, true);
        appendParagraph(paragraph, matcher.group(), matcher.start() == 0);

        lineCursor = matcher.end();
      }

      // text after tag
      String textAfterTag = line.substring(lineCursor, line.length());
      if (StringUtils.isNotEmpty(textAfterTag)) {
        paragraph = prepareParagraph(paragraphs, paragraph, StringUtils.isNotEmpty(openIgnoreTag));
        appendParagraph(paragraph, textAfterTag, false);
      }
    }

    paragraphs.add(paragraph);
    paragraphs.forEach(p -> log.info("paragraph: {}", p));
    return paragraphs;
  }

  @Override
  public String correct(Paragraph paragraph) {
    return paragraph.isIgnored() ? paragraph.getText() : paragraph.getTranslatedText();
  }

  private Paragraph prepareParagraph(
      List<Paragraph> paragraphs, Paragraph paragraph, boolean ignore) {
    if (StringUtils.isNotBlank(paragraph.getText()) && (paragraph.isIgnored() ^ ignore)) {
      paragraphs.add(paragraph);
      paragraph = new Paragraph();
    }
    paragraph.setIgnored(ignore);

    return paragraph;
  }

  private void appendParagraph(Paragraph paragraph, String text, boolean bol) {
    if (!paragraph.isIgnored()) {
      text = process4translate(text);
    }

    if (bol) {
      paragraph.append(text);
    } else {
      paragraph.inlineAppend(text);
    }
  }

  private String ignoreTagSet(String tag, String ignoreTag) {
    Matcher matcher = Pattern.compile("</?([\\w]+)").matcher(tag);
    String tagName = matcher.find() ? matcher.group(1) : "";

    if (containsCloseIgnoreTag(tag, ignoreTag)) {
      ignoreTag = "";
    } else if (StringUtils.isEmpty(ignoreTag) && IGNORE_TAGS.contains(tagName)) {
      ignoreTag = tagName;
    }

    return ignoreTag;
  }

  private boolean containsCloseIgnoreTag(String text, String ignoreTag) {
    if (StringUtils.isEmpty(ignoreTag)) return false;
    return text.contains("</" + ignoreTag);
  }

  private String process4translate(String text) {
    return text.replaceAll("(@[\\w]+)(\\S)", "$1 $2");
  }
}
