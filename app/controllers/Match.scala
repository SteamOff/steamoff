package controllers

import play.api.mvc._

import ws.GoogleCalendarWS

object Match extends ExtController with Authentication {

  def make = GoogleAuthenticated { implicit user => implicit request =>
    GoogleCalendarWS.registerEvent(
      request.session.get("accessToken").get,
      "Left 4 Dead 2",
      "2013-04-26T19:00:00+02:00",
      "2013-04-26T20:00:00+02:00",
      Seq("ast@zenexity.com", "evo@zenexity.com", "pdi@zenexity.com")
    )
    Redirect(routes.Application.main(""))
  }

}
