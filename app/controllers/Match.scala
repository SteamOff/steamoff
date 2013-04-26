package controllers

import play.api.mvc._

import ws.GoogleCalendarWS

object Match extends ExtController with Authentication {

  def make = GoogleAuthenticated { implicit user => implicit request =>
    GoogleCalendarWS.registerEvent(request.session.get("accessToken").get)
    Ok("blah")
  }

}
