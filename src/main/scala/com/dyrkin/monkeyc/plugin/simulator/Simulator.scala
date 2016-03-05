package com.dyrkin.monkeyc.plugin.simulator

import java.io.File

import org.apache.maven.plugin.logging.Log

import scala.util.Try

/**
  * @author eugene zadyra
  */
case class Simulator(val sdkLocation: String, val program: File, val deviceId: String, val log: Log) extends Commands {
  val Ports = 1234 to 1238
  val Attempts = 3
  val WaitDuration = 500L

  def run() = {
    launch(prepareSimulatorCommand)
    val port = pushProgram()
    launch(prepareRunProgrammCommand(port)).exitValue()
  }

  private def pushProgram(): Int = {
    withAttempts {
      Ports.find { port =>
        Thread.sleep(WaitDuration)
        log.info(s"Trying to connect using port: $port")
        Try(launch(preparePushProgrammCommand(port)).exitValue() == 0) getOrElse false
      }
    } getOrElse sys.error("Unable to connect to simulator")
  }

  def withAttempts[T](f: => Option[T], attempts: Int = Attempts): Option[T] = {
    log.info(s"Attempt left: $attempts")
    f match {
      case None => if (attempts > 0) withAttempts(f, attempts - 1) else None
      case some => some
    }
  }
}
