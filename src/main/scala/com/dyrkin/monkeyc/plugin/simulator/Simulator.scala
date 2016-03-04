package com.dyrkin.monkeyc.plugin.simulator

import java.io.File

import com.dyrkin.monkeyc.plugin._
import org.apache.maven.plugin.logging.Log

import scala.sys.process._
import scala.util.Try

/**
  * @author eugene zadyra
  */
case class Simulator(sdkLocation: String, program: File, deviceId: String, log: Log) {
  var simulatorProcess: Process = _
  val Ports = 1234 to 1238
  val Attempts = 3
  val WaitDuration = 500L

  def run() = {
    launchSimulator()
    val port = deployProgram()
    runProgram(port)
  }

  def launchSimulator(): Unit = {
    val executable = s"$sdkLocation/bin/${os("connectiq", winName = "simulator.exe")}"
    simulatorProcess = executable run log
  }

  def deployProgram(): Int = {
    withAttempts {
      Ports.find { port =>
        Thread.sleep(WaitDuration)
        log.debug(s"Trying to connect using port: $port")
        Try {
          val deployCommand = s"$sdkLocation/bin/${os("shell")}" +
            s" --transport=tcp --transport_args=127.0.0.1:$port push ${program.getAbsolutePath}" +
            s" 0:/GARMIN/APPS/${program.getName}"

          val process = deployCommand run log
          process.exitValue() == 0
        } getOrElse false
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

  def runProgram(port: Int): Unit = {
    val runCommand = s"$sdkLocation/bin/${os("shell")}" +
      s" --transport=tcp --transport_args=127.0.0.1:$port tvm" +
      s" 0:/GARMIN/APPS/${program.getName} ${if(deviceId.isEmpty) "square_watch" else deviceId}"
    val process = runCommand run log
    process.exitValue()
  }
}
