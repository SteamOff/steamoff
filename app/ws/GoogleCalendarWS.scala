package ws

import play.api.libs.ws.WS

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object GoogleCalendarWS {
  case class AccessToken(token: String)

  val BASE_URL = "https://www.googleapis.com"
  val CALENDAR_LIST = "/calendar/v3/users/me/calendarList"

  private def url(url: String)(implicit accessToken: AccessToken) = WS
    .url(BASE_URL + url)
    .withHeaders("Authorization" -> s"Bearer ${accessToken.token}")
    //.withQueryString("key" -> "Z2FXYku49-VwJVV4dLLMuwW6")

  def registerEvent(accessToken: String): Future[Any] = {
    implicit val t = AccessToken(accessToken)


    val resp = url(CALENDAR_LIST).get
    resp.foreach{ r =>
      println(r)
      println(r.status)
      println(r.body)
    }
    resp
  }

}

