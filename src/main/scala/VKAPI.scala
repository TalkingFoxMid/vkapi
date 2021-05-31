import Responses.{ApiResponse, FriendsResponse, NotSingletonListError, UserDataResponse}
import cats.effect.IO
import cats.syntax.all._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object VKAPI {
  final val PREFIX: String = "https://api.vk.com/method/"
  def getUsersData(ids: List[Int])(implicit token: Token): IO[List[UserData]] = {
    HttpGetter.urlGetUTF8(
      f"${PREFIX}users.get?user_ids=${ids.mkString(",")}&access_token=${token.token}&v=5.131"
    ) >>= {
      s =>
        decode[ApiResponse[List[UserDataResponse]]](s) match {
          case Left(value) => IO.raiseError(value)
          case Right(value) => IO(value.response.map(v => UserData(v.first_name, v.last_name)))
        }
    }
  }
  def getUserData(id: Int)(implicit token: Token): IO[UserData] = {
    (HttpGetter.urlGetUTF8(
      f"${PREFIX}users.get?user_ids=${id}&access_token=${token.token}&v=5.131"
    ) >>= {
      s =>
        decode[ApiResponse[List[UserDataResponse]]](s) match {
          case Left(value) => {
            print(s)
            IO.raiseError(value)
          }
          case Right(value) => IO(value)
        }
    }).flatMap(g => g.response match {
      case List(a) => IO(UserData(a.first_name, a.last_name))
      case _ => IO.raiseError(new NotSingletonListError())
    })
  }
  def getFriendsId(id: Int)(implicit token: Token): IO[List[Int]] = {
    HttpGetter.urlGetUTF8(
      f"${PREFIX}friends.get?user_id=${id}&access_token=${token.token}&v=5.131"
    ) >>= {
      s =>
        decode[ApiResponse[FriendsResponse]](s) match {
          case Left(value) => IO.raiseError(value)
          case Right(value) =>IO(value)
        }
    }
  }.map(_.response.items)

}
