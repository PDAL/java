import sbt._
import sbt.Keys._

object Dependencies {
  private def ver(for211: String, for213: String) = Def.setting {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 11)) => for211
      case Some((2, 12)) | Some((2, 13)) => for213
      case _ => sys.error("not good")
    }
  }

  def circe(module: String) = Def.setting {
    "io.circe" %% s"circe-$module" % ver("0.11.2", "0.12.2").value
  }

  val jtsCore   = "org.locationtech.jts" % "jts-core"  % "1.16.1"
  val scalaTest = "org.scalatest"       %% "scalatest" % "3.1.0"
}
