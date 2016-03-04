package com.dyrkin.monkeyc

import java.io.File

import org.apache.maven.model.Resource

/**
  * @author eugene zadyra
  */
package object plugin {
  implicit def any2Opt[T](any: T): Option[T] = Option(any)

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

}
