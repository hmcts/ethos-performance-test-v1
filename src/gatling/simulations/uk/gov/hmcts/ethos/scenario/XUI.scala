package uk.gov.hmcts.ethos.scenario

import java.io.{BufferedWriter, FileWriter}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ethos.scenario.utils._

object XUI {

    val feedMultipleName = csv("Ethos_MultipleName.csv")
    val feedSingleName = csv("Ethos_SingleName.csv")
	val xuiLogins = csv("CaseworkerLogins.csv")

    val XUIHomePage =

        exec(http("XUI_010_005_Homepage")
            .get(Environment.xuiUrl + "/")
            .headers(XUIHeaders.headers_0)
            .check(status.in(200,304))).exitHereIfFailed

        .exec(http("XUI_010_010_Homepage")
            .get(Environment.xuiUrl + "/assets/config/config.json")
            .headers(XUIHeaders.headers_1))

        .exec(http("XUI_010_015_HomepageTCEnabled")
            .get(Environment.xuiUrl + "/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(XUIHeaders.headers_1))

        .exec(http("XUI_010_020_HomepageIsAuthenticated")
            .get(Environment.xuiUrl + "/auth/isAuthenticated")
            .headers(XUIHeaders.headers_1))

        .exec(http("XUI_010_025_Homepage")
            .get(Environment.xuiUrl + "/auth/login")
            .headers(XUIHeaders.headers_4)
            .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
            .check(regex("manage-user%20create-user&state=(.*)&client").saveAs("state")))

        .pause(Environment.constantthinkTime)

    val XUILogin =

		feed(xuiLogins)

        .exec(http("XUI_020_005_SignIn")
            .post(Environment.idamURL + "/login?response_type=code&redirect_uri=" + Environment.xuiUrl + "%2Foauth2%2Fcallback&scope=profile%20openid%20roles%20manage-user%20create-user&state=${state}&client_id=xuiwebapp")
            .formParam("username", "${email}")
            .formParam("password", "Password12")
            .formParam("save", "Sign in")
            .formParam("selfRegistrationEnabled", "false")
            .formParam("_csrf", "${csrfToken}")
            .headers(XUIHeaders.headers_login_submit)
            .check(status.in(200, 304, 302))).exitHereIfFailed

        .exec(http("XUI_020_010_Homepage")
            .get(Environment.xuiUrl + "/external/config/ui")
            .headers(XUIHeaders.headers_0)
            .check(status.in(200,304)))

        .exec(http("XUI_020_015_SignInTCEnabled")
            .get(Environment.xuiUrl + "/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(XUIHeaders.headers_38)
            .check(status.in(200, 304)))

        .exec(http("XUI_020_020_AcceptT&CAccessJurisdictions")
            .get(Environment.xuiUrl + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
            .headers(XUIHeaders.headers_access_read)
            .check(status.in(200, 304, 302)))

        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("xsrfToken")))

        .pause(Environment.constantthinkTime)

    val XUISearchAndOpenMultipleCase = 

        feed(feedMultipleName)

        .exec(http("XUI_030_SearchForMultipleCase")
			.post(Environment.xuiUrl + "/data/internal/searchCases?ctid=Leeds_Multiple&use_case=WORKBASKET&view=WORKBASKET&page=1&case.multipleName=${multipleName}")
			.headers(XUIHeaders.ethos_headers_0)
            .body(StringBody("{\n  \"size\": 25\n}"))
            .check(regex("""case_id":"(.*)","supplementary_data"""").saveAs("caseId")))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_040_005_OpenCaseHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_040_010_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_2))
            
		.pause(Environment.constantthinkTime)

    val BatchUpdateMultiple = 

        exec(http("XUI_050_005_SelectBatchUpdateHealthCheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_050_010_SelectBatchUpdateAction")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/batchUpdateCases?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_4)
            .check(jsonPath("$.event_token").saveAs("eventToken")))

        .exec(http("XUI_050_015_SelectBatchUpdateHealthCheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2FbatchUpdateCases1")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_050_020_GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

        .pause(Environment.constantthinkTime)

        .exec(http("XUI_060_005_SelectBatchUpdatePage1")
			.post(Environment.xuiUrl + "/data/case-types/Leeds_Multiple/validate?pageId=batchUpdateCases1")
			.headers(XUIHeaders.ethos_headers_7)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": \"All\",\n    \"flag2\": \"All\",\n    \"flag3\": \"All\",\n    \"flag4\": \"All\"\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

        .exec(http("XUI_060_010_SelectBatchUpdateActionHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2FbatchUpdateCases2")
			.headers(XUIHeaders.ethos_headers_1))

        .pause(Environment.constantthinkTime)

		.exec(http("XUI_070_005_SelectBatchUpdatePage2")
			.post(Environment.xuiUrl + "/data/case-types/Leeds_Multiple/validate?pageId=batchUpdateCases2")
			.headers(XUIHeaders.ethos_headers_7)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateCase\": \"${batchCaseNumber}\"\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"batchUpdateCase\": \"${batchCaseNumber}\"\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

        .exec(http("XUI_070_010_SelectBatchUpdatePage2Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2FbatchUpdateCases5")
			.headers(XUIHeaders.ethos_headers_1))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_080_005_SelectBatchUpdatePage3")
			.post(Environment.xuiUrl + "/data/case-types/Leeds_Multiple/validate?pageId=batchUpdateCases5")
			.headers(XUIHeaders.ethos_headers_7)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateClaimantRep\": {\n      \"value\": {\n        \"code\": \"None\",\n        \"label\": \"None\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        }\n      ]\n    },\n    \"batchUpdateJurisdiction\": {\n      \"value\": {\n        \"code\": \"DRB\",\n        \"label\": \"DRB\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"DDA\",\n          \"label\": \"DDA\"\n        },\n        {\n          \"code\": \"DRB\",\n          \"label\": \"DRB\"\n        },\n        {\n          \"code\": \"PID\",\n          \"label\": \"PID\"\n        },\n        {\n          \"code\": \"RRD\",\n          \"label\": \"RRD\"\n        },\n        {\n          \"code\": \"SXD\",\n          \"label\": \"SXD\"\n        },\n        {\n          \"code\": \"UDL\",\n          \"label\": \"UDL\"\n        }\n      ]\n    },\n    \"batchUpdateRespondent\": {\n      \"value\": {\n        \"code\": \"Ptr Atnsn\",\n        \"label\": \"Ptr Atnsn\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"freds respondent\",\n          \"label\": \"freds respondent\"\n        },\n        {\n          \"code\": \"Kzt Fnn\",\n          \"label\": \"Kzt Fnn\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Jn Rtt\",\n          \"label\": \"Jn Rtt\"\n        },\n        {\n          \"code\": \"Alxzndr Czdwt\",\n          \"label\": \"Alxzndr Czdwt\"\n        },\n        {\n          \"code\": \"Nl Ld\",\n          \"label\": \"Nl Ld\"\n        },\n        {\n          \"code\": \"Crstpr Wnspzr\",\n          \"label\": \"Crstpr Wnspzr\"\n        },\n        {\n          \"code\": \"Ptr Atnsn\",\n          \"label\": \"Ptr Atnsn\"\n        },\n        {\n          \"code\": \"Annz Crzwly\",\n          \"label\": \"Annz Crzwly\"\n        },\n        {\n          \"code\": \"Elzzxt Cunnnzm\",\n          \"label\": \"Elzzxt Cunnnzm\"\n        },\n        {\n          \"code\": \"Andrw Wnwrt\",\n          \"label\": \"Andrw Wnwrt\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Aysz Kzn\",\n          \"label\": \"Aysz Kzn\"\n        },\n        {\n          \"code\": \"Mtzl Rds\",\n          \"label\": \"Mtzl Rds\"\n        },\n        {\n          \"code\": \"bob rob\",\n          \"label\": \"bob rob\"\n        },\n        {\n          \"code\": \"Stpn Frsyt\",\n          \"label\": \"Stpn Frsyt\"\n        },\n        {\n          \"code\": \"k rita\",\n          \"label\": \"k rita\"\n        },\n        {\n          \"code\": \"Tn Bvll\",\n          \"label\": \"Tn Bvll\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"batchUpdateCase\": \"${batchCaseNumber}\",\n    \"batchUpdateClaimantRep\": \"None\",\n    \"batchUpdateJurisdiction\": \"DRB\",\n    \"batchUpdateRespondent\": \"Ptr Atnsn\"\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

        .exec(http("XUI_080_010_SelectBatchUpdatePage3Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_080_015_GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_090_005_SubmitBatchUpdateEvent")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_14)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"batchUpdateCase\": \"${batchCaseNumber}\",\n    \"batchUpdateClaimantRep\": {\n      \"value\": {\n        \"code\": \"None\",\n        \"label\": \"None\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        }\n      ]\n    },\n    \"batchUpdateJurisdiction\": {\n      \"value\": {\n        \"code\": \"DRB\",\n        \"label\": \"DRB\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"DDA\",\n          \"label\": \"DDA\"\n        },\n        {\n          \"code\": \"DRB\",\n          \"label\": \"DRB\"\n        },\n        {\n          \"code\": \"PID\",\n          \"label\": \"PID\"\n        },\n        {\n          \"code\": \"RRD\",\n          \"label\": \"RRD\"\n        },\n        {\n          \"code\": \"SXD\",\n          \"label\": \"SXD\"\n        },\n        {\n          \"code\": \"UDL\",\n          \"label\": \"UDL\"\n        }\n      ]\n    },\n    \"batchUpdateRespondent\": {\n      \"value\": {\n        \"code\": \"Ptr Atnsn\",\n        \"label\": \"Ptr Atnsn\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"freds respondent\",\n          \"label\": \"freds respondent\"\n        },\n        {\n          \"code\": \"Kzt Fnn\",\n          \"label\": \"Kzt Fnn\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Jn Rtt\",\n          \"label\": \"Jn Rtt\"\n        },\n        {\n          \"code\": \"Alxzndr Czdwt\",\n          \"label\": \"Alxzndr Czdwt\"\n        },\n        {\n          \"code\": \"Nl Ld\",\n          \"label\": \"Nl Ld\"\n        },\n        {\n          \"code\": \"Crstpr Wnspzr\",\n          \"label\": \"Crstpr Wnspzr\"\n        },\n        {\n          \"code\": \"Ptr Atnsn\",\n          \"label\": \"Ptr Atnsn\"\n        },\n        {\n          \"code\": \"Annz Crzwly\",\n          \"label\": \"Annz Crzwly\"\n        },\n        {\n          \"code\": \"Elzzxt Cunnnzm\",\n          \"label\": \"Elzzxt Cunnnzm\"\n        },\n        {\n          \"code\": \"Andrw Wnwrt\",\n          \"label\": \"Andrw Wnwrt\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Aysz Kzn\",\n          \"label\": \"Aysz Kzn\"\n        },\n        {\n          \"code\": \"Mtzl Rds\",\n          \"label\": \"Mtzl Rds\"\n        },\n        {\n          \"code\": \"bob rob\",\n          \"label\": \"bob rob\"\n        },\n        {\n          \"code\": \"Stpn Frsyt\",\n          \"label\": \"Stpn Frsyt\"\n        },\n        {\n          \"code\": \"k rita\",\n          \"label\": \"k rita\"\n        },\n        {\n          \"code\": \"Tn Bvll\",\n          \"label\": \"Tn Bvll\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false\n}")))

        .exec(http("XUI_090_010_SubmitBatchUpdateHealthCheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_090_015_GetCaseDetails")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_2))

		.exec {
        session =>
          val fw = new BufferedWriter(new FileWriter("BurntData_Multiples.csv", true))
          try {
            fw.write(session("multipleName").as[String] + "\r\n")
          }
          finally fw.close()
          session
      }

    val XUISearchAndOpenSingleCase = 

        feed(feedSingleName)

        .exec(http("XUI_030_SearchForSingleCase")
			.post(Environment.xuiUrl + "/data/internal/searchCases?ctid=Leeds&use_case=WORKBASKET&view=WORKBASKET&page=1&case.ethosCaseReference=${singleName}")
			.headers(XUIHeaders.ethos_headers_0)
			.body(StringBody("{\n  \"size\": 25\n}"))
            .check(regex("""case_id":"(.*)","supplementary_data"""").saveAs("caseId")))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_040_005_OpenCaseHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_1))
        
        .exec(http("XUI_040_010_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_2)
            .check(regex("""Date of Receipt","hidden":null,"value":"(.+?)","metadata""").saveAs("receiptDate")))

        .pause(Environment.constantthinkTime)

    val XUISingleUpdateCaseDetails =

        exec(http("XUI_050_005_SelectAmendCaseDetailsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FamendCaseDetails")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_050_010_SelectAmendCaseDetails")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/amendCaseDetails?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_4)
            .check(jsonPath("$.event_token").saveAs("eventToken"))
            .check(regex("""receiptDate","label":"Date of Receipt","hidden":null,"value":"(.+?)","metadata":false,"hint_text":null,"field_type""").saveAs("receiptDate"))
            .check(regex("""feeGroupReference","label":"Submission Reference","hidden":null,"value":"(.+?)","metadata""").saveAs("feeGroupReference"))
            .check(regex("""clerkResponsible","label":"Clerk Responsible","hidden":null,"value":"(.+?)","metadata""").saveAs("clerkResponsible"))
            .check(regex("""positionType","label":"Current Position","hidden":null,"value":"(.+?)","metadata""").saveAs("positionType"))
            .check(regex("""fileLocation","label":"Physical Location","hidden":null,"value":"(.+?)","metadata""").saveAs("fileLocation"))
            .check(regex("""conciliationTrack","label":"Conciliation Track","hidden":null,"value":"(.+?)","metadata""").saveAs("conciliationTrack"))
            .check(regex("""caseNotes","label":"Case Notes","hidden":null,"value":"(.+?)","metadata":false,"hint_text""").saveAs("caseNotes")))

        .pause(Environment.constantthinkTime)

        .exec(http("XUI_050_015_SelectAmendCaseDetailsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FamendCaseDetails%2FamendCaseDetails1")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_050_020_GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_3))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_060_005_AmendCaseDetailsPage1")
			.post(Environment.xuiUrl + "/data/case-types/Leeds/validate?pageId=amendCaseDetails1")
			.headers(XUIHeaders.ethos_headers_5)
			.body(StringBody("{\n  \"data\": {\n    \"receiptDate\": \"${receiptDate}\",\n    \"feeGroupReference\": \"${feeGroupReference}\",\n    \"clerkResponsible\": \"${clerkResponsible}\",\n    \"positionType\": \"${positionType}\",\n    \"fileLocation\": \"${fileLocation}\",\n    \"conciliationTrack\": \"${conciliationTrack}\",\n    \"caseType\": \"Single\",\n    \"multipleFlag\": \"No\"\n  },\n  \"event\": {\n    \"id\": \"amendCaseDetails\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"receiptDate\": \"${receiptDate}\",\n    \"feeGroupReference\": \"${feeGroupReference}\",\n    \"clerkResponsible\": \"${clerkResponsible}\",\n    \"positionType\": \"${positionType}\",\n    \"fileLocation\": \"${fileLocation}\",\n    \"conciliationTrack\": \"${conciliationTrack}\",\n    \"caseType\": \"Single\",\n    \"multipleFlag\": \"No\"\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

        .exec(http("XUI_060_010_AmendCaseDetailsPage1Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FamendCaseDetails%2FamendCaseDetails2")
			.headers(XUIHeaders.ethos_headers_0))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_070_005_AmendCaseDetailsPage2")
			.post(Environment.xuiUrl + "/data/case-types/Leeds/validate?pageId=amendCaseDetails2")
			.headers(XUIHeaders.ethos_headers_5)
			.body(StringBody("{\n  \"data\": {\n    \"caseNotes\": \"${caseNotes}\",\n    \"additionalCaseInfo\": {\n      \"additional_live_appeal\": \"Yes\",\n      \"additional_sensitive\": \"No\",\n      \"additional_ind_expert\": \"No\",\n      \"doNotPostpone\": \"No\"\n    }\n  },\n  \"event\": {\n    \"id\": \"amendCaseDetails\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"receiptDate\": \"${receiptDate}\",\n    \"feeGroupReference\": \"${feeGroupReference}\",\n    \"clerkResponsible\": \"${clerkResponsible}\",\n    \"positionType\": \"${positionType}\",\n    \"fileLocation\": \"${fileLocation}\",\n    \"conciliationTrack\": \"${conciliationTrack}\",\n    \"caseType\": \"Single\",\n    \"multipleFlag\": \"No\",\n    \"caseNotes\": \"${caseNotes} \",\n    \"additionalCaseInfo\": {\n      \"additional_live_appeal\": \"Yes\",\n      \"additional_sensitive\": \"No\",\n      \"additional_ind_expert\": \"No\",\n      \"doNotPostpone\": \"No\"\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

        .exec(http("XUI_060_010_AmendCaseDetailsPage1Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FamendCaseDetails%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_0))
            
        .exec(http("XUI_060_015_AmendCaseDetailsPage1GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_3))

        .pause(Environment.constantthinkTime)

        .exec(http("XUI_070_005_AmendCaseDetailsSubmit")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_9)
			.body(StringBody("{\n  \"data\": {\n    \"receiptDate\": \"${receiptDate}\",\n    \"feeGroupReference\": \"${feeGroupReference}\",\n    \"clerkResponsible\": \"${clerkResponsible}\",\n    \"positionType\": \"${positionType}\",\n    \"fileLocation\": \"${fileLocation}\",\n    \"conciliationTrack\": \"${conciliationTrack}\",\n    \"caseType\": \"Single\",\n    \"multipleReference\": null,\n    \"leadClaimant\": null,\n    \"subMultipleName\": null,\n    \"multipleFlag\": \"No\",\n    \"caseNotes\": \"${caseNotes}\",\n    \"additionalCaseInfo\": {\n      \"additional_live_appeal\": \"Yes\",\n      \"additional_sensitive\": \"No\",\n      \"additional_ind_expert\": \"No\",\n      \"doNotPostpone\": \"No\"\n    }\n  },\n  \"event\": {\n    \"id\": \"amendCaseDetails\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false\n}")))

        .exec(http("XUI_070_010_AmendCaseDetailsSubmitHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_070_015_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_11))

        .pause(Environment.constantthinkTime)

    val XUISingleUpdateJurisdictions = 

        exec(http("XUI_080_005_SelectAmendJurisdictionsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FaddAmendJurisdiction")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_080_010_SelectAmendJurisdictions")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/addAmendJurisdiction?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_4)
            .check(jsonPath("$.event_token").saveAs("eventToken")))

        .exec(http("XUI_080_015_SelectAmendJurisdictionsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FaddAmendJurisdiction%2FaddAmendJurisdiction1")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_080_020_GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_090_005_AmendJurisdictionsPage1Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FaddAmendJurisdiction%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_090_010_AmendJurisdictionsPage1")
			.post(Environment.xuiUrl + "/data/case-types/Leeds/validate?pageId=addAmendJurisdiction1")
			.headers(XUIHeaders.ethos_headers_5)
			.body(StringBody("{\n  \"data\": {\n    \"jurCodesCollection\": [\n      {\n        \"id\": \"e4886987-f07a-47ea-8c22-3e4091ffcabd\",\n        \"value\": {\n          \"juridictionCodesList\": \"DDA\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"65e9bcd5-067d-46f3-a7c2-4ae34819b214\",\n        \"value\": {\n          \"juridictionCodesList\": \"DRB\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"482b438a-aef5-44b1-b102-771f0d809413\",\n        \"value\": {\n          \"juridictionCodesList\": \"PID\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"988519d2-0c28-4097-b330-f5ace7255a83\",\n        \"value\": {\n          \"juridictionCodesList\": \"RRD\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"d48ca03c-f069-4bb4-bf39-62906d298765\",\n        \"value\": {\n          \"juridictionCodesList\": \"SXD\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"856ba701-6878-46be-a133-6cd0760f0e49\",\n        \"value\": {\n          \"juridictionCodesList\": \"UDL\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"a389b3c1-ac6c-4ead-a84e-0f17aab100ee\",\n        \"value\": {\n          \"juridictionCodesList\": \"CCP\",\n          \"judgmentOutcome\": \"Acas conciliated settlement\"\n        }\n      },\n      {\n        \"id\": null,\n        \"value\": {\n          \"juridictionCodesList\": \"FCT\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"addAmendJurisdiction\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"jurCodesCollection\": [\n      {\n        \"id\": \"e4886987-f07a-47ea-8c22-3e4091ffcabd\",\n        \"value\": {\n          \"juridictionCodesList\": \"DDA\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"65e9bcd5-067d-46f3-a7c2-4ae34819b214\",\n        \"value\": {\n          \"juridictionCodesList\": \"DRB\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"482b438a-aef5-44b1-b102-771f0d809413\",\n        \"value\": {\n          \"juridictionCodesList\": \"PID\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"988519d2-0c28-4097-b330-f5ace7255a83\",\n        \"value\": {\n          \"juridictionCodesList\": \"RRD\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"d48ca03c-f069-4bb4-bf39-62906d298765\",\n        \"value\": {\n          \"juridictionCodesList\": \"SXD\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"856ba701-6878-46be-a133-6cd0760f0e49\",\n        \"value\": {\n          \"juridictionCodesList\": \"UDL\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      },\n      {\n        \"id\": \"a389b3c1-ac6c-4ead-a84e-0f17aab100ee\",\n        \"value\": {\n          \"juridictionCodesList\": \"CCP\",\n          \"judgmentOutcome\": \"Acas conciliated settlement\"\n        }\n      },\n      {\n        \"id\": null,\n        \"value\": {\n          \"juridictionCodesList\": \"FCT\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\"\n        }\n      }\n    ]\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

        .exec(http("XUI_090_015_AmendJurisdictionsPage1GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

        .pause(Environment.constantthinkTime)

        .exec(http("XUI_100_005_AmendJurisdictionsSubmit")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_9)
			.body(StringBody("{\n  \"data\": {\n    \"jurCodesCollection\": [\n      {\n        \"id\": \"e4886987-f07a-47ea-8c22-3e4091ffcabd\",\n        \"value\": {\n          \"juridictionCodesList\": \"DDA\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\",\n          \"juridictionCodesSubList1\": null\n        }\n      },\n      {\n        \"id\": \"65e9bcd5-067d-46f3-a7c2-4ae34819b214\",\n        \"value\": {\n          \"juridictionCodesList\": \"DRB\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\",\n          \"juridictionCodesSubList1\": null\n        }\n      },\n      {\n        \"id\": \"482b438a-aef5-44b1-b102-771f0d809413\",\n        \"value\": {\n          \"juridictionCodesList\": \"PID\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\",\n          \"juridictionCodesSubList1\": null\n        }\n      },\n      {\n        \"id\": \"988519d2-0c28-4097-b330-f5ace7255a83\",\n        \"value\": {\n          \"juridictionCodesList\": \"RRD\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\",\n          \"juridictionCodesSubList1\": null\n        }\n      },\n      {\n        \"id\": \"d48ca03c-f069-4bb4-bf39-62906d298765\",\n        \"value\": {\n          \"juridictionCodesList\": \"SXD\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\",\n          \"juridictionCodesSubList1\": null\n        }\n      },\n      {\n        \"id\": \"856ba701-6878-46be-a133-6cd0760f0e49\",\n        \"value\": {\n          \"juridictionCodesList\": \"UDL\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\",\n          \"juridictionCodesSubList1\": null\n        }\n      },\n      {\n        \"id\": \"a389b3c1-ac6c-4ead-a84e-0f17aab100ee\",\n        \"value\": {\n          \"juridictionCodesList\": \"CCP\",\n          \"judgmentOutcome\": \"Acas conciliated settlement\",\n          \"juridictionCodesSubList1\": null\n        }\n      },\n      {\n        \"id\": \"f755d963-fb54-4c98-8a2a-e0b278c2a8b1\",\n        \"value\": {\n          \"juridictionCodesList\": \"FCT\",\n          \"judgmentOutcome\": \"Unsuccessful at hearing\",\n          \"juridictionCodesSubList1\": null\n        }\n      }\n    ]\n  },\n  \"event\": {\n    \"id\": \"addAmendJurisdiction\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false\n}")))

        .exec(http("XUI_100_010_AmendJurisdictionsSubmitHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_100_015_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_11))

        .pause(Environment.constantthinkTime)

    val XUISingleAdd25Respondents = 

        exec(http("XUI_110_005_SelectAmendRespondentsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FamendRespondentDetails")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_110_010_SelectAmendRespondents")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/amendRespondentDetails?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_4)
            .check(jsonPath("$.event_token").saveAs("eventToken")))

        .exec(http("XUI_110_015_SelectAmendRespondentsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FamendRespondentDetails%2FamendRespondentDetails1")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_110_020_SelectAmendRespondentsGetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

        .pause(Environment.constantthinkTime)

		.exec(http("XUI_120_005_AddRespondentDetailsPage1")
			.post(Environment.xuiUrl + "/data/case-types/Leeds/validate?pageId=amendRespondentDetails1")
			.headers(XUIHeaders.ethos_headers_7)
			.body(ElFileBody("XUI_25RespondentsAdd.json")))

        .exec(http("XUI_120_010_AddRespondentDetailsPage1Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FamendRespondentDetails%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_120_015_AddRespondentDetailsPage1GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

        .pause(Environment.constantthinkTime)

        .exec(http("XUI_130_005_AddRespondentDetailsSubmit")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_9)
			.body(ElFileBody("XUI_25RespondentsSubmit.json")))

        .exec(http("XUI_130_010_AddRespondentDetailsSubmitHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_130_015_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_11))

        .pause(Environment.constantthinkTime)

	val XUISingleList25Hearings = 

		exec(http("XUI_140_005_SelectAmendHearingHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FaddAmendHearing")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_140_010_SelectAmendHearing")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/addAmendHearing?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_4)
			.check(jsonPath("$.event_token").saveAs("eventToken")))

		.exec(http("XUI_140_015_SelectAmendHearingHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FaddAmendHearing%2FaddAmendHearing1")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_140_020_SelectAmendHearingGetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_150_005_AmendHearingPage1")
			.post(Environment.xuiUrl + "/data/case-types/Leeds/validate?pageId=addAmendHearing1")
			.headers(XUIHeaders.ethos_headers_5)
			.body(ElFileBody("XUI_25HearingsAdd.json")))

		.exec(http("XUI_150_005_AmendHearingPage1Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FaddAmendHearing%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_150_005_AmendHearingPage1GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_160_005_AmendHearingSubmit")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_9)
			.body(ElFileBody("XUI_25HearingsSubmit.json")))

		.exec(http("XUI_160_010_AmendHearingSubmitHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_160_015_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_11))

		.pause(Environment.constantthinkTime)

	val XUISingleAllocate25Hearings = 

		exec(http("XUI_170_005_SelectAllocateHearingsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallocateHearing")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_170_010_SelectAllocateHearings")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/allocateHearing?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_4)
			.check(jsonPath("$.event_token").saveAs("eventToken")))

		.exec(http("XUI_170_015_SelectAllocateHearingsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallocateHearing%2FallocateHearing1")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_170_020_SelectAllocateHearingsGetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

		.pause(Environment.constantthinkTime)
		
		.exec(http("XUI_180_005_AddHearings")
			.post(Environment.xuiUrl + "/data/case-types/Leeds/validate?pageId=allocateHearing1")
			.headers(XUIHeaders.ethos_headers_5)
			.body(ElFileBody("XUI_25AllocateHearingsAdd.json")))

		.exec(http("XUI_180_010_AddHearingsHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FallocateHearing%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_180_015_AddHearingsGetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_190_005_AddHearingsSubmit")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_9)
			.body(ElFileBody("XUI_25AllocateHearingsSubmit.json")))

		.exec(http("XUI_190_010_AddHearingsSubmitHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_0))

		.exec(http("XUI_190_015_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_11))

		.exec {
			session =>
			val fw = new BufferedWriter(new FileWriter("BurntData_Singles.csv", true))
			try {
				fw.write(session("singleName").as[String] + "\r\n")
			}
			finally fw.close()
			session
		}

		.pause(Environment.constantthinkTime)

    val XUISinglePreAcceptance =

        exec(http("XUI_PreAcceptance_010_005_PreAcceptanceHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FpreAcceptanceCase")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_PreAcceptance_010_010_PreAcceptance")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/preAcceptanceCase?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_2)
            .check(jsonPath("$.event_token").saveAs("eventToken")))

        .exec(http("XUI_PreAcceptance_010_015_PreAcceptanceHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FpreAcceptanceCase%2FpreAcceptanceCase1")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_PreAcceptance_010_020_PreAcceptanceGetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))
		
        .pause(Environment.constantthinkTime)

		.exec(http("XUI_PreAcceptance_020_005_PreAcceptancePage1")
			.post(Environment.xuiUrl + "/data/case-types/Leeds/validate?pageId=preAcceptanceCase1")
			.headers(XUIHeaders.ethos_headers_5)
			.body(StringBody("{\n  \"data\": {\n    \"preAcceptCase\": {\n      \"caseAccepted\": \"Yes\",\n      \"dateAccepted\": \"${receiptDate}\",\n      \"caseReferred\": null,\n      \"partRejected\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"preAcceptanceCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"preAcceptCase\": {\n      \"caseAccepted\": \"Yes\",\n      \"dateAccepted\": \"${receiptDate}\",\n      \"caseReferred\": null,\n      \"partRejected\": null\n    }\n  },\n  \"case_reference\": \"${caseId}\"\n}")))

        .exec(http("XUI_PreAcceptance_020_010_PreAcceptancePage1Healthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FpreAcceptanceCase%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_PreAcceptance_020_015_PreAcceptancePage1GetProfileData")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

        .pause(Environment.constantthinkTime)

        .exec(http("XUI_PreAcceptance_030_005_PreAcceptanceSubmit")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_9)
			.body(StringBody("{\n  \"data\": {\n    \"preAcceptCase\": {\n      \"caseAccepted\": \"Yes\",\n      \"dateAccepted\": \"${receiptDate}\",\n      \"rejectReason\": [],\n      \"caseReferred\": null,\n      \"caseJudge\": null,\n      \"caseEJReferredDate\": null,\n      \"preAcceptOutcome\": null,\n      \"caseEJReferredDateReturn\": null,\n      \"partRejected\": null\n    }\n  },\n  \"event\": {\n    \"id\": \"preAcceptanceCase\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false\n}")))

        .exec(http("XUI_PreAcceptance_030_010_PreAcceptanceSubmitHealthcheck")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_0))

        .exec(http("XUI_PreAcceptance_030_015_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_11))

        .pause(Environment.constantthinkTime)

	val XUILogout =

      	exec(http("XUI_SignOut")
           .get(Environment.xuiUrl + "/api/logout")
           .headers(XUIHeaders.headers_signout)
           .check(status.in(200, 304, 302)))

}