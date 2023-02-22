package io.pdal

sealed trait LogLevel { def code: Int }

object LogLevel {
  case object Error extends LogLevel { val code = 0 }
  case object Warning extends LogLevel { val code = 1 }
  case object Info extends LogLevel { val code = 2 }
  case object Debug extends LogLevel { val code = 3 }
  case object Debug1 extends LogLevel { val code = 4 }
  case object Debug2 extends LogLevel { val code = 5 }
  case object Debug3 extends LogLevel { val code = 6 }
  case object Debug4 extends LogLevel { val code = 7 }
  case object Debug5 extends LogLevel { val code = 8 }
  case object None extends LogLevel { val code = 9 }
}
