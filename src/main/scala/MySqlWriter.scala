import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.duration._
object MySqlWriter extends App {
  implicit val system = ActorSystem("mysql")
  implicit val materializer = ActorMaterializer()
  case object Tick

  val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("mysql")
  implicit val session = SlickSession.forConfig(databaseConfig)
  system.registerOnTermination(session.close())
  case class User(id: Int, name: String)
  val users = (100 to 10000).map(i => User(i, "name" + i))
  import session.profile.api._
  Source
    .repeat(Tick)
    .map(_ => User(1, UUID.randomUUID().toString))
    .via(Slick.flow(user => sqlu"Insert into user(name) values(${user.name})"))
    .runWith(Sink.ignore)

}
