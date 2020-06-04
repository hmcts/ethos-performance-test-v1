package uk.gov.hmcts.ethos.scenario

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ethos.scenario.utils._
import scala.concurrent.duration._
import scala.util.Random

object BulkUpdate10Claimant {

  private val config = ConfigFactory.load()

  private val rng: Random = new Random()
  private def respondentSurname_v2(): String = rng.alphanumeric.take(10).mkString
  private def caseSummaryText(): String = rng.alphanumeric.take(20).mkString
  private def caseDescriptionText(): String = rng.alphanumeric.take(30).mkString

  val MinThinkTime = Environment.minThinkTime

  val MaxThinkTime = Environment.maxThinkTime
  val EventId = "updateBulkAction_v2"
  var SaveEventUrl = config.getString("caseDataUrl") + "/" + config.getString("saveEthosEvent2ClaimantUrl")

  println("Update case url: " + SaveEventUrl)

  val caseReferenceValue = Array("1574773455019683","1574773468513326")


  val casereference_random_index = rng.nextInt(caseReferenceValue.length)

  println("caseReferenceValue.length: " + casereference_random_index)
  println("caseReferenceValue(casereference_random_index)): " + caseReferenceValue(casereference_random_index))

  val caseReferenceVal = caseReferenceValue(casereference_random_index)

  println("caseReferenceVal : " + caseReferenceVal)

  private def caseReference(): String = caseReferenceVal


  // val EventBodyMainDemo = StringBody("""{"data": { "respondentSurname_v2": "Kapil ${RespondentSurname_v2}", "claimantRep_v2": null, "respondentRep_v2": null, "managingOffice": null, "subMultipleDynamicList": {   "value": {     "code": "999999",     "label": "None"   },   "list_items": [     {       "code": "999999",       "label": "None"     }   ] }, "multipleReference_v2": null, "clerkResponsible_v2": null, "positionType_v2": null, "flag1Update": null, "flag2Update": null, "EQPUpdate": null, "jurCodesDynamicList": {   "value": {     "code": "None", "label": "None"   },   "list_items": [     {       "code": "None",       "label": "None"     }   ] }},"event": { "id": "updateBulkAction_v2", "summary": "Bulk update summary - ${CaseSummaryText}", "description": "Bulk update description - ${CaseDescriptionText}"},"event_token": """"  + "${eventToken}" +   """","ignore_warning": false}""")


  val EventBodyMain = StringBody("""{"data": {"respondentSurname_v2": "Kapil ${RespondentSurname_v2}","claimantRep_v2": null,"respondentRep_v2": null,"fileLocation_v2": null,"subMultipleDynamicList": {"value": {"code": "999999","label": "None"},"list_items": [{"code": "999999","label": "None"}]},"multipleReference_v2": null,"clerkResponsible_v2": null,"positionType_v2": null,"flag1Update": null,"flag2Update": null,"EQPUpdate": null,"jurCodesDynamicList": {"value": {"code": "None","label": "None"},"list_items": [{"code": "None","label": "None"}]}},"event": {"id": "updateBulkAction_v2","summary": "Bulk update summary - ${CaseSummaryText}", "description": "Bulk update description - ${CaseDescriptionText}"},"event_token": """"  + "${eventToken}" +   """","ignore_warning": false}""")

  val CaseProviderSeq = csv("caseFeeder_10claimant.csv").circular

  val s2sToken = Environment.generateS2SToken()
  val userToken = Environment.generateEthosSIDAMUserTokenInternal()

  val ethosBulkUpdate =  exec(
    _.setAll(
      ("RespondentSurname_v2", respondentSurname_v2()),
      ("CaseReference", caseReference()),
      ("CaseSummaryText",caseSummaryText()),
      ("CaseDescriptionText",caseDescriptionText())
    )
  )
    .feed(CaseProviderSeq)
    .exec(http("TX01_Ethos_bulkupdate_10claimant_saveeventtoken")
        .get(SaveEventUrl.replace(":case_reference","${CaseRef}").replaceAll("events", "") + "event-triggers/${EventId}/token")
        .header("ServiceAuthorization", s2sToken)
        .header("Authorization", userToken)
        .header("Content-Type","application/json")
        .check(status.is(200),jsonPath("$.token").saveAs("eventToken"))
    )
    
    .exec(http("TX02_Ethos_bulkupdate_10claimant")
      .post(SaveEventUrl.replace(":case_reference","${CaseRef}"))
      .body(StringBody(EventBodyMain)).asJson
      .header("ServiceAuthorization", s2sToken)
      .header("Authorization", userToken)
      .header("Content-Type","application/json")
      .check(status is 201)
  )
    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  // .pause(880 seconds, 900 seconds)


}