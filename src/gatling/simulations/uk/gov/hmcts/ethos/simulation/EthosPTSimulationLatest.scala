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
     .exec(
       // CreateCase.CreateSingleCase,
       CreateCase.CreateMultipleCase
     )



  setUp(
    EthosSCN.inject(atOnceUsers(1))
  ).protocols(httpProtocol)


}
