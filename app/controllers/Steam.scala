package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import play.api._
import play.api.mvc.{AsyncResult, Action}
import play.api.Logger
import play.api.libs.openid.OpenID
import play.api.libs.json.JsString
import play.api.libs.json.Json

object Steam extends  ExtController {
  def opendIDUrl = Action { implicit request =>
    Async {
      OpenID.redirectURL(
        openID = "http://steamcommunity.com/openid",
        callbackURL = routes.Steam.openIDCallback.absoluteURL(),
        axRequired = Seq.empty,
        axOptional = Seq.empty,
        realm = Some("http://localhost")
      )
        .map{ url =>
          Logger.debug(s"Redirect to $url")
          Redirect(url)
        }
        .recover { case e: Throwable =>
          Logger.error("error", e)
          BadRequest(Json.obj( "ok" -> false ))
        }
    }
  }

  def openIDCallback = Action { implicit request =>
    Logger.info("headers: " + request.headers.toString())
    Logger.info("body: " + request.body.toString)
    Logger.info("querystring: " + request.queryString.toString)
    AsyncResult(
      OpenID.verifiedId
        .map( i => Ok(i.id + "\n" + i.attributes) )
        .recover { case e: Throwable =>
          Logger.error("error", e)
          Redirect(routes.Application.main(""))
        }
    )
  }
}
