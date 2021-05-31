import cats.effect.IO
import com.squareup.okhttp.{OkHttpClient, Request}
import scala.util.Try

object HttpGetter {

  def urlGetUTF8(url: String): IO[String] = {
    IO.fromTry({
      Try {
        val client = new OkHttpClient

        val builder = new Request.Builder().url(url).build()
        new String(client.newCall(builder).execute().body().bytes(), "UTF-8")
      }
    })
  }
}
