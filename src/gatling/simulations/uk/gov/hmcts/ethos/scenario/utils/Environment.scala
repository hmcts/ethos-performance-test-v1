package uk.gov.hmcts.ethos.scenario.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURL = "https://et-api-stg-azure.staging.et.dsd.io"
  val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
  val idamAPI = "https://idam-api.perftest.platform.hmcts.net"
  val ccdEnvurl = "https://www-ccd.perftest.platform.hmcts.net"
  val ccdDataStoreUrl = "ccd-data-store-api-perftest.service.core-compute-perftest.internal"
  val s2sUrl = "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal"
  val xuiUrl = "https://manage-case.perftest.platform.hmcts.net"
  val xuiTCUrl = "https://manage-case.perftest.platform.hmcts.net/accept-terms-and-conditions"

  val minThinkTime = 10
  val maxThinkTime = 15
  val constantthinkTime = 5
  val minWaitForNextIteration = 1
  val maxWaitForNextIteration = 2

}
