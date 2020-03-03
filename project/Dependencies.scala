import sbt._
import sbt.Keys._

import de.heikoseeberger.sbtheader.{CommentCreator, CommentStyle, FileType}
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.{HeaderLicense, headerLicense, headerMappings}

object Dependencies {
  private def ver(for211: String, for213: String): Def.Initialize[String] = Def.setting {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 11)) => for211
      case Some((2, 12)) | Some((2, 13)) => for213
      case _ => sys.error("not good")
    }
  }

  def priorTo213(scalaVersion: String): Boolean =
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, minor)) if minor < 13 => true
      case _                              => false
    }

  def circe(module: String) = Def.setting {
    "io.circe" %% s"circe-$module" % ver("0.11.2", "0.12.2").value
  }

  lazy val macroSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= (
      if (priorTo213(scalaVersion.value)) Seq(compilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full))
      else Nil
    ),
    scalacOptions ++= (if (priorTo213(scalaVersion.value)) Nil else Seq("-Ymacro-annotations"))
  )

  lazy val licenseSettings: Seq[Setting[_]] = Seq(
    headerLicense := Some(HeaderLicense.ALv2(java.time.Year.now.getValue.toString, "Azavea")),
    headerMappings := Map(
      FileType.scala -> CommentStyle.cStyleBlockComment.copy(commentCreator = new CommentCreator() {
        val Pattern = "(?s).*?(\\d{4}(-\\d{4})?).*".r
        def findYear(header: String): Option[String] = header match {
          case Pattern(years, _) => Some(years)
          case _                 => None
        }
        def apply(text: String, existingText: Option[String]): String = {
          // preserve year of old headers
          val newText = CommentStyle.cStyleBlockComment.commentCreator.apply(text, existingText)
          existingText.flatMap(_ => existingText.map(_.trim)).getOrElse(newText)
        }
      })
    )
  )

  val jtsCore   = "org.locationtech.jts" % "jts-core"  % "1.16.1"
  val scalaTest = "org.scalatest"       %% "scalatest" % "3.1.1"
}
