package uk.gov.hmcts.ethos.scenario

import java.io.{BufferedWriter, FileWriter}

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ethos.scenario.utils._

object CCDCreate {

  val IdamURL = Environment.idamURL
  val IdamAPI = Environment.idamAPI
  val CCDEnvurl = Environment.ccdEnvurl
  val s2sUrl = Environment.s2sUrl
  val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"
  def casePrefix = "Perf50k-20201021"
  def receiptDate = "2020-10-21"
  def multiCasePrefix = "Perf-20201025/"

  val ETGetSingleToken =

    exec(http("GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/event-triggers/initiateCase/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

      .pause(1)

    .exec(
      _.setAll(
        ("CaseRefPrefix", casePrefix),
        ("CaseReceiptDate", receiptDate),
        //("CaseMultipleName", multipleName)
      )
     )

  val ETGetMultipleToken =

    exec(http("GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds_Multiple/event-triggers/createMultiple/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

      .pause(1)

      .exec(
        _.setAll(
          ("MultiCaseRefPrefix", multiCasePrefix),
        )
      )

  val feedEthosMultiName = csv("Ethos_MultipleName.csv")
  val feedEthosCaseRef = csv("EthosCaseRef.csv")

  val ETCreateCase =

    feed(feedEthosCaseRef)
    feed(feedEthosMultiName)

    .exec(http("CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("Ethos_SingleCaseForMultiple.json"))
      .check(jsonPath("$.id").saveAs("caseId"))
      .check(status.saveAs("statusvalue")))

    .doIf(session=>session("statusvalue").as[String].contains("201")) {
      exec {
        session =>
          val fw = new BufferedWriter(new FileWriter("CreateSingles.csv", true))
          try {
            fw.write(session("CaseRefPrefix").as[String] + "/" + session("caseRef").as[String] + "\r\n")
          }
          finally fw.close()
          session
      }
    }

    .pause(1)

  val feedEthosMultiCaseRef = csv("EthosMultiCaseRef.csv")

  val ETCreateMultipleCase =

  feed(feedEthosMultiCaseRef, 100)

    .exec(http("CreateMultipleCase")
      .post(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds_Multiple/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("Ethos_MultipleCase.json"))
      .check(jsonPath("$.id").saveAs("caseId"))
      .check(status.saveAs("statusvalue")))

    .doIf(session=>session("statusvalue").as[String].contains("201")) {
      exec {
        session =>
          val fw = new BufferedWriter(new FileWriter("CreateMultiples.csv", true))
          try {
            fw.write(session("CaseMultipleName").as[String] + "," + session("caseId").as[String] + "\r\n")
          }
          finally fw.close()
          session
      }
    }

    .pause(1)
}
