package ws

import models.{Game, AvatarSize, SteamUser}
import play.api.libs.ws.WS
import play.api.Play
import scala.concurrent._
import ExecutionContext.Implicits.global

import play.api.libs.json._
import play.api.libs.functional.syntax._

object SteamWS {

  val STEAM_API_KEY = Play.current.configuration.getString("steam_api_key").getOrElse({
    throw new Exception("Couldn't retrive the API key from the configuration file.")
  })

  // TODO: Refactor this crap
  val OWNED_GAMES_URL_FORMAT =
    s"http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=%s&steamid=%s".format(STEAM_API_KEY, "%s")
  val USER_INFO_URL_FORMAT =
    s"http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s".format(STEAM_API_KEY, "%s")

  implicit val userGamesReader = (
    (__ \ "game_count").read[Int] and
      (__ \ "games").read[Seq[Game]]
    ).tupled

  def getUserInfo(steamID: String): Future[SteamUser] = {
      // TODO: Refactor this using the functional Json API
      WS.url(USER_INFO_URL_FORMAT.format(steamID)).get().map { response =>
        val user: JsArray = (response.json \ "response" \ "players").as[JsArray]

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
      }
  }

  /**
   * @param steamID The user's SteamID
   * @return A tuple containing the counter of owned games  and the games' list
   */
  def getGamesOfUser(steamID: String): Future[(Int, Seq[Game])] = {
    WS.url(OWNED_GAMES_URL_FORMAT.format(steamID)).get().flatMap { response =>
      println(OWNED_GAMES_URL_FORMAT.format(steamID))
      Json.fromJson[(Int, Seq[Game])](response.json \ "response") map { result =>
        Future.successful[(Int, Seq[Game])](result)
      } recoverTotal { e => Future.failed(new RuntimeException(e.toString)) }
    }
  }

}

case class SteamUserNotFoundException() extends Throwable
