package ws

import models.{AvatarSize, SteamUser}
import play.api.libs.ws.WS
import play.api.Play
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.libs.json.{JsArray, JsObject, JsValue}

object SteamWS {

  val STEAM_API_KEY = Play.current.configuration.getString("steam_api_key").getOrElse({
    throw new Exception("Couldn't retrive the API key from the configuration file.")
  })

  val USER_INFO_URL_FORMAT =
    s"http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s".format(STEAM_API_KEY, "%s")

  def getUserInfo(steamID: String): SteamUser = {
    Await.result(
      // TODO: Handle failure case
      WS.url(USER_INFO_URL_FORMAT.format(steamID)).get().map { response =>
        val user: JsArray = (response.json \ "response" \ "players").as[JsArray]

        // Sorry guys :'(
        if (user.value.size == 0) throw new SteamUserNotFoundException

        val avatars = Map(
          AvatarSize.Normal -> (user(0) \ "avatar").as[String],
          AvatarSize.Medium -> (user(0) \ "avatarmedium").as[String],
          AvatarSize.Full   -> (user(0) \ "avatarfull").as[String])

        SteamUser(
          steamID,
          (user(0) \ "personaname").as[String],
          (user(0) \ "realname").as[String],
          (user(0) \ "profileurl").as[String],
          avatars,
          (user(0) \ "timecreated").as[Int],
          (user(0) \ "loccountrycode").as[String],
          (user(0) \ "loccityid").as[Int].toString)
      },
      10 seconds)
  }

}

case class SteamUserNotFoundException() extends Throwable
