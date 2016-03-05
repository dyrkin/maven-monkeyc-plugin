package com.dyrkin.monkeyc.plugin.simulator

import com.dyrkin.monkeyc.plugin._
import scala.sys.process._

/**
  * @author eugene zadyra
  */
trait Commands {
  self: Simulator =>

  def preparePushProgrammCommand(port: Int) = {
    s"$sdkLocation/bin/${os("shell")} --transport=tcp --transport_args=127.0.0.1:$port push ${program.getAbsolutePath} 0:/GARMIN/APPS/${program.getName}"
  }

  def prepareRunProgrammCommand(port: Int) = {
    s"$sdkLocation/bin/${os("shell")} --transport=tcp --transport_args=127.0.0.1:$port tvm 0:/GARMIN/APPS/${program.getName} ${if (deviceId.isEmpty) "square_watch" else deviceId}"
  }

  def prepareSimulatorCommand = {
    s"$sdkLocation/bin/${os("connectiq", winName = "simulator.exe")}"
  }

  def launch(f: => String) = {
    f run log
  }
}
