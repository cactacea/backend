package io.github.cactacea.backend.controllers

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import io.github.cactacea.backend.core.application.components.services.DatabaseService

class UdonkoController @Inject()(db: DatabaseService) extends Controller {

  import db._

  get("/friend") { _: Request =>
    val q1 = quote { query[expressions].sortBy(_.id)(Ord.desc).take(3).map(_.happiness) }
    val q2 = quote { query[hearts].sortBy(_.id)(Ord.desc).take(3).map(_.beat)}
    val r = for {
      h <- run(q1)
      b <- run(q2)
    } yield (h, b)
    r.map({ case (n, b) =>
      val nn = n.size match {
        case 0 => 0.0005f
        case 1 => (n.fold(0.0f)(_ + _) / n.size)
      }
      val bb = b.size match {
        case 0 => 0.0005f
        case 1 => (b.fold(0.0f)(_ + _) / b.size)
      }
      val v = nn + bb
      GetFriend(v)
    })
  }

  post("/hearts") { request: PostHeart =>
    val q = quote {
      query[hearts].insert(
        _.beat -> lift(request.beat)
      )
    }
    run(q).map(_ => response.ok)
  }

  post("/blinks") { request: PostHeart =>
    val q = quote {
      query[blinks].insert(
        _.strength -> lift(request.beat)
      )
    }
    run(q).map(_ => response.ok)
  }


  post("/expressions") { request: List[PostExpression] =>
    request.headOption match {
      case Some(e) =>
        val a1 = e.faceRectangle.top.getOrElse(0L)
        val a2 = e.faceRectangle.left.getOrElse(0L)
        val a3 = e.faceRectangle.width.getOrElse(0L)
        val a4 = e.faceRectangle.height.getOrElse(0L)
        val a5 = e.scores.anger.getOrElse(0.0f)
        val a6 = e.scores.contempt.getOrElse(0.0f)
        val a7 = e.scores.disgust.getOrElse(0.0f)
        val a8 = e.scores.fear.getOrElse(0.0f)
        val a9 = e.scores.happiness.getOrElse(0.0f)
        val a10 = e.scores.neutral.getOrElse(0.0f)
        val a11 = e.scores.sadness.getOrElse(0.0f)
        val a12 = e.scores.surprise.getOrElse(0.0f)

        val q = quote {
          query[expressions].insert(
            _.top -> lift(a1),
            _.left -> lift(a2),
            _.width -> lift(a3),
            _.height -> lift(a4),
            _.anger -> lift(a5),
            _.contempt -> lift(a6),
            _.disgust -> lift(a7),
            _.fear -> lift(a8),
            _.happiness -> lift(a9),
            _.neutral -> lift(a10),
            _.sadness -> lift(a11),
            _.surprise -> lift(a12)
          )
        }
        run(q).map(_ => response.ok)
      case None =>
        response.ok
    }
  }

}

case class expressions (
                         id: Long,
                         top: Long,
                         left: Long,
                         width: Long,
                         height: Long,
                         anger: Float,
                         contempt: Float,
                         disgust: Float,
                         fear: Float,
                         happiness: Float,
                         neutral: Float,
                         sadness: Float,
                         surprise: Float
                       )



case class GetFriend(value: Float)
case class PostHeart(beat: Float)
case class PostExpression(
                           faceRectangle: PostExpressionFaceRectangle,
                           scores: PostExpressionScores
                         )
case class PostExpressionFaceRectangle(
                                        top: Option[Long],
                                        left: Option[Long],
                                        width: Option[Long],
                                        height: Option[Long])
case class PostExpressionScores(
                                 anger: Option[Float],
                                 contempt: Option[Float],
                                 disgust: Option[Float],
                                 fear: Option[Float],
                                 happiness: Option[Float],
                                 neutral: Option[Float],
                                 sadness: Option[Float],
                                 surprise: Option[Float]
                               )

case class hearts (
                    id: Long,
                    beat: Float
                  )

case class blinks (
                    id: Long,
                    strength: Float
                  )

