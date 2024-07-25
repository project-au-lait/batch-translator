package dev.aulait.bt.core.domain.file;

import java.nio.file.Path;
import java.util.List;

public interface ParagraphResolver {
  List<Paragraph> resolve(Path file);

  String correct(Paragraph paragraph);
}
