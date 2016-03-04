package com.dyrkin.monkeyc.plugin

import java.io.File

import com.dyrkin.monkeyc.plugin.simulator.Simulator
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.{Mojo, Parameter, ResolutionScope}
import scala.xml._

import scala.util.Try

/**
  * @author eugene zadyra
  */
@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = false)
class MonkeycSimulatorMojo extends AbstractMojo {

  @Parameter(readonly = true)
  var monkeycHome: String = _

  @Parameter(defaultValue = "bin", property = "binDirectory", readonly = true)
  var binDirectory: File = _

  @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
   var finalName: String = _

  @Parameter(defaultValue = "manifest.xml", property = "manifest", readonly = true)
  var manifest: File = _

  override def execute(): Unit = {
    val sdkLocation = Try(monkeycHome getOrElse sys.env("GARMIN_SDK_HOME")) getOrElse sys.error("Please specify parameter <monkeycHome> or add environment variable $GARMIN_SDK_HOME")

    val target = new File(binDirectory.getAbsolutePath, s"$finalName.prg")

    val deviceIds = XML.loadFile(manifest) \\ "products" \ "product" map(_ \ "@id")

    Simulator(sdkLocation, target, deviceIds.map(_.text).mkString(" "), getLog).run()
  }
}
