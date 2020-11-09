package uk.gov.hmcts.ethos.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ethos.scenario.utils._

object XUI {

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

        exec(http("XUI_020_005_SignIn")
            .post(Environment.idamURL + "/login?response_type=code&redirect_uri=" + Environment.xuiUrl + "%2Foauth2%2Fcallback&scope=profile%20openid%20roles%20manage-user%20create-user&state=${state}&client_id=xuiwebapp")
            .formParam("username", "ccdloadtest4501@gmail.com")
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

        .exec(http("XUI_020_020_AcceptT&CAccessJurisdictions${count}")
            .get(Environment.xuiUrl + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
            .headers(XUIHeaders.headers_access_read)
            .check(status.in(200, 304, 302)))

        .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("xsrfToken")))

        .pause(Environment.constantthinkTime)

    val XUISearchAndOpenCase = 

        exec(http("XUI_030_SearchForMultiple")
			.post(Environment.xuiUrl + "/data/internal/searchCases?ctid=Leeds_Multiple&use_case=WORKBASKET&view=WORKBASKET&page=1&case.multipleName=PerfTest-Multiple-0005")
			.headers(XUIHeaders.ethos_headers_0)
            .body(StringBody("{\n  \"size\": 25\n}"))
            .check(regex("""case_id":"(.*)","supplementary_data"""").saveAs("caseId")))

		.pause(Environment.constantthinkTime)

		.exec(http("XUI_040_005_OpenCase")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_040_010_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_2))
            
		.pause(Environment.constantthinkTime)

    val BatchUpdateMultiple = 

        exec(http("XUI_040_015_OpenCase")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_040_020_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}/event-triggers/batchUpdateCases?ignore-warning=false")
			.headers(XUIHeaders.ethos_headers_4)
            .check(jsonPath("$.event_token").saveAs("eventToken")))

        .exec(http("XUI_040_025_OpenCase")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2FbatchUpdateCases1")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("XUI_040_030_OpenCase")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

        .exec(http("request_7")
			.post(Environment.xuiUrl + "/data/case-types/Leeds_Multiple/validate?pageId=batchUpdateCases1")
			.headers(XUIHeaders.ethos_headers_7)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": \"All\",\n    \"flag2\": \"All\",\n    \"flag3\": \"All\",\n    \"flag4\": \"All\"\n  },\n  \"case_reference\": \"1604585676546623\"\n}")))

        .exec(http("request_8")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2FbatchUpdateCases2")
			.headers(XUIHeaders.ethos_headers_1))

		.exec(http("request_9")
			.post(Environment.xuiUrl + "/data/case-types/Leeds_Multiple/validate?pageId=batchUpdateCases2")
			.headers(XUIHeaders.ethos_headers_7)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateCase\": \"Perf-20201031/0001\"\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"batchUpdateCase\": \"Perf-20201031/0001\"\n  },\n  \"case_reference\": \"1604585676546623\"\n}")))

        .exec(http("request_10")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2FbatchUpdateCases5")
			.headers(XUIHeaders.ethos_headers_1))

		.pause(Environment.constantthinkTime)

		.exec(http("request_11")
			.post(Environment.xuiUrl + "/data/case-types/Leeds_Multiple/validate?pageId=batchUpdateCases5")
			.headers(XUIHeaders.ethos_headers_7)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateClaimantRep\": {\n      \"value\": {\n        \"code\": \"None\",\n        \"label\": \"None\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        }\n      ]\n    },\n    \"batchUpdateJurisdiction\": {\n      \"value\": {\n        \"code\": \"DRB\",\n        \"label\": \"DRB\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"DDA\",\n          \"label\": \"DDA\"\n        },\n        {\n          \"code\": \"DRB\",\n          \"label\": \"DRB\"\n        },\n        {\n          \"code\": \"PID\",\n          \"label\": \"PID\"\n        },\n        {\n          \"code\": \"RRD\",\n          \"label\": \"RRD\"\n        },\n        {\n          \"code\": \"SXD\",\n          \"label\": \"SXD\"\n        },\n        {\n          \"code\": \"UDL\",\n          \"label\": \"UDL\"\n        }\n      ]\n    },\n    \"batchUpdateRespondent\": {\n      \"value\": {\n        \"code\": \"Ptr Atnsn\",\n        \"label\": \"Ptr Atnsn\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"freds respondent\",\n          \"label\": \"freds respondent\"\n        },\n        {\n          \"code\": \"Kzt Fnn\",\n          \"label\": \"Kzt Fnn\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Jn Rtt\",\n          \"label\": \"Jn Rtt\"\n        },\n        {\n          \"code\": \"Alxzndr Czdwt\",\n          \"label\": \"Alxzndr Czdwt\"\n        },\n        {\n          \"code\": \"Nl Ld\",\n          \"label\": \"Nl Ld\"\n        },\n        {\n          \"code\": \"Crstpr Wnspzr\",\n          \"label\": \"Crstpr Wnspzr\"\n        },\n        {\n          \"code\": \"Ptr Atnsn\",\n          \"label\": \"Ptr Atnsn\"\n        },\n        {\n          \"code\": \"Annz Crzwly\",\n          \"label\": \"Annz Crzwly\"\n        },\n        {\n          \"code\": \"Elzzxt Cunnnzm\",\n          \"label\": \"Elzzxt Cunnnzm\"\n        },\n        {\n          \"code\": \"Andrw Wnwrt\",\n          \"label\": \"Andrw Wnwrt\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Aysz Kzn\",\n          \"label\": \"Aysz Kzn\"\n        },\n        {\n          \"code\": \"Mtzl Rds\",\n          \"label\": \"Mtzl Rds\"\n        },\n        {\n          \"code\": \"bob rob\",\n          \"label\": \"bob rob\"\n        },\n        {\n          \"code\": \"Stpn Frsyt\",\n          \"label\": \"Stpn Frsyt\"\n        },\n        {\n          \"code\": \"k rita\",\n          \"label\": \"k rita\"\n        },\n        {\n          \"code\": \"Tn Bvll\",\n          \"label\": \"Tn Bvll\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"batchUpdateCase\": \"Perf-20201031/0001\",\n    \"batchUpdateClaimantRep\": \"None\",\n    \"batchUpdateJurisdiction\": \"DRB\",\n    \"batchUpdateRespondent\": \"Ptr Atnsn\"\n  },\n  \"case_reference\": \"1604585676546623\"\n}")))

        .exec(http("request_12")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}%2Ftrigger%2FbatchUpdateCases%2Fsubmit")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("request_13")
			.get(Environment.xuiUrl + "/data/internal/profile")
			.headers(XUIHeaders.ethos_headers_6))

		.pause(Environment.constantthinkTime)

		.exec(http("request_14")
			.post(Environment.xuiUrl + "/data/cases/${caseId}/events")
			.headers(XUIHeaders.ethos_headers_14)
			.body(StringBody("{\n  \"data\": {\n    \"batchUpdateType\": \"batchUpdateType3\",\n    \"flag1\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag2\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag3\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"flag4\": {\n      \"value\": {\n        \"code\": \"All\",\n        \"label\": \"All\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"All\",\n          \"label\": \"All\"\n        }\n      ]\n    },\n    \"batchUpdateCase\": \"Perf-20201031/0001\",\n    \"batchUpdateClaimantRep\": {\n      \"value\": {\n        \"code\": \"None\",\n        \"label\": \"None\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        }\n      ]\n    },\n    \"batchUpdateJurisdiction\": {\n      \"value\": {\n        \"code\": \"DRB\",\n        \"label\": \"DRB\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"DDA\",\n          \"label\": \"DDA\"\n        },\n        {\n          \"code\": \"DRB\",\n          \"label\": \"DRB\"\n        },\n        {\n          \"code\": \"PID\",\n          \"label\": \"PID\"\n        },\n        {\n          \"code\": \"RRD\",\n          \"label\": \"RRD\"\n        },\n        {\n          \"code\": \"SXD\",\n          \"label\": \"SXD\"\n        },\n        {\n          \"code\": \"UDL\",\n          \"label\": \"UDL\"\n        }\n      ]\n    },\n    \"batchUpdateRespondent\": {\n      \"value\": {\n        \"code\": \"Ptr Atnsn\",\n        \"label\": \"Ptr Atnsn\"\n      },\n      \"list_items\": [\n        {\n          \"code\": \"None\",\n          \"label\": \"None\"\n        },\n        {\n          \"code\": \"freds respondent\",\n          \"label\": \"freds respondent\"\n        },\n        {\n          \"code\": \"Kzt Fnn\",\n          \"label\": \"Kzt Fnn\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Jn Rtt\",\n          \"label\": \"Jn Rtt\"\n        },\n        {\n          \"code\": \"Alxzndr Czdwt\",\n          \"label\": \"Alxzndr Czdwt\"\n        },\n        {\n          \"code\": \"Nl Ld\",\n          \"label\": \"Nl Ld\"\n        },\n        {\n          \"code\": \"Crstpr Wnspzr\",\n          \"label\": \"Crstpr Wnspzr\"\n        },\n        {\n          \"code\": \"Ptr Atnsn\",\n          \"label\": \"Ptr Atnsn\"\n        },\n        {\n          \"code\": \"Annz Crzwly\",\n          \"label\": \"Annz Crzwly\"\n        },\n        {\n          \"code\": \"Elzzxt Cunnnzm\",\n          \"label\": \"Elzzxt Cunnnzm\"\n        },\n        {\n          \"code\": \"Andrw Wnwrt\",\n          \"label\": \"Andrw Wnwrt\"\n        },\n        {\n          \"code\": \"Gmmz Cpr\",\n          \"label\": \"Gmmz Cpr\"\n        },\n        {\n          \"code\": \"Aysz Kzn\",\n          \"label\": \"Aysz Kzn\"\n        },\n        {\n          \"code\": \"Mtzl Rds\",\n          \"label\": \"Mtzl Rds\"\n        },\n        {\n          \"code\": \"bob rob\",\n          \"label\": \"bob rob\"\n        },\n        {\n          \"code\": \"Stpn Frsyt\",\n          \"label\": \"Stpn Frsyt\"\n        },\n        {\n          \"code\": \"k rita\",\n          \"label\": \"k rita\"\n        },\n        {\n          \"code\": \"Tn Bvll\",\n          \"label\": \"Tn Bvll\"\n        }\n      ]\n    }\n  },\n  \"event\": {\n    \"id\": \"batchUpdateCases\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false\n}")))

        .exec(http("request_15")
			.get(Environment.xuiUrl + "/api/healthCheck?path=%2Fcases%2Fcase-details%2F${caseId}")
			.headers(XUIHeaders.ethos_headers_1))

        .exec(http("request_16")
			.get(Environment.xuiUrl + "/data/internal/cases/${caseId}")
			.headers(XUIHeaders.ethos_headers_2))
}