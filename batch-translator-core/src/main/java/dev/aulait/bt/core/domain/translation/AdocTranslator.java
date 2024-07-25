package dev.aulait.bt.core.domain.translation;

import dev.aulait.bt.core.domain.asciidoctorj.AdocConverter;
import dev.aulait.bt.core.infrastructure.command.TranslationEngine;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;

public class AdocTranslator implements Translator {

  private final Asciidoctor asciidoctor = Asciidoctor.Factory.create();
  private final TranslationEngine engine;

  public AdocTranslator(TranslationEngine engine) {
    this.engine = engine;
    asciidoctor.javaConverterRegistry().register(AdocConverter.class);
  }

  @Override
  public String ja2en(String text) {
    Attributes attributes =
        AttributesBuilder.attributes()
            .attribute("engine", String.valueOf(engine))
            .attribute("mode", "ja2en")
            .backend("adoc")
            .get();
    return asciidoctor.convert(text, OptionsBuilder.options().attributes(attributes).get());
  }

  @Override
  public String en2ja(String text) {
    Attributes attributes =
        AttributesBuilder.attributes()
            .attribute("engine", String.valueOf(engine))
            .attribute("mode", "en2ja")
            .backend("adoc")
            .get();
    return asciidoctor.convert(text, OptionsBuilder.options().attributes(attributes).get());
  }
}
