package dev.aulait.bt.core.domain.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.bt.core.infrastructure.util.TestResourceUtils;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MarkdownParagraphResolverTests {

  MarkdownParagraphResolver resolver = new MarkdownParagraphResolver();

  @Test
  public void test() {
    Path input = TestResourceUtils.res2path(this, "input.md");

    List<Paragraph> paragraphs = resolver.resolve(input);

    System.out.println(paragraphs);

    assertEquals(6, paragraphs.size());
  }
}
