package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.Json
import play.api.mvc.Action

import models.Model

trait ModelController extends ExtController {
  def model: Model

  def all()  = Action { implicit request =>
    Async {
      model.all().map( Ok(_) )
    }
  }

  def findById(id: String)  = Action { implicit request =>
    Async {
      model.findById(id).map( _.map( Ok(_) ).getOrElse( NotFound("") ) )
    }
  }

  def create() = Action(parse.json) { implicit request =>
    Async {
      model.create(request.body).map(le => Ok(Json.obj("ok" -> le.ok)))
    }
  }
}