package uk.gov.hmcts.ethos.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ethos.scenario._
import uk.gov.hmcts.ethos.scenario.utils._
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._

class EthosPTSimulationLatest extends Simulation{

  val BashURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = http
    .baseUrl(BashURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs

   val EthosSCN = scenario("Ethos - Case Creation")
  //  .repeat(2){
  //    exec(CreateSingle.CreateSingleCase)
  //   }
    .exec(CreateMultiple.CreateMultipleCase_100)

  val CCDCreateSingleSCN = scenario("CCD - Create Single Cases")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .repeat(1) {
          exec(CCDCreate.ETGetToken)
          .exec(CCDCreate.ETCreateCase)
        }
      }

  setUp(
    //EthosSCN.inject(atOnceUsers(1))
    CCDCreateSingleSCN.inject(rampUsers(1) during (1 minute))
    //EthosSCN.inject(rampUsers(50) during (20 minutes))

  ).protocols(httpProtocol)


}
