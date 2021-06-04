package uk.gov.hmcts.ethos.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ethos.scenario._
import uk.gov.hmcts.ethos.scenario.utils._
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._

class EthosPTPipeline extends Simulation{

  val BashURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = http
    .baseUrl(BashURL)

  val EthosPipeline = scenario("Create Case and Update")
    .repeat(1) {
        exec(TokenGenerator.CDSGetRequest)
        .repeat(1) {
          exec(CCDCreate.ETGetSingleToken)
          .exec(CCDCreate.ETCreateSingleCase)
          .exec(CCDCreate.UpdateCasePreAcceptance)
          .exec(XUI.XUIHomePage)
          .exec(XUI.XUILogin)
          .exec(XUI.XUISearchAndOpenSingleCase)
          .exec(XUI.XUISingleUpdateCaseDetails)
          .exec(XUI.XUISingleUpdateJurisdictions)
          .exec(XUI.XUISingleAdd25Respondents)
          .exec(XUI.XUISingleList25Hearings) 
          .exec(XUI.XUISingleAllocate25Hearings) //to fix
          .exec(XUI.XUILogout)
        }
    }

  setUp(
    EthosPipeline.inject(rampUsers(1) during (1 minutes))
  ).protocols(httpProtocol)
}