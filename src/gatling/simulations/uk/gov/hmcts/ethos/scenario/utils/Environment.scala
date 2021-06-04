package uk.gov.hmcts.ethos.scenario.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURL = "https://et-api-stg-azure.staging.et.dsd.io"
  val idamURL = "https://idam-web-public.aat.platform.hmcts.net"
  val idamAPI = "https://idam-api.aat.platform.hmcts.net"
  val ccdEnvurl = "https://www-ccd.aat.platform.hmcts.net"
  // val ccdDataStoreUrl = "ccd-data-store-api-aat.service.core-compute-aat.internal"
  val ccdDataStoreUrl = "http://ccd-data-store-api-aat.service.core-compute-aat.internal"

  val s2sUrl = "http://rpe-service-auth-provider-aat.service.core-compute-aat.internal"
  val xuiUrl = "https://manage-case.aat.platform.hmcts.net"
  val xuiTCUrl = "https://manage-case.aat.platform.hmcts.net/accept-terms-and-conditions"

  val minThinkTime = 10
  val maxThinkTime = 15
  val constantthinkTime = 5
  val minWaitForNextIteration = 1
  val maxWaitForNextIteration = 2

}
