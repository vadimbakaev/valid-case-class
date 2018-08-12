import Currency._
import Positive._
import cats.Monad
import cats.implicits._
import cats.kernel.Monoid
import com.softwaremill.tagging._

import scala.language.higherKinds

object Positive {

  trait Positive

  type PositiveInt = Int @@ Positive
  val isPositive: Int => Boolean = _ > 0
  implicit val positiveOpt: Int => Option[PositiveInt] = positive[Option]

  def positive[F[_]](v: Int)(implicit M: Monad[F], A: Monoid[F[Int]]): F[Int @@ Positive] =
    M.pure(v)
      .flatMap {
        case e if isPositive(e) => M.pure(e)
        case _                  => A.empty
      }
      .map(_.taggedWith[Positive])
}

object Currency {

  trait Currency

  type CurrencyString = String @@ Currency
  val isCurrency: String => Boolean = Set("EUR", "USD").contains
  implicit val currencyOpt: String => Option[CurrencyString] =
    Some(_).filter(isCurrency).map(_.taggedWith[Currency])
}

case class Account(value: PositiveInt, currency: CurrencyString)

object Main extends App {

  implicit class Type[A](a: A) {
    def validate[F[_], B](
                           implicit f: A => F[A @@ B],
                           M: Monad[F]
                         ): F[A @@ B] =
      M.pure(a).flatMap(f)

  }


  private val maybeAccount = (10.validate[Option, Positive], "EUR".validate[Option, Currency]) mapN Account

  print(maybeAccount)

}
