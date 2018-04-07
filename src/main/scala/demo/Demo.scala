package demo.batch.temp

import com.fintech.lib.batch.{AltProcessor, BatchContext, Item}

import scala.util.{Success, Try}

object Demo extends App {

  val context = BatchContext[String, Symbol]
  import context._

  val commissions = for {
    src <- source()
    order <- parse(src)
    commission <- computeCommission(order.amount, order.terms)
    _ <- post(order.id, commission)
    line <- index().map(_ + 1)
    _ <- guard('Line6)(line != 6)
    publisher <- publisherForContract(order.terms)
    advertiser <- advertiserForContract(order.terms)
    _ <- log(s"LOG: $line: commission $commission posted to publisher $publisher from advertiser $advertiser for order ${order.id}")
  } yield ()


  val result = commissions.exec(DemoData.batch)

  println("rejected: ")
  result.incomplete.map(i => s"${i.index + 1}: ${i.value}  (${i.source})").foreach(r => println("  " + r))


  def parse(line: String): Processor[Order] = {
    if (line.trim() == "")
      return reject('BlankLine)
    val parseError = 'Parse
    val Pattern = """([^ ,]+)\s*,\s*([^ ,]+)\s*,\s*([^ ,]+)\s*,\s*([^ ,]+)\s*.*""".r
    Try {
      line match {
        case Pattern(id, terms, amount, product) => pure(Order(id, BigInt(terms), BigDecimal(amount), BigInt(product)))
        case _ => reject(parseError)
      }
    } match {
      case Success(maybeOrder) => maybeOrder
      case _ => reject(parseError)
    }
  }

  def publisherForContract(contractId: BigInt): Processor[BigInt] = pure(contractId / 7)
  def advertiserForContract(contractId: BigInt): Processor[BigInt] = pure(contractId % 3)

  def computeCommission(amount: BigDecimal, contractId: BigInt): Processor[BigDecimal] = {
    if (contractId == 1900) reject('InvalidContract)
    else {
      val rate = BigDecimal(contractId) / 100.0
      pure(rate * amount)
    }
  }

  def post(orderId: String, amount: BigDecimal): Processor[Unit] = {
    if (amount < 0) reject ('InvalidAmount)
    else {
      // write it to the shopcart
      pure(())
    }
  }

  def log(msg: String): Processor[Unit] = {
    println(msg)
    pure(())
  }

}

case class Order(id: String, terms: BigInt, amount: BigDecimal, product: BigInt )

object DemoData {

  val batch =
    //  order,   terms, amount ,   product
    """ |x29059,   1013, 7.53,  19
        |z390,     1013, -9.12,  254
        |A00033,   1900, 1.01,  80
        |A00034,   1901, 15.03, 80
        |how did this get in here?
        |qx49cc-5, 12,   303.73, 150
        |qx49cc-6, 12,   109.33, 153
    """.stripMargin.split("\n")



}

