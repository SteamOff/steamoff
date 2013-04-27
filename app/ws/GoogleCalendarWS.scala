package ws

import play.api.libs.ws.WS

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._

object GoogleCalendarWS {
  case class AccessToken(token: String)
  object Google {
    case class CalendarEvent(eventId: String)
    case class CalendarId(calendarId: String)
  }

  val BASE_URL = "https://www.googleapis.com"
  val CALENDAR_LIST = "/calendar/v3/users/me/calendarList"
  def eventInsert(calId: String) = s"/calendar/v3/calendars/${calId}/events"

  private def url(url: String)(implicit accessToken: AccessToken) = WS
    .url(BASE_URL + url)
    .withHeaders("Authorization" -> s"Bearer ${accessToken.token}")
    //.withQueryString("key" -> "Z2FXYku49-VwJVV4dLLMuwW6")

  private def lookupCalendar(implicit accessToken: AccessToken): Future[Google.CalendarId] = {
    val req = url(CALENDAR_LIST).withQueryString("minAccessRole" -> "owner")
    val resp = req.get

    resp.flatMap{
      case r if r.status == 200 =>
        val j = r.json
        val id: Option[String] = (j \ "items" \\ "id").headOption.flatMap(_.asOpt[String])

        id.map(x => Future(Google.CalendarId(x))).getOrElse(Future.failed(CalendarLookupFailed))
      case _ => Future.failed(CalendarLookupFailed)
    }
  }

  private def insertEvent(calendar: Google.CalendarId,
                  title: String,
                  start: String,
                  end: String,
                  attendees: Seq[String])(implicit accessToken: AccessToken): Future[Google.CalendarEvent] = {

    val body = Json.obj(
      "attendees" -> JsArray(
        attendees.map{email: String =>
          Json.obj("email" -> email, "responseStatus" -> "needsAction")
        }
      ),
      "start" -> Json.obj("dateTime" -> start),
      "end" -> Json.obj("dateTime" -> end),
      "summary" -> title,
      "gadget" -> Json.obj(
        "preferences" -> Json.obj("blah" -> "foo"),
        "display" -> "chip",
        "type" -> "text/html",
        "title" -> title,
        "link" -> "https://localhost:9101/match/id",
        //"link" -> "steam://run/8870",
        "iconLink" -> "https://www.google.fr/images/google_favicon_128.png",
        "width" -> 300,
        "height" -> 300
      ),
      "reminders" -> Json.obj(
        "useDefault" -> false,
        "overrides" -> Json.arr(
          Json.obj("method" -> "email", "minutes" -> 1),
          Json.obj("method" -> "email", "minutes" -> 2),
          Json.obj("method" -> "email", "minutes" -> 3),
          Json.obj("method" -> "email", "minutes" -> 4),
          Json.obj("method" -> "email", "minutes" -> 5)
        )
      )
    )

    val req = url(eventInsert(calendar.calendarId))
      .withQueryString("sendNotifications" -> "true")
    val resp = req.post(body)

    resp.flatMap {
      case r if r.status == 200 =>
        (r.json \ "id").asOpt[String].map(x => Future(Google.CalendarEvent(x))).getOrElse(Future.failed(CalendarCreationFailed))
      case _ => Future.failed(CalendarCreationFailed)
    }
  }


  def registerEvent(accessToken: String, game: String, start: String, end: String, attendees: Seq[String]): Future[Google.CalendarEvent] = {
    implicit val t = AccessToken(accessToken)

    val title = s"Let's play $game"

    val event: Future[Google.CalendarEvent] = for {
      calendar <- lookupCalendar
      event <- insertEvent(calendar, title, start, end, attendees)
    } yield event

    event
  }

  case object CalendarLookupFailed extends Throwable
  case object CalendarCreationFailed extends Throwable
}


