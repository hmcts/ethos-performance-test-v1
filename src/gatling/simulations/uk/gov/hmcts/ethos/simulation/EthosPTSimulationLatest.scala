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
    //.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs

   val EthosSCN = scenario("Ethos - Case Creation")
  //  .repeat(2){
  //    exec(CreateSingle.CreateSingleCase)
  //   }
    .exec(CreateMultiple.CreateMultipleCase_100)

  val CCDCreateSingleSCN = scenario("CCD - Create Single Cases")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .repeat(10) {
          exec(CCDCreate.ETGetSingleToken)
          .exec(CCDCreate.ETCreateSingleCase)
        }
      }

  val CCDCreateSingleForMultiSCN = scenario("CCD - Create Single Cases for a Multiple Case")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .repeat(1000) {
          exec(CCDCreate.ETGetSingleToken)
          .exec(CCDCreate.ETCreateSingleCaseForMultiple)
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
        exec(XUI.XUISearchAndOpenMultipleCase)
        .exec(XUI.BatchUpdateMultiple)
      }
    }

  val XUISingleCaseJourney = scenario("XUI - Single Case Update Journey")
  .repeat(1){
      exec(XUI.XUIHomePage)
      .exec(XUI.XUILogin)
      .repeat(1) {
        exec(XUI.XUISearchAndOpenSingleCase)
        //.exec(XUI.XUISinglePreAcceptance) //Only required for cases created in the UI
        .exec(XUI.XUISingleUpdateCaseDetails)
        .exec(XUI.XUISingleUpdateJurisdictions)
        .exec(XUI.XUISingleAdd25Respondents)
        .exec(XUI.XUISingleList25Hearings)
        .exec(XUI.XUISingleAllocate25Hearings)
      }
  }

  setUp(
    //ETOnlineCreateSingleSCN.inject(rampUsers(1) during (1 minute))
    //ETOnlineCreateMultipleSCN.inject(rampUsers(1) during (1 minute))
    //XUIMultipleBatchUpdate.inject(rampUsers(1) during (1 minute))
    //XUISingleCaseJourney.inject(rampUsers(1) during (1 minute))
    //CCDCreateSingleSCN.inject(rampUsers(5) during (1 minute))
    CCDCreateSingleForMultiSCN.inject(rampUsers(50) during (10 minute))
    //CCDCreateMultipleSCN.inject(rampUsers(1) during (1 minute))

  ).protocols(httpProtocol)


}
