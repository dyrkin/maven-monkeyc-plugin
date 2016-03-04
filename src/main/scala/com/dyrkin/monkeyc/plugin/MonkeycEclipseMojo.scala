package com.dyrkin.monkeyc.plugin

import java.io.{File, PrintWriter}
import java.{util => ju}

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.{Mojo, Parameter}

/**
  * @author eugene zadyra
  */
@Mojo(name = "eclipse")
class MonkeycEclipseMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project.artifactId}", required = true, readonly = true)
  var artifactId: String = _

  @Parameter(defaultValue = "${project.basedir}", readonly = true)
  var basedir: File = _

  override def execute(): Unit = {
    val projectDefinition =
      <projectDescription>
        <name>{artifactId}</name>
        <comment></comment>
        <projects>
        </projects>
        <buildSpec>
          <buildCommand>
            <name>connectiq.builder</name>
            <arguments>
            </arguments>
          </buildCommand>
        </buildSpec>
        <natures>
          <nature>connectiq.projectNature</nature>
        </natures>
      </projectDescription>.toString()

    val projectFile = new File(basedir.getAbsolutePath, ".project")
    projectFile.delete()

    val writer = new PrintWriter(projectFile)
    writer.write(projectDefinition)
    writer.close()
    getLog.info("Eclipse .project file generated. Now you can import project to eclipse")
  }
}
