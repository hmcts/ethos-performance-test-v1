package uk.gov.hmcts.ethos.scenario.utils
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURL = "https://et-api-stg-azure.staging.et.dsd.io"

  val minThinkTime = 11
  val maxThinkTime = 14

}
