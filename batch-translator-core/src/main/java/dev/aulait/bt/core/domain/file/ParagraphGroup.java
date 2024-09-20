package dev.aulait.bt.core.domain.file;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ParagraphGroup {
  private static final String LINE_SEPARATER = "\n";
  private static final String DELIMITER_STRING = "== DELIMITER ==";
  private static final String DELIMITER = LINE_SEPARATER + DELIMITER_STRING + LINE_SEPARATER;

  private boolean useDelimiter;
  private List<Paragraph> group = new ArrayList<>();

  private void add(Paragraph paragraph) {
    group.add(paragraph);
  }

  private boolean isEmpty() {
    return group.isEmpty();
  }

  public ParagraphGroup(boolean useDelimiter) {
    this.useDelimiter = useDelimiter;
  }

  public String getAllText() {
    String eos = DELIMITER;
    if (!useDelimiter) {
      eos = LINE_SEPARATER + LINE_SEPARATER;
    }
    return group.stream().map(Paragraph::getText).collect(Collectors.joining(eos));
  }

  public void reduce(String text) {
    text = text.replaceAll("([^\n])\n?" + DELIMITER_STRING + "\n?([^\n])", "$1" + DELIMITER + "$2");
    int i = 0;
    for (String paragraphText : text.split(DELIMITER)) {
      group.get(i++).setTranslatedText(paragraphText);
    }
  }

  public List<ParagraphGroup> grouping(List<Paragraph> paragraphs) {
    List<ParagraphGroup> groups = new ArrayList<>();
    ParagraphGroup group = new ParagraphGroup(useDelimiter);

    int length = 0;
    for (Paragraph paragraph : paragraphs) {

      if (paragraph.isIgnored()) {
        continue;
      }

      group.add(paragraph);
      length += paragraph.getText().length();

      if (length > 1000) {
        groups.add(group);
        group = new ParagraphGroup(useDelimiter);
        length = 0;
      }
    }

    if (!group.isEmpty()) {
      groups.add(group);
    }

    return groups;
  }
}
