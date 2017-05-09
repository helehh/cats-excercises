import cats._
import cats.implicits._

object MonoidEx1 {
  //Monoid on andmetüüp, millel leidub assotsiatiivne tehe ja ühikelement
  //Assotsiatiivseks tehteks on meetod combine (|+|) ning ühikelemendiks empty
  //Liitmise jaoks on näiteks järgmine definitsion:
  implicit val intAdditionMonoid: Monoid[Int] = new Monoid[Int] {
    override def empty: Int = 0
    override def combine(x: Int, y: Int): Int = x + y
  }

  def main(args: Array[String]): Unit = {

    // Monoidi operatsiooni saab kasutada kõikidel tüüpidel, mis seda toetavad:
    println(5 |+| 10)
    println("kala" |+| "maja")

    // Tüübiklasside üks tugev külg on see, et neid saab väga kergesti komponeerima.
    // Näiteks: monoidiliste tüüpide A ja B korrutis (A, B) on samuti monoid.
    // Proovi kokku liita järgmised elemendid:
    val x = (5, "kala")
    val y = (10, "maja")

    println(???)

    // tulemus peaks olema sama kui:
    println((x._1 |+| y._1, x._2 |+| y._2))

    // Mis saab kui lisada neile mõni komponent juurde?
  }
}

object MonoidEx2 {
  //Cats on paljude andmetüüpide jaoks juba monoidi defineerinud, kuid näiteks mitte Booleani jaoks.
  //Defineerime monoidi konjunktsiooni jaoks:
  //  * Mis on tema ühikelement?
  //  * (Iga monoidi jaoks peab kehtima: empty |+| x == x.)

  implicit val andMonoid: Monoid[Boolean] = ???

  //Mis on järgneval kolmel funktsioonil ühist?
  def sumInts(list: List[Int]): Int = list.foldRight(0)(_ + _)

  def concatStrings(list: List[String]): String = list.foldRight("")(_ ++ _)

  def unionSets[A](list: List[Set[A]]): Set[A] = list.foldRight(Set.empty[A])(_ union _)

  def combineAll[A](list: List[A])(implicit A: Monoid[A]): A = ???

  // Kui meie andMonoid ja combineAll on olemas, siis forAll meetod, mis kontrollib,
  // kas kõik tõeväärtuste list on tõesed, ei ole midagi muud kui boolean listi combineAll:
  def forAll(list: List[Boolean]): Boolean = combineAll(list)

  // Kuna me importime kõik catsi implitsiitsed definitsioonid, siis selline meetod on
  // Catsis tegelikult listidele juba olemas (implicit maagia). Defineerige jälle funktsioon forAll,
  // aga nüüd kirjutage "list." ja otsige, kas leidub combineAll listi meetodina.

  def forAllBuiltin(list: List[Boolean]): Boolean = ???

  def main(args: Array[String]): Unit = {
    println(forAll(List(true, false, true)))
    println(forAllBuiltin(List(true, false, true)))
  }

}

object MonoidEx3 {
  //Lihtsalt elementide liitmine combine meetodiga pole eriti huvitav.
  //Sellepärast vaatame järgnevas kahes ülesandes funktsiooni foldMap, mis implitsiitselt kasutab foldimiseks monoidi

  //Kuna monoidiliste tüüpide A ja B korrutis (A, B) on samuti monoid,
  //saame kasutada foldMapi, et foldida listi läbimisel mitut erinevat väärtust korraga. See funktsioon on Catsis ka listi meetodina olemas
  def avg(l: List[Int]): Int = {
    val (sum, count): (Int, Int) = ???
    sum / count
  }

  //Bag on nagu set, kuid võib sisaldada korduvaid elemente. Seda kujutatakse Mapina, kus võtmeteks on erinevad
  //elemendid ning väärtusteks elementide esinemiste arv bagis. Kirjuta funktsioon bag, mis teisendab etteantud sõnade järjendi bagiks.
  //Vihje: kuidas töötab combine meetod Mapi puhul? (vt. main meetodis)

  def bag(l: List[String]): Map[String, Int] = ???

  def main(args: Array[String]): Unit = {
    println(avg(List(1, 2, 3, 45, 6)))

    println(Map("x" -> 5, "y" -> 10) |+| Map("x" -> 3, "y" -> 4, "z" -> 2))
    println(Map("x" -> "kala", "y" -> "tellis") |+| Map("x" -> "maja", "y" -> "kivi", "z" -> "lisa"))

    val lause = "Esimese lause esimene sõna. Teise lause esimene sõna ja teine sõna."
    val sõnad = lause.toLowerCase().replace(".", "").split(" ")
    assert(bag(sõnad.toList) == Map("esimese" -> 1, "lause" -> 2, "esimene" -> 2, "sõna" -> 3, "teise" -> 1, "ja" -> 1, "teine" -> 1))
  }
}

object MonoidEx4 {
  //Ka Order tüübiklassi isendid moodustavad monoidi. Selle monoidi assotsiatiivne tehe annab meile Order isendi, mis
  //kõigepealt proovib elemente järjestada ühe reegli alusel ja kui see ei õnnestu (s.t elemendid on võrdsed), siis teise reegli alusel.
  //Olgu antud erinevatest paaridest koosnev paaride järjend.
  //Ülesanne: järjestada paarid summa alusel ning summa sees leksikograafiliselt.

  //Selle jaoks on meil vaja täisarvude paaride peal Order-monoidi:
  implicit val orderMonoid: Monoid[Order[(Int,Int)]] = Order.whenEqualMonoid

  //Kõigepealt oleks vaja kahte Order tüüpi väärtust, et neid orderMonoid-i abil kombineerida.
  //Catsis tavaline paaare võrdlev Order on juba leksikograafiline. Olgu see muutujas lexOrder:
  private val lexOrder = Order[(Int, Int)]

  //Seega on ainult vaja defineerida summa järgi võrdlemine:

  private val sumOrder: Order[(Int, Int)] = ???

  //Nüüd kasuta meile juba tuttavat monoidide operatsiooni, et järjestusi kombineerida.

  val pairOrder: Order[(Int, Int)] = ???

  //kasuta pairOrderit paaride järjestamiseks mittekahanevalt
  def orderPairs(pairs: List[(Int, Int)]): List[(Int, Int)] = pairs.sorted(pairOrder.toOrdering)

  def main(args: Array[String]): Unit = {

    val paarid = List((2,0), (1,0), (2,2), (1,1), (0,2), (2,1), (0,0), (1,2), (0,1))
    val järjestatud = List((0,0), (0,1), (1,0), (0,2), (1,1), (2,0), (1,2), (2,1), (2,2))
    println(paarid)
    println(paarid.sorted)
    println(paarid.sorted(sumOrder.toOrdering))
    println(paarid.sorted(pairOrder.toOrdering))

    assert(orderPairs(paarid) == järjestatud)
  }
}
