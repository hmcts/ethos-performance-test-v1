package uk.gov.hmcts.ethos.scenario

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import uk.gov.hmcts.ethos.scenario.utils._

object CCDCreate {

  val IdamURL = Environment.idamURL
  val IdamAPI = Environment.idamAPI
  val CCDEnvurl = Environment.ccdEnvurl
  val s2sUrl = Environment.s2sUrl
  val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"

  val ETGetToken =

    exec(http("GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/event-triggers/initiateCase/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

      .pause(4)

  val feedEthosCaseRef = csv("EthosCaseRef.csv")

  val ETCreateCase =

    feed(feedEthosCaseRef)

    .exec(http("CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("Ethos_SingleCase.json"))
      .check(jsonPath("$.id").saveAs("caseId"))
      .check(status.saveAs("statusvalue")))

    // .doIf(session=>session("statusvalue").as[String].contains("200")) {
    //   exec {
    //     session =>
    //       val fw = new BufferedWriter(new FileWriter("CreateSingles.csv", true))
    //       try {
    //         fw.write(session("caseId").as[String] + "\r\n")
    //       }
    //       finally fw.close()
    //       session
    //   }
    // }

  //val ETCreateMultipleCase =

}
