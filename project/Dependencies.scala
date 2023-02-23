import sbt._
import sbt.Keys._

import de.heikoseeberger.sbtheader.{CommentStyle, FileType}
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.{headerLicense, headerMappings, HeaderLicense}

object Version {
  val jts = "1.19.0"
  val scalaTest = "3.2.14"
  val circe = "0.14.3"
}

object Dependencies {
  def priorTo213(scalaVersion: String): Boolean =
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, minor)) if minor < 13 => true
      case _                              => false
    }

  lazy val macroSettings: Seq[Setting[_]] = Seq(
    libraryDependencies ++= (
      if (priorTo213(scalaVersion.value))
        Seq(compilerPlugin(("org.scalamacros" %% "paradise" % "2.1.1").cross(CrossVersion.full)))
      else Nil
    ),
    scalacOptions ++= (if (priorTo213(scalaVersion.value)) Nil else Seq("-Ymacro-annotations"))
  )

  lazy val licenseSettings: Seq[Setting[_]] = Seq(
    headerLicense := Some(HeaderLicense.ALv2(java.time.Year.now.getValue.toString, "Azavea")),
    headerMappings := Map(
      FileType.scala -> CommentStyle.cStyleBlockComment.copy(
        commentCreator = { (text, existingText) =>
          {
            // preserve year of old headers
            val newText = CommentStyle.cStyleBlockComment.commentCreator.apply(text, existingText)
            existingText.flatMap(_ => existingText.map(_.trim)).getOrElse(newText)
          }
        }
      )
    )
  )

  def circe(module: String) = "io.circe" %% s"circe-$module" % Version.circe

  val jtsCore = "org.locationtech.jts" % "jts-core" % Version.jts
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
}
