package uk.gov.hmcts.ethos.scenario.utils
import com.typesafe.config.ConfigFactory
import com.warrenstrange.googleauth.GoogleAuthenticator
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.http.ContentType

object Environment {

  //val baseURL = "https://et-api-dev-azure.dev.et.dsd.io"
  val baseURL = "https://et-api-stg-azure.staging.et.dsd.io"

  val minThinkTime = 10
  val maxThinkTime = 15
  val constantthinkTime = 2
  val minWaitForNextIteration = 1
  val maxWaitForNextIteration = 2

  private val config = ConfigFactory.load()

  val TOKEN_LEASE_URL = config.getString("s2sUrl")
  val USERTOKEN_SidAM_URL = config.getString("idam_api_url")

  /**
    * Kapil Jain: Helper function to optionally apply a proxy if set in the config - ToDo
    */

  val baseURL = config.getString("baseUrl")


  def generateS2SToken() : String = {

    val authenticator: GoogleAuthenticator = new GoogleAuthenticator()

    val password = authenticator.getTotpPassword(config.getString("ethos_perftest_service.pass"))

    val jsonPayload: String = """{"microservice":"""" + config.getString("ethos_perftest_service.name") + """","oneTimePassword":"""" + password + """"}"""

    val s2sRequest = RestAssured.given
      .contentType("application/json")
      .accept("application/json")
    //  .proxy("proxyout.reform.hmcts.net", 8080)
      .body(jsonPayload)
      .post(TOKEN_LEASE_URL +"/lease")
      .then()
      .statusCode(200)
      .extract()
      .response()

    val token = s2sRequest.asString()

    System.out.println(token)

    token

  }

  //=======================================

  def generateEthosSIDAMUserTokenInternal() : String = {
   // return generateEthosSIDAMUserTokenInternal("eric.ccdcooper@gmail.com")
    return generateEthosSIDAMUserTokenInternal("ccdloadtest4700@gmail.com")
  }

  def generateEthosSIDAMUserTokenInternal(userName : String) : String = {

    val authCodeRequest = RestAssured.given().config(RestAssured.config()
      .encoderConfig(EncoderConfig.encoderConfig()
        .encodeContentTypeAs("x-www-form-urlencoded",
          ContentType.URLENC)))
      .contentType("application/x-www-form-urlencoded; charset=UTF-8")
     // .proxy("proxyout.reform.hmcts.net", 8080)
      .formParam("username", userName)
      .formParam("password", "Password12")
      .formParam("client_id", "ccd_gateway")
      .formParam("client_secret", "vUstam6brAsT38ranuwRut65rakec4u6")
      .formParam("redirect_uri", "https://www-ccd.perftest.platform.hmcts.net/oauth2redirect")
      .formParam("grant_type", "password")
      .formParam("scope", "openid profile authorities acr roles")
      .request()


    val response = authCodeRequest.post(USERTOKEN_SidAM_URL + ":443/o/token")

    val statusCode = response.getStatusCode()

    val tokenStr = response.asString()

    val tokenIndexStart = tokenStr.indexOf(":")

    val tokenIndexEnd = tokenStr.indexOf(",")

    val token =  tokenStr.substring(tokenIndexStart+2,tokenIndexEnd -1 )

    System.out.println(token)

    "Bearer " + token
  }

}
