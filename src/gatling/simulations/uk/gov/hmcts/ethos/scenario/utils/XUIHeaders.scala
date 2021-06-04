package uk.gov.hmcts.ethos.scenario.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object XUIHeaders {

	val headers_login_submit = Map(
			"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
			"Accept-Encoding" -> "gzip, deflate, br",
			"Accept-Language" -> "en-US,en;q=0.9",
			"Origin" -> Environment.idamURL,
			"Sec-Fetch-Mode" -> "navigate",
			"Sec-Fetch-Site" -> "same-origin",
			"Sec-Fetch-User" -> "?1",
			"Upgrade-Insecure-Requests" -> "1")

	val headers_0 = Map(
			"accept" -> "application/json, text/plain, */*",
			"accept-encoding" -> "gzip, deflate, br",
			"accept-language" -> "en-US,en;q=0.9",
			"sec-fetch-mode" -> "cors",
			"sec-fetch-site" -> "same-origin")

	val headers_1 = Map(
			"Pragma" -> "no-cache",
			"Sec-Fetch-Dest" -> "empty",
			"Sec-Fetch-Mode" -> "cors",
			"Sec-Fetch-Site" -> "same-origin")

	val headers_4 = Map(
			"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
			"Pragma" -> "no-cache",
			"Sec-Fetch-Dest" -> "document",
			"Sec-Fetch-Mode" -> "navigate",
			"Sec-Fetch-Site" -> "same-origin",
			"Upgrade-Insecure-Requests" -> "1")

	val headers_38 = Map(
			"accept" -> "application/json, text/plain, */*",
			"accept-encoding" -> "gzip, deflate, br",
			"accept-language" -> "en-US,en;q=0.9",
			"sec-fetch-dest" -> "empty",
			"sec-fetch-mode" -> "cors",
			"sec-fetch-site" -> "same-origin",
			"x-dtpc" -> "3$38732415_350h4vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0")

	val headers_access_read = Map(
			"accept" -> "application/json",
			"accept-encoding" -> "gzip, deflate, br",
			"accept-language" -> "en-US,en;q=0.9",
			"content-type" -> "application/json",
			"sec-fetch-dest" -> "empty",
			"sec-fetch-mode" -> "cors",
			"sec-fetch-site" -> "same-origin",
			"x-dtpc" -> "3$38734236_77h15vDTRMSASFKPLKDRFKMHCCHMMCARPGMHGD-0",
			"x-dtreferer" -> Environment.xuiTCUrl)

	val ethos_headers_0 = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json",
		"Origin" -> Environment.xuiUrl ,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}")

	val ethos_headers_1 = Map(
		"Pragma" -> "no-cache",
		"Accept" -> "application/json, text/plain, */*",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

	val ethos_headers_2 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
		"Content-Type" -> "application/json",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_3 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_4 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_5 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Origin" -> Environment.xuiUrl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_6 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_7 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Origin" -> Environment.xuiUrl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_9 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Origin" -> Environment.xuiUrl,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_11 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json",
		"Content-Type" -> "application/json",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_14 = Map(
		"Accept" -> "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8",
		"Content-Type" -> "application/json",
		"Origin" -> Environment.xuiUrl ,
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin",
		"X-XSRF-TOKEN" -> "${xsrfToken}",
		"experimental" -> "true")

	val ethos_headers_15 = Map(
		"cache-control" -> "no-cache",
		"dnt" -> "1",
		"pragma" -> "no-cache",
		"sec-ch-ua" -> """ Not A;Brand";v="99", "Chromium";v="90", "Google Chrome";v="90""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-origin")

	val headers_signout = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"accept-encoding" -> "gzip, deflate, br",
		"accept-language" -> "en-US,en;q=0.9",
		"sec-fetch-mode" -> "navigate",
		"sec-fetch-site" -> "same-origin",
		"sec-fetch-user" -> "?1",
		"upgrade-insecure-requests" -> "1")
}