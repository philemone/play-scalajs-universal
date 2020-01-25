package settings

object AssetUrl {

  lazy val placeholder = "/assets/images/placeholder.png"

  def websiteScreenImage(fileName: String) = s"/assets/images/screenshots/$fileName.png"


}
