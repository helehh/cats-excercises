import mouse.boolean._

import scala.util.{Left, Right}

//Selle ülesande eesmärk on teha tutvust monaadidega.
//Monad laiendab Applicative tüübiklassi funktsiooniga flatMap:  def flatMap[A, B](fa: F[A])(f: (A) ⇒ F[B]): F[B]
//flatMap on sisuliselt funkstioonid map ja flatten koos (def flatten[A](ffa: F[F[A]]): F[A])

//Ülesanne:
//Pierre'i suureks hobiks on köiel kõndimine.
//Ainaks probleemiks on see, et teibale, millega Pierre tasakaalu hoiab, maanduvad alatihti linnud, kes teda tasakaalust välja ajavad.
//Linnud võivad maanduda nii vasakule kui ka paremale poole Pierre'i, vahepeal ära lennata ja siis uuesti tagasi tulla.
//Pierre suudab tasakaalu hoida ainult siis, kui temast paremale ja vasakule jäävate lindude arv ei erine rohkem kui kolme võrra.
//Suurema vahe korral kaotab Pierre tasakaalu ja kukub köielt alla.

// Kirjutame programmi, mis simuleerib lindude maandumist ja ära lendamist teibalt, et näha,
// kas pärast mingit maandumiste ja ära lendamiste jada on Pierre endiselt tasakaalus või alla kukkunud.
object RopeWalker1 {

  //Tüübisünonüüm lindude jaoks
  type Birds = Int

  //Teeme teiba jaoks case classi, left ja right näitavad, kui palju on teiba vastaval poolel linde
  case class Pole(left: Birds, right: Birds){

    //funktsioonid lindude maandumise jaoks
    //ära lendamise jaoks saame argumendiks anda lihtsalt negatiivse arvu
    def landLeft(b: Birds): Pole = Pole(left+b, right)

    def landRight(b: Birds): Pole = Pole(left, right+b)

    def isBalanced: Boolean = Math.abs(left - right) <= 3
  }

  def main(args: Array[String]): Unit = {

    //saame teha lindude maandumiste ja ära lendamiste ahela:
    val pole1 = Pole(0,0).landLeft(1).landRight(1).landLeft(2)

    //mis siin tegelikult Pierre'ga juhtuma peaks?
    val pole2 = Pole(0, 0).landLeft(1).landRight(4).landLeft(-1).landRight(-2)

    println(pole1)
    println(pole2)
  }
}

//Eelmises ülesandes oleks Pierre pidanud ühel hetkel tasakaalust välja minema, s.t landLeft ja landRight peaksid ebaõnnestuma, kui
//lindude arv vasakul ja paremal liiga palju erineb. Täiendame oma programmi:
object RopeWalker2{
  type Birds = Int

  case class Pole(left: Birds, right: Birds){

    //nüüd need funktsioonid tagastavad None, kui lindude arv paremal ja vasakul erineb rohkem kui kolme võrra
    //Pole(0,1).landLeft(2) => Some(Pole(2,1))
    //Pole(0,1).landLeft(5) => None
    def landLeft(b: Birds): Option[Pole] = isBalanced option Pole(left+b, right)

    def landRight(b: Birds): Option[Pole] = isBalanced option Pole(left, right+b)

    def isBalanced: Boolean = Math.abs(left - right) <= 3
  }

  def main(args: Array[String]): Unit = {

    //aga kuna need funktsioonid tagastavad nüüd Option[Pole], siis ei saa me enam teha ahelat nii nagu enne, vaid peame kasutama funktsiooni
    //flatMap (ja pure).
    //def flatMap[A, B](fa: F[A])(f: (A) ⇒ F[B]): F[B]

    import cats.Monad.ops._
    import cats.{Monad, _}
    import cats.instances.all._
    val res1 = Monad[Option].pure(Pole(0,0)).flatMap(_.landLeft(1)).flatMap(_.landRight(4)).flatMap(_.landLeft(-1)).flatMap(_.landRight(-2))

    //Mis siin ahelas täpselt toimub?
    // 1. Monad[Option].pure(Pole(0,0)) <- funktsioon pure paneb puhta väärtuse konteksti sisse. See on vajalik flatMapi jaoks. Saame Some(Pole(0,0))
    // 2. Some(Pole(0,0)).flatMap(_.landLeft(1)) <- rakendame Some-väärtusele flatMapi, s.t Pole(0,0).landLeft(1), mis annab Some(Pole(1,0))
    // 3. Some(Pole(1,0)).flatMap(_.landRight(4)) <- rakendame jälle flatMapi, s.t Pole(1,0).landRight(4), mis annab Some(Pole(1,4))
    // 4. Some(Pole(1,4)).flatMap(_.landLeft(-1)) <- rakendame flatMapi, s.t Pole(1,4).landLeft(-1), mis annab None, sest vahe on liiga suur
    // 5. None.flatMap(_.landRight(-2)) <- rakendame None-väärtusele flatMapi, mis tagastab automaatselt None.

    //kirjuta vähemalt kolmest osast koosnev ahel nii, et tulemuseks oleks mingi Some-väärtus.
    ////võid flatMapi asemel kasutada ka selle infikskuju >>=

    val res2 = ???

    //saame flatMap ahelat for-komprehensiooni abil tegelikult esitada ka rohkem loetavamal kujul.
    //kirjuta res1 ümber, kasutades for-komprehensiooni

    val res3 = ???

    //veel üks võimalus paremale ja vasakule maandumist kirjeldada on andmetüübiga Either. Sellel on kaks võimalikku väärtust Left ja Right.
    //teeme listi Either tüüpi väärtustest - need on paremale ja vasakule maandumised
    val actions: List[Either[Int,Int]] = List(Left(1), Right(2), Left(-1), Right(-1))

    //see funktsioon võtab vana seisu ja tagastab uue seisu lindude arvust
    def interpret(state: Pole, action: Either[Int,Int]): Option[Pole] = action match {
      case Left(value) => state.landLeft(value)
      case Right(value) => state.landRight(value)
    }

    //selleks, et funktsiooniga interpret tervet listi maandumisi simuleerida, kasutame Foldable tüübiklassi funktsiooni FoldM
    //foldM[G[_], A, B](fa: F[A], z: B)(f: (B, A) ⇒ G[B])(implicit G: Monad[G]): G[B]

    val res4 = ???

    println(res1)
    println(res2)
    println(res3)
    println(res4)
  }
}
