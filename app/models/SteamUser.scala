package models

object AvatarSize extends Enumeration {

  type AvatarSize = Value
  val Normal, Medium, Full = Value

}

case class SteamUser(
  steamID: String,
  personaName: String,
  realName: String,
  profileUrl: String,
  avatars: Map[AvatarSize.AvatarSize, String],
  joinDate: Int,
  country: String,
  cityID: String)
