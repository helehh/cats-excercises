import org.scalatest.FunSuite

class MonoidTest extends FunSuite {

  import MonoidEx2._
  import MonoidEx3.avg

  test("1. AndMonoid ja combineAll.") {
    assert(combineAll(List(true, false, true)) === false)
    assert(combineAll(List(true, true)) === true)
  }

  test("2. Avg") {
    assert(avg(List(1, 2, 3)) === 2)
  }
}

