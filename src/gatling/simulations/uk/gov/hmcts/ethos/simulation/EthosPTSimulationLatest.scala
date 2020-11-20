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
  val feedEthosMultiName = csv("Ethos_MultipleName.csv")
  val feedEthosCaseRef = csv("EthosCaseRef.csv").circular

  val httpProtocol = http
    .baseUrl(BashURL)
    // .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    // .noProxyFor(Environment.xuiUrl)

   val EthosSCN = scenario("Ethos - Case Creation")
  //  .repeat(2){
  //    exec(CreateSingle.CreateSingleCase)
  //   }
    .exec(CreateMultiple.CreateMultipleCase_100)

  val CCDCreateSingleSCN = scenario("CCD - Create Single Cases")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .repeat(1) {
          exec(CCDCreate.ETGetSingleToken)
          .exec(CCDCreate.ETCreateSingleCase)
        }
      }

  val CCDCreateSingleForMultiSCN = scenario("CCD - Create Single Cases for a Multiple Case")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .feed(feedEthosMultiName).feed(Feeders.DataFeeder)
        .repeat(200) {
          //feed(feedEthosCaseRef).feed(Feeders.DataFeeder)
          exec(CCDCreate.ETGetSingleToken)
          .exec(CCDCreate.ETCreateSingleCaseForMultiple)
        }
      }

  val CCDCreateMultipleSCN = scenario("CCD - Create Multiple Case from Singles")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .repeat(1) {
          exec(CCDCreate.ETGetMultipleToken)
          .exec(CCDCreate.ETCreateMultipleCase)
        }
      }

  val ETOnlineCreateSingleSCN = scenario("ETOnline - Create Single Case")
    .repeat(2) {
      exec(ETOnline_CreateCase.ETOnline_CreateSingle)
    }

  val ETOnlineCreateMultipleSCN = scenario("ETOnline - Create Multiple Case")
    .repeat(2) {
      exec(ETOnline_CreateCase.ETOnline_CreateMultiple)
    }

  val XUIMultipleBatchUpdate = scenario("XUI - Batch Update Multiple Case")
    .repeat(1){
      exec(XUI.XUIHomePage)
      .exec(XUI.XUILogin)
      .repeat(3) { //3
        exec(XUI.XUISearchAndOpenMultipleCase)
        .exec(XUI.BatchUpdateMultiple)
      }
      .exec(XUI.XUILogout)
    }

  val XUISingleCaseJourney = scenario("XUI - Single Case Update Journey")
  .repeat(1){
      exec(XUI.XUIHomePage)
      .exec(XUI.XUILogin)
      .repeat(3) { //3
        exec(XUI.XUISearchAndOpenSingleCase)
        //.exec(XUI.XUISinglePreAcceptance) //Only required for cases created in the UI
        .exec(XUI.XUISingleUpdateCaseDetails)
        .exec(XUI.XUISingleUpdateJurisdictions)
        .exec(XUI.XUISingleAdd25Respondents)
        .exec(XUI.XUISingleList25Hearings)
        .exec(XUI.XUISingleAllocate25Hearings)
      }
      .exec(XUI.XUILogout)
  }

  setUp(
    ETOnlineCreateSingleSCN.inject(rampUsers(30) during (20 minutes)),
    ETOnlineCreateMultipleSCN.inject(rampUsers(20) during (20 minutes)),
    XUIMultipleBatchUpdate.inject(rampUsers(16) during (20 minutes)), //16
    XUISingleCaseJourney.inject(rampUsers(24) during (20 minutes)), //24
    //CCDCreateSingleSCN.inject(rampUsers(1) during (1 minute))
    //CCDCreateSingleForMultiSCN.inject(rampUsers(1) during (1 minutes))
    // CCDCreateMultipleSCN.inject(rampUsers(1) during (1 minute))

  ).protocols(httpProtocol)


}
