package models

import play.api._
import play.api.libs.json.Json

// ReactiveMongo
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.LastError
import play.modules.reactivemongo.json.collection.JSONCollection

// ReactiveMongo plugin
import play.modules.reactivemongo._

case class Settings(
  test: String
)

object Settings {
  implicit val settingsFormat = Json.format[Settings]
}

object SettingsBsonHandler extends BSONDocumentReader[Settings] with BSONDocumentWriter[Settings] {
  def read(document: BSONDocument): Settings = {
    Settings("test")
  }
  def write(o: Settings): BSONDocument = {
    BSONDocument(
    )
  }
}
