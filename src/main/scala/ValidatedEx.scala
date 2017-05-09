
//kirjutame programmi, mille ülesandeks on etteantud kontaktandmeid valideerida ning
//kui mingid andmed ei vasta etteantud reeglitele, siis kasutajat sellest teavitada, öeldes konkreetselt, millised sisendid ei vastanud reeglitele

//Catsi andmetüüp Validated koos applicative funktoriga teeb sellise andmete valideerimise eriti mugavaks
//Validated on andmetüüp, millel on kaks võimalikku väärtust - Valid ja Invalid:

//  sealed trait Validated[+E, +A]
//  case class Valid[+A](a: A) extends Validated[Nothing, A]
//  case class Invalid[+E](e: E) extends Validated[E, Nothing]

//Validated andmetüübi teeb eriliseks see, et see ei lõpeta veaohtlike tegevuste ahelas esimese ettetulnud vea korral tööd,
//vaid kogub kõik ettetulnud veateated kokku
//seda on hea kasutada näiteks siis, kui on vaja mitut sisendit valideerida ja iga sisendi kohta on vaja teada, kas see vastas
//reeglitele või mitte - näiteks ankeedi valideerimisel


import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, ValidatedNel}

import scala.util.{Failure, Success, Try}

object ValidateEx {
  //eesmärk on sisendi põhjal teha uusi kasutajaid:
  case class User(name: String, email: String, age: Int)

  //juhul kui valideerimisel osutub sisend vigaseks, tagastame Invalid(veateade):
  def error[A](e: String): ValidatedNel[String, A] = Invalid(NonEmptyList.of(e))


  ///Selleks, et sisendeid valideerida, defineerime iga argumendi jaoks eraldi seda argumenti valideeriva funktsiooni:
  def validateName(name: String): ValidatedNel[String, String] =
    if (name.length > 0) Valid(name) else error(s"Vigane kasutajanimi: $name.")

  def validateEmail(email: String): ValidatedNel[String, String] =
    if(email.matches("[a-zA-Z0-9.]+@[a-z]+\\.[a-z]+")) Valid(email) else error(s"Vigane e-maili aadress: $email!")

  def validateAge(age: String): ValidatedNel[String, Int] = {
    Try(age.toInt) match {
      case Success(s) => Valid(s)
      case Failure(f) => error(s"Vigane vanus: $age!")
    }
  }

  //kirjutame nüüd eelnevat ära kasutades funktsiooni, mis tagastab uue kasutaja, kui sisendid vastavad reeglitele
  //ja listi veateadetest, kui mõni sisend ei vasta reeglitele

  //kuna Catsis vaikimisi Validated kuulub Applicative-tüübiklassi, siis saame kasutada selle tüübiklassi funktsioone:
  import cats.syntax.cartesian._

  def validateUser(name: String, email: String, age: String): ValidatedNel[String, User] = {
    (validateName(name) |@| validateEmail(email) |@| validateAge(age)) map User
  }

  def main(args: Array[String]): Unit = {
    val list = List(
      validateUser("Joosep Toots", "tootsjoosepemail.com", "22a"),
      validateUser("Mikk Kotkas", "mikk.kotkas@email.com", "47")
    )

    for (user <- list) {
      val res = user match {
        case Valid(user) => "Nimi: " + user.name + ", email: " + user.email + ", vanus: " + user.age
        case Invalid(e) => e.toList.mkString("\n")
      }
      println(res)
    }
  }
}
