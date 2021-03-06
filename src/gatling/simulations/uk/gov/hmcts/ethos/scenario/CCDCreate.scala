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
  val ccdDataStoreUrl = "http://ccd-data-store-api-aat.service.core-compute-aat.internal"
  def casePrefix = "20210602"
  def receiptDate = "2021-06-02"
  // def multiCasePrefix = "Perf-20201025/"
  val feedEthosMultipleCaseRef = csv("PT_MultipleCreation_1.csv")
  val feedEthosMultiCaseNum = csv("EthosMultiCaseRef.csv")
  val feedEthosCaseReference = csv("EthosCaseRef.csv")
  val feedEthosCaseRef = csv("EthosCaseRef.csv")
  // val feedEthosMultiName = csv("Ethos_MultipleName.csv")

  val ETGetSingleToken =

    exec(http("GetEventToken")
      .get(Environment.ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/EMPLOYMENT/case-types/Leeds/event-triggers/initiateCase/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

      .pause(1)

    .exec(
      _.setAll(
        ("CaseRefPrefix", casePrefix),
        ("CaseReceiptDate", receiptDate),
        // ("CaseMultipleName", multipleName)
      )
     )

  // val feedEthosCaseRef = csv("EthosCaseRef.csv")

  val ETCreateSingleCase =

    //feed(feedEthosCaseRef)

    exec(http("CreateCase")
      .post(Environment.ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/EMPLOYMENT/case-types/Leeds/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("Ethos_SingleCaseNewApril.json"))
      .check(jsonPath("$.id").saveAs("caseId"))
      .check(regex("""ethosCaseReference":"([0-9\/]+?)","claimantIndType""").saveAs("singleName"))
      //.check(status.saveAs("statusvalue"))
      )

    // .exec {
    //   session =>
    //     println(session("singleName").as[String])
    //     session}

    // .doIf(session=>session("statusvalue").as[String].contains("201")) {
    //   exec {
    //     session =>
    //       val fw = new BufferedWriter(new FileWriter("CreateSingles.csv", true))
    //       try {
    //         fw.write(session("CaseRefPrefix").as[String] + "/" + session("caseRef").as[String] + "\r\n")
    //       }
    //       finally fw.close()
    //       session
    //   }
    // }

    .pause(1)

  val UpdateCasePreAcceptance = 

    exec(http("GetEventToken")
      .get(Environment.ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/EMPLOYMENT/case-types/Leeds/cases/${caseId}/event-triggers/preAcceptanceCase/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

      .pause(1)

    .exec(http("PreAcceptance")
      .post(Environment.ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/EMPLOYMENT/case-types/Leeds/cases/${caseId}/events")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("Ethos_SingleCasePreAcceptance.json")))

    .pause(1)

  val ETGetMultipleToken =

    exec(http("GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/EMPLOYMENT/case-types/Leeds_Multiple/event-triggers/createMultiple/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

      .pause(1)

      // .exec(
      //   _.setAll(
      //     ("MultiCaseRefPrefix", multiCasePrefix),
      //   )
      // )

  // val feedEthosCaseRef = csv("EthosCaseRef.csv").queue

  val ETCreateSingleCaseForMultiple =
    
    // feed(feedEthosMultiName)
    feed(feedEthosCaseReference)

    .exec(http("CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/EMPLOYMENT/case-types/Leeds/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("Ethos_SingleCaseForMultiple.json"))
      .check(jsonPath("$.id").saveAs("caseId"))
      .check(status.saveAs("statusvalue")))

    .doIf(session=>session("statusvalue").as[String].contains("201")) {
      exec {
        session =>
          val fw = new BufferedWriter(new FileWriter("CreateSinglesForMultiple_TestingFeb_2.csv", true))
          try {
            fw.write(session("multipleName").as[String] + "," + session("multipleName").as[String] + "-" + session("CaseRefPrefix").as[String] + "/" + session("caseReference").as[String] + "\r\n")
          }
          finally fw.close()
          session
      }
    }

    .pause(1)

  val ETCreateMultipleCase =

    feed(feedEthosMultipleCaseRef, 200)
    .feed(feedEthosMultiCaseNum, 200)

    .exec(http("CreateMultipleCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/EMPLOYMENT/case-types/Leeds_Multiple/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("Ethos_MultipleCase200.json"))
      .check(jsonPath("$.id").saveAs("caseId"))
      .check(status.saveAs("statusvalue")))

    .doIf(session=>session("statusvalue").as[String].contains("201")) {
      exec {
        session =>
          val fw = new BufferedWriter(new FileWriter("CreateMultiplesFeb_ForPT.csv", true))
          try {
            fw.write(session("multipleName1").as[String] + "," + session("caseId").as[String] + "," + session("multipleRef1").as[String] + "\r\n")
          }
          finally fw.close()
          session
      }
    }

    .pause(1)
}
