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
        .repeat(1062) {
          exec(CCDCreate.ETGetSingleToken)
          .exec(CCDCreate.ETCreateCase)
        }
      }

  val CCDCreateMultipleSCN = scenario("CCD - Create Single Cases")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .repeat(1) {
          exec(CCDCreate.ETGetMultipleToken)
          .exec(CCDCreate.ETCreateMultipleCase)
        }
      }

  val ETOnlineCreateSingleSCN = scenario("ETOnline - Create Single Case")
    .repeat(1) {
      exec(ETOnline_CreateCase.ETOnline_CreateSingle)
    }

  val ETOnlineCreateMultipleSCN = scenario("ETOnline - Create Multiple Case")
    .repeat(1) {
      exec(ETOnline_CreateCase.ETOnline_CreateMultiple)
    }

  val XUIMultipleBatchUpdate = scenario("XUI - Batch Update Multiple Case")
    .repeat(1){
      exec(XUI.XUIHomePage)
      .exec(XUI.XUILogin)
      .repeat(1) {
        exec(XUI.XUISearchAndOpenCase)
        .exec(XUI.BatchUpdateMultiple)
      }
    }

  setUp(
    //EthosSCN.inject(atOnceUsers(1))
    //ETOnlineCreateSingleSCN.inject(rampUsers(1) during (1 minute))
    //ETOnlineCreateMultipleSCN.inject(rampUsers(1) during (1 minute))
    XUIMultipleBatchUpdate.inject(rampUsers(1) during (1 minute))
    //CCDCreateSingleSCN.inject(rampUsers(1) during (1 minute))
    //CCDCreateMultipleSCN.inject(rampUsers(1) during (1 minute))
    //EthosSCN.inject(rampUsers(50) during (20 minutes))

  ).protocols(httpProtocol)


}
