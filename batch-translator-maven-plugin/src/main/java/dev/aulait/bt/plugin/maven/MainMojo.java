package dev.aulait.bt.plugin.maven;

import dev.aulait.bt.core.Main;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "translate")
public class MainMojo extends AbstractMojo {

  @Parameter(property = "bt.source")
  private String source;

  @Parameter(property = "bt.target")
  private String target;

  @Parameter(property = "bt.mode")
  private String mode;

  @Parameter(property = "bt.filePattern")
  private String filePattern;

  @Parameter(property = "bt.engine")
  private String engine;

  Main main = new Main();

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    List<String> args = new ArrayList<>();

    buildArgs(args, "--source", source);
    buildArgs(args, "--target", target);
    buildArgs(args, "--mode", mode);
    buildArgs(args, "--file-pattern", filePattern);
    buildArgs(args, "--engine", engine);

    main.execute(args.toArray(new String[args.size()]));
  }

  private void buildArgs(List<String> args, String name, String arg) {
    if (arg != null) {
      args.add(name);
      args.add(arg);
    }
  }
}
