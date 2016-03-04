package com.dyrkin.monkeyc

import java.io.File

import org.apache.maven.model.Resource
import org.apache.maven.plugin.logging.Log

import scala.sys.process.ProcessLogger

/**
  * @author eugene zadyra
  */
package object plugin {
  implicit def any2Opt[T](any: T): Option[T] = Option(any)

  implicit def log2ProcessLog(log: Log): ProcessLogger = ProcessLogger(
    (o: String) => log.info(o),
    (e: String) => log.error(e))

  def isWin = sys.props("os.name").toLowerCase().contains("win")

  implicit class FileScanner(file: File) {
    def find(extensions: String*) = {
      def find(f: File): Array[File] = {
        val these = f.listFiles
        these ++ these.filter(_.isDirectory).flatMap(find)
      }
      find(file).filter(f => extensions.exists(ext => f.getName.endsWith(s".$ext")))
    }

    def empty(): Unit = {
      def empty(f: File): Unit = {
        val these = f.listFiles
        these.filterNot(_.isDirectory).foreach(_.delete())
        these.filter(_.isDirectory).foreach(empty)
      }

      if (file.exists()) {
        empty(file)
        file.delete()
      }
    }
  }

  implicit class RichResource(resource: Resource) {
    def asFile = new File(resource.getDirectory)
  }

  def os(unixName: String, ext: String = ".exe", winName: Option[String] = None) = {
    if(isWin) winName getOrElse s"$unixName$ext" else unixName
  }
}
