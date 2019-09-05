package jp.smartreach.members.backend.core.infrastructure.generators

import org.scalacheck.Gen
import org.scalacheck.Gen.Parameters
import org.scalacheck.rng.Seed

trait Generator {

  def forOne[T1,P](g1: Gen[T1])(f: (T1) => P): P = {
    val r1 = (for {
      r1 <- g1
    } yield (r1)).pureApply(Parameters.default,Seed.random())
    f(r1)
  }

  def forOne[T1,T2,P](g1: Gen[T1], g2: Gen[T2])(f: (T1,T2) => P): P = {
    val (r1, r2) = (for {
      r1 <- g1
      r2 <- g2
    } yield (r1, r2)).pureApply(Parameters.default,Seed.random())
    f(r1, r2)
  }

  def forOne[T1,T2,T3,P](g1: Gen[T1], g2: Gen[T2], g3: Gen[T3])(f: (T1,T2,T3) => P): P = {
    val (r1, r2, r3) = (for {
      r1 <- g1
      r2 <- g2
      r3 <- g3
    } yield (r1, r2, r3)).pureApply(Parameters.default,Seed.random())
    f(r1, r2, r3)
  }

  def forOne[T1,T2,T3,T4,P]
  (g1: Gen[T1], g2: Gen[T2], g3: Gen[T3], g4: Gen[T4])
  (f: (T1,T2,T3,T4) => P): P = {
    val (r1, r2, r3, r4) = (for {
      r1 <- g1
      r2 <- g2
      r3 <- g3
      r4 <- g4
    } yield (r1, r2, r3, r4)).pureApply(Parameters.default,Seed.random())
    f(r1, r2, r3, r4)
  }

  def forOne[T1,T2,T3,T4,T5,P]
  (g1: Gen[T1], g2: Gen[T2], g3: Gen[T3], g4: Gen[T4], g5: Gen[T5])
  (f: (T1,T2,T3,T4,T5) => P): P = {
    val (r1, r2, r3, r4, r5) = (for {
      r1 <- g1
      r2 <- g2
      r3 <- g3
      r4 <- g4
      r5 <- g5
    } yield (r1, r2, r3, r4, r5)).pureApply(Parameters.default,Seed.random())
    f(r1, r2, r3, r4, r5)
  }

  def forOne[T1,T2,T3,T4,T5,T6,P]
  (g1: Gen[T1], g2: Gen[T2], g3: Gen[T3], g4: Gen[T4], g5: Gen[T5], g6: Gen[T6])
  (f: (T1,T2,T3,T4,T5,T6) => P): P = {
    val (r1, r2, r3, r4, r5, r6) = (for {
      r1 <- g1
      r2 <- g2
      r3 <- g3
      r4 <- g4
      r5 <- g5
      r6 <- g6
    } yield (r1, r2, r3, r4, r5, r6)).pureApply(Parameters.default,Seed.random())
    f(r1, r2, r3, r4, r5, r6)
  }

  def forOne[T1,T2,T3,T4,T5,T6,T7,P]
  (g1: Gen[T1], g2: Gen[T2], g3: Gen[T3], g4: Gen[T4], g5: Gen[T5], g6: Gen[T6], g7: Gen[T7])
  (f: (T1,T2,T3,T4,T5,T6,T7) => P): P = {
    val (r1, r2, r3, r4, r5, r6, r7) = (for {
      r1 <- g1
      r2 <- g2
      r3 <- g3
      r4 <- g4
      r5 <- g5
      r6 <- g6
      r7 <- g7
    } yield (r1, r2, r3, r4, r5, r6, r7)).pureApply(Parameters.default,Seed.random())
    f(r1, r2, r3, r4, r5, r6, r7)
  }

  def forOne[T1,T2,T3,T4,T5,T6,T7,T8,P]
  (g1: Gen[T1], g2: Gen[T2], g3: Gen[T3], g4: Gen[T4], g5: Gen[T5], g6: Gen[T6], g7: Gen[T7], g8: Gen[T8])
  (f: (T1,T2,T3,T4,T5,T6,T7,T8) => P): P = {
    val (r1, r2, r3, r4, r5, r6, r7, r8) = (for {
      r1 <- g1
      r2 <- g2
      r3 <- g3
      r4 <- g4
      r5 <- g5
      r6 <- g6
      r7 <- g7
      r8 <- g8
    } yield (r1, r2, r3, r4, r5, r6, r7, r8)).pureApply(Parameters.default,Seed.random())
    f(r1, r2, r3, r4, r5, r6, r7, r8)
  }


}
