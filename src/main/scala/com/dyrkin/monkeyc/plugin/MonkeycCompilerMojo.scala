package com.dyrkin.monkeyc.plugin

import java.io.File
import java.{util => ju}

import org.apache.maven.model.Resource
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.{Component, LifecyclePhase, Mojo, Parameter}
import org.apache.maven.project.MavenProject

import scala.collection.JavaConversions._
import scala.sys.process._
import scala.util.Try

/**
  * @author eugene zadyra
  */
@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
class MonkeycCompilerMojo extends AbstractMojo {

  @Component
  var project: MavenProject = _

  @Parameter(readonly = true)
  var monkeycHome: String = _

  @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
  var finalName: String = _

  @Parameter(defaultValue = "bin", property = "binDirectory", readonly = true)
  var binDirectory: File = _

  @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
  var sourceDirectory: File = _

  @Parameter(defaultValue = "${project.build.resources}", required = true, readonly = true)
  var resourceDirectories: ju.List[Resource] = _

  @Parameter(defaultValue = "manifest.xml", property = "manifest", readonly = true)
  var manifest: File = _


  override def execute(): Unit = {
    val sdkLocation = Try(monkeycHome getOrElse sys.env("GARMIN_SDK_HOME")) getOrElse sys.error("Please specify parameter <monkeycHome> or add environment variable $GARMIN_SDK_HOME")

    val target = new File(binDirectory.getAbsolutePath, s"$finalName.prg")

    project.getArtifact.setFile(target)

    val sourceFiles = sourceDirectory.find("mc")
    val resourceFiles = resourceDirectories.flatMap(_.asFile.find("xml"))

    val command = buildCommand(sdkLocation, target, sourceFiles, resourceFiles)

    command ! getLog
  }

  def buildCommand(sdk: String, target: File, sourceFiles: Seq[File], resourceFiles: Seq[File]) = {
    s"$sdk/bin/${os("monkeyc", ext = ".bat")}" +
      s" -m ${manifest.getAbsolutePath}" +
      s" -o ${target.getAbsolutePath}" +
      s" ${sourceFiles.map(_.getAbsolutePath).mkString(" ")}" +
      s" -z ${resourceFiles.map(_.getAbsolutePath).mkString(if (isWin) ";" else ":")}"
  }
}
