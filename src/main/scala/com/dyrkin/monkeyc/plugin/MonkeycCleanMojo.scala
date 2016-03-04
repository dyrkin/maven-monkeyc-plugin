package com.dyrkin.monkeyc.plugin

import java.io.File
import java.{util => ju}

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.{Mojo, Parameter}

/**
  * @author eugene zadyra
  */
@Mojo(name = "clean")
class MonkeycCleanMojo extends AbstractMojo {

  @Parameter(defaultValue = "bin", property = "binDirectory", readonly = true)
  var binDirectory: File = _

  @Parameter(defaultValue = "${project.build.directory}", readonly = true)
  var targetDirectory: File = _

  override def execute(): Unit = {
    binDirectory.empty()
    targetDirectory.empty()
  }
}
