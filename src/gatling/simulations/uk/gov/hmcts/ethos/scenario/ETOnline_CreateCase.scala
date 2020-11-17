package uk.gov.hmcts.ethos.scenario

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import uk.gov.hmcts.ethos.scenario.utils._
import java.io.{BufferedWriter, FileWriter}
import java.util.UUID.randomUUID

object ETOnline_CreateCase {

    def createSingleuUID: String = randomUUID.toString
    def createMultipleuUID: String = randomUUID.toString
    def dateTime = "100_001"

    val ETOnline_CreateSingle = 
    
        exec(_.setAll(
            ("createSingleUUID", createSingleuUID),
        ))

        .exec(http("ETOnline_CreateSingle")
            .post(Environment.baseURL + "/api/v2/claims/build_claim")
            .body(ElFileBody("ETOnline_SingleCase.json"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json"))

    val ETOnline_CreateMultiple = 

        exec(_.setAll(
            ("createMultipleUUID", createMultipleuUID),
            ("dateTime", dateTime)
        ))

        .exec(http("ETOnline_CreateMultiple")
            .post(Environment.baseURL + "/api/v2/claims/build_claim")
            .body(ElFileBody("ETOnline_100claimant.json"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json"))

}