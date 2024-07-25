package dev.aulait.bt.core;

import dev.aulait.bt.core.application.FileTranslationService;
import dev.aulait.bt.core.domain.translation.TranslationSpecResolver;
import dev.aulait.bt.core.infrastructure.command.Command;
import dev.aulait.bt.core.infrastructure.command.TranslationEngine;
import dev.aulait.bt.core.infrastructure.command.TranslationMode;
import dev.aulait.bt.core.infrastructure.util.ResourceUtils;

import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Main {

  static Option modeOpt =
      Option.builder("m")
          .argName("TranslationMode")
          .desc("Translation mode (ja2en, en2ja)")
          .longOpt("mode")
          .required(true)
          .hasArg()
          .build();

  static Option filePatternOpt =
      Option.builder("p")
          .argName("FinePattern")
          .desc("Pattern of file name to be translated ('*' can be used as wild card)")
          .longOpt("file-pattern")
          .required(false)
          .hasArg()
          .build();

  static Option sourceOpt =
      Option.builder("s")
          .argName("Source")
          .desc("Input path")
          .longOpt("source")
          .required(true)
          .hasArg()
          .build();

  static Option targetOpt =
      Option.builder("t")
          .argName("Target")
          .desc("Output path")
          .longOpt("target")
          .required(true)
          .required(true)
          .hasArg()
          .build();

  static Option engineOpt =
      Option.builder("e")
          .argName("Engine")
          .desc("Translation engine (minhon)")
          .longOpt("engine")
          .required(false)
          .hasArg()
          .build();

  static Options options =
      new Options()
          .addOption(modeOpt)
          .addOption(filePatternOpt)
          .addOption(sourceOpt)
          .addOption(targetOpt)
          .addOption(engineOpt);

  public static void main(String[] args) {
    System.exit(new Main().execute(args));
  }

  public int execute(String[] args) {

    if (args.length == 0) {
      printHelp();
      return 0;
    }

    try {
      Command command = parse(args);

      return execute(command);
    } catch (ParseException e) {
      log.error("Error:", e);
      printHelp();
      return 1;
    }
  }

  public int execute(Command command) {
    FileTranslationService service = new FileTranslationService();
    // TODO Exception Handling
    TranslationSpecResolver.toSpecs(
            command.getSource(),
            command.getTarget(),
            command.getMode(),
            command.getFilePattern(),
            command.getEngine())
        .forEach(service::translate);

    return 0;
  }

  void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(-1);

    String jarFileName =
        Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
            .getFileName()
            .toString();

    String header = ResourceUtils.res2str(this, "help.txt");
    formatter.printHelp("java -jar " + jarFileName + " <Target...>", header, options, "", true);
  }

  Command parse(String[] args) throws ParseException {
    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine = parser.parse(options, args);
    Command command = new Command();

    command.setMode(TranslationMode.parse(commandLine.getOptionValue(modeOpt.getOpt())));
    command.setSource(commandLine.getOptionValue(sourceOpt.getOpt()));
    command.setTarget(commandLine.getOptionValue(targetOpt.getOpt()));
    command.setFilePattern(commandLine.getOptionValue(filePatternOpt.getOpt()));
    if (engineOpt.getOpt() != null
        && StringUtils.isNotEmpty(commandLine.getOptionValue(engineOpt.getOpt()))) {
      command.setEngine(TranslationEngine.parse(commandLine.getOptionValue(engineOpt.getOpt())));
    }
    return command;
  }
}
