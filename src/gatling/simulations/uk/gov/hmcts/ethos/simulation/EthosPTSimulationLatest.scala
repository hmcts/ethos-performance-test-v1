package uk.gov.hmcts.ethos.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ethos.scenario._
import uk.gov.hmcts.ethos.scenario.utils._

import scala.concurrent.duration._

class EthosPTSimulationLatest extends Simulation{

  val BashURL = Environment.baseURL

  val httpProtocol = http
    .baseUrl(BashURL)

   val EthosSCN = scenario("Ethos - Case Creation")
   .repeat(2){
     exec(CreateSingle.CreateSingleCase)
    }
    .exec(CreateMultiple.CreateMultipleCase_100)

  setUp(
    //EthosSCN.inject(atOnceUsers(1))
    EthosSCN.inject(rampUsers(50) during (30 minutes))

  ).protocols(httpProtocol)


}
