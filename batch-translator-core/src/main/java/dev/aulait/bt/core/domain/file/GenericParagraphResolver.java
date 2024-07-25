package dev.aulait.bt.core.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GenericParagraphResolver implements ParagraphResolver {

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

    for (String line : lines) {
      paragraph.append(line);
    }
    paragraphs.add(paragraph);

    return paragraphs;
  }

  @Override
  public String correct(Paragraph paragraph) {
    return paragraph.getTranslatedText();
  }
}
