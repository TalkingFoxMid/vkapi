import cats.syntax.all._

import scala.io.Source
object Main {
  def main(args: Array[String]): Unit = {
    val source = Source.fromFile("token")
    implicit val token: Token = Token(source.mkString)
    val out = args.toList match {
      case List("getFriends", id) => id.toIntOption match {
        case Some(value) => (VKAPI.getFriendsId(value) >>= (VKAPI.getUsersData(_))).unsafeRunSync()
          .map(data => s"${data.name} ${data.surname}").mkString("\n")
        case _ => "Wrong user ID"
      }
    }
    print(out)
    source.close()
  }
}
