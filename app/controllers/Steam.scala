package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{AsyncResult, Action}
import play.api.Logger
import play.api.libs.openid.OpenID
import play.api.libs.json.JsString
import play.api.libs.json.Json

object Steam extends  ExtController {
  def opendIDUrl = Action(parse.json) { implicit request =>
    request.body.\("openid").asOpt[JsString] match {
      case None =>  Logger.info("Steam.login bad request"); BadRequest("")
      case Some(openid) => Async {
        OpenID.redirectURL(openid.value, routes.Steam.openIDCallback.absoluteURL(), Seq.empty, Seq.empty, Some("http://localhost"))
          .map{ url => Ok(Json.obj( "ok" -> true, "url" -> url )) }
          .recover { case e: Throwable =>
            Logger.error(e.getStackTraceString)
            Logger.error(e.getMessage)
            BadRequest(Json.obj( "ok" -> false ))
          }
      }
    }
  }

  def openIDCallback = Action { implicit request =>
    Logger.info(request.headers.toString())
    Logger.info(request.body.toString)
    AsyncResult(
      OpenID.verifiedId
        .map( i => Ok(i.id + "\n" + i.attributes) )
        .recover { case e: Throwable =>
          Logger.error(e.getStackTraceString)
          Logger.error(e.getMessage)
          Redirect(routes.Application.main(""))
        }
    )
  }
}