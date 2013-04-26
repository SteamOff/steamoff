package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.core.commands.LastError
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AvatarSize extends Enumeration {
  type AvatarSize = Value
  val Normal, Medium, Full = Value
}

case class SteamUser(
  steamID: String,
  personaName: String,
  realName: String,
  profileUrl: String,
  avatars: Map[AvatarSize.AvatarSize, String],
  games: Seq[Game],
  joinDate: Int,
  country: String,
  cityID: String) extends Model {
  lazy val collectionJson = db[JSONCollection]("steamusers")

  def save: Future[LastError] = collectionJson.insert((Json.toJson(this)(SteamUser.steamUserWriter)).as[JsObject])
}

object SteamUser {
  implicit val avatarSizeMapWriter: Writes[Map[AvatarSize.AvatarSize, String]] = (
    __.write[Map[String, String]].contramap{ (m: Map[AvatarSize.AvatarSize, String]) =>
      m.map { case (k, v) => (k.toString -> v) }
    }
  )

  implicit val steamUserWriter = (
    (__ \ "steamId").write[String] and
    (__ \ "personaName").write[String] and
    (__ \ "realName").write[String] and
    (__ \ "profileUrl").write[String] and
    (__ \ "avatars").write[Map[AvatarSize.AvatarSize, String]] and
    (__ \ "games").write[Seq[Game]] and
    (__ \ "joinDate").write[Int] and
    (__ \ "country").write[String] and
    (__ \ "city").write[String]
  )(unlift(SteamUser.unapply))
}