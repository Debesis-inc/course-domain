package com.course_domain.course_domain.integrationtests.coursedomain.support;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public final class WireMockStubs {

    private WireMockStubs() {
    }

    public static void reset(WireMockServer wireMockServer) {
        wireMockServer.resetAll();
    }

    public static void stubHealthCheck(WireMockServer wireMockServer) {
        wireMockServer.stubFor(get(urlPathEqualTo("/health"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"UP\"}")));
    }
}
