package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Game(appId: String, totalPlaytime: Int)

object Game {
  implicit val gameReader = (
    (__ \ "appid").read[Int].map(_.toString) and
    (__ \ "playtime_forever").read[Int].orElse(Reads.pure(0))
  )(Game.apply _)

  implicit val gameWriter = (
    (__ \ "appid").write[String] and
    (__ \ "playtime_forever").write[Int]
  )(unlift(Game.unapply))
}