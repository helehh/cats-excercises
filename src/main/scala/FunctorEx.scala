import cats.Functor
import cats.data.Nested
import cats.implicits._

import scala.language.higherKinds

object FunctorEx1 {
  //Funktorist võib mõelda kui kontekstist, mille sees me mingit väärtust või väärtuseid hoiame, ning millega tuleb kaasa funktsioon map.
  //Funktori kasulikkus seisneb selles, et see võimaldab uute tüüpide jaoks kergesti map funktsiooni defineerida. Lisaks on functor tüübiklassis mõned
  //huvitavad funktsioonid, mida standardteegis ei ole.

  //defineerime kõigepealt enda andmetüübi:
  case class Box[A](item: A)

  //niisiis Box on kontekst, mis hoiab endas mingit väärtust A
  //saame selle jaoks defineerida funktori:

  implicit val BoxFunctor: Functor[Box] = ???

  val inc: Int => Int = _ + 1
  val demo1: Box[Int] = BoxFunctor.map(Box(3))(inc)
  val demo2: Box[Int] = Box(3) map inc

  // Haskellis on funktsioon map defineeritud järgmiselt:
  // map :: (a -> b) -> ([a] -> [b])
  // Haskelli mapist võib mõelda kui funktsioonist, mis võtab fuktsiooni a -> b ja teisendab selle kujule [a] -> [b]. Kantsulud tähistavad siin listi.
  // Catsis on mapi definitsioon selline:
  // def map[A, B](fa: F[A])(f: A => B): F[B]
  // Defineerime ka Scala jaoks funktsiooni, mis võtaks f: A => B ja teisendaks kujule  f: F[A] => F[B]

  def liftFun[A, B, F[_]](f: A => B)(implicit F: Functor[F]): F[A] => F[B] = ???

  val demo3: Box[Int] => Box[Int] = liftFun(inc)
  val demo4: List[Int] => List[Int] = liftFun(inc)

  //kirjuta funktsioon, mis mapiks etteantud funktsiooniga üle listi, mis sisaldab andmetüüpi Box
  //vihje: funktorite kompositsioon on samuti funktor. Funktorite kompositsiooni saab teha funktsiooniga compose

  def mapBoxes[A, B](l: List[Box[A]])(f: A => B): List[Box[B]] = ???

  def main(args: Array[String]): Unit = {
    val l = List(Box("üks"), Box("kaks"), Box("kolm"))
    println(Nested(l).map(_.length).value)
    assert(mapBoxes(l)(_.length) == List(Box(3), Box(4), Box(4)))
  }
}

//Sageli kasutatake konteksti mingi efekti väljendamiseks, näiteks Option väljendab seda, et väärtus võib puududa
//Funktor on vahend üksiku efektiga töötamiseks, sest võimaldab rakendada puhast funktsiooni efektiga väärtusele samas efekti säilitades
object FunctorEx2 {
  //kirjutame lihtsa ükskiku efektiga programmi, mis küsib kasutajalt sünniaasta ja väljastab, kui vana on kasutaja aastal 2025 või veateate, kui
  //stringi teisendamine arvuks ebaõnnestus

  //olgu antud järgmised funktsioonid

  //teisendab sisendit
  def toInt(s: String): Option[Int] = util.Try(s.toInt).toOption

  //tagastab vastuse
  def displayAnswer(age: Option[Int]): String = age match {
    case None => "Vigane sisend"
    case Some(x) => if(x < 0) "Vigane sisend" else s"Aastal 2025 oled sa $x."
  }

  //funktsioon vanuse arvutamiseks
  def ageIn2025(birthYear: Int): Int = 2025 - birthYear

  //paneme nendest juppidest nüüd kokku töötava programmi
  def main(args: Array[String]): Unit = {
    println("Sisesta sünniaasta: ")
      val age: Option[Int] = toInt(scala.io.StdIn.readLine())

    //leiame tulevikuvanuse nii, et kontekst säiliks
    val result = age map ageIn2025

    //kirjutame funktsiooni, mis võtab konteksti sees oleva vanuse ja tagastab konteksti sees tulevikuvanuse
    //vihje: üks variant oleks teisendada funktsiooni ageIn2015 tüübist Int => Int tüübiks Option[Int] => Option[Int]
    //uuri, millise Functor-tüübiklassi funktsiooniga seda teha saab: http://typelevel.org/cats/api/cats/Functor.html

    val computeOrFail: Option[Int] => Option[Int] = ???

    println(displayAnswer(computeOrFail(age)))
  }
}

object ApplicativeFunctor{
  //Applicative functor on sisuliselt funktor, mis võimaldab konteksti sees olevat funktsiooni rakendada konteksti sees olevale väärtusele

  //täiendame eelmist programmi nii, et see küsib kasutajalt sünniaasta ja mingi aasta tulevikus, ja väljastab, kui vana on kasutaja
  //sellel tulevikuaastal (või veateate)
  import FunctorEx2.{displayAnswer, toInt}

  //funktsioon vanuse arvutamiseks
  def ageInFuture(birthYear: Int, futureYear: Int): Int = futureYear - birthYear

  //paneme programmi kokku
  def main(args: Array[String]): Unit = {
    print("Sisesta sünniaasta: ")
    val birth = toInt(scala.io.StdIn.readLine())
    print("Sisesta aasta tulevikus: ")
    val future = toInt(scala.io.StdIn.readLine())

    //a ja b on Option konteksti sees olevad Int tüüpi väärtused, mille põhjal me tahaks arvutada ageInFuture,
    //samas konteksti säilitades displayAnswer jaoks
    //mis juhtub, kui proovime kasutada map'i nagu eelmises funktsioonis?
    //mappida saame ainult ühte väärtust korraga:
    val curriedFun: Int => Int => Int = (ageInFuture _).curried
    val wrappedFun: Option[Int => Int] = birth map curriedFun

    //Saame tulemuseks konteksti sees oleva funktsiooni, millele meil oleks vaja rakendada konteksti sees olevat väärtust future
    //map'i siin kasutada ei saa, sest see võtab tavalise funktsiooni A => B, meil on aga F[Int => Int]

    //mida siis teha? Applicative functor tuleb appi. Probleemi lahendamiseks on mitu erinevat võimalust:

    //1) rakendame wrappedFun muutujale Applicative tüübiklassi meetodit ap

    val result1 = ???

    //2) ärme väärtust wrappedFun üldse kasuta, vaid Applicative-tüübiklassi funktsiooni map2, mis võtab kaks kontekstiga väärtust
    //ja rakendab neid kahe argumendiga funktsioonile
    //def map2[A, B, Z](fa: F[A], fb: F[B])(f: (A, B) ⇒ Z): F[Z]

    val result2 = ???

    //3 kasutame Cartesian tüübiklassi meetodit product. Selle infiks-kuju on |@|
    //def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]

    //funktsioonide product ja map koos kasutamise abil saab samuti mitu tavalist argumenti
    //võtva funktsiooni mitmele kontekstiga väärtusele rakendada
    import cats.syntax.cartesian._

    val result3 = ???

    println("r1: " + displayAnswer(result1))
    println("r2: " + displayAnswer(result2))
    println("r3: " + displayAnswer(result3))


  }
}

