package dev.aulait.bt.plugin.maven;

import java.io.File;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

public class MainMojoTest {
  @Rule public MojoRule mojoRule = new MojoRule();

  @Rule public TestResources resources = new TestResources();

  @Test
  public void execute() throws Exception {
    File pom = new File(resources.getBasedir("test-project-1"), "pom.xml");
    MainMojo mojo = (MainMojo) mojoRule.lookupMojo("translate", pom);
    mojo.execute();
  }
}
