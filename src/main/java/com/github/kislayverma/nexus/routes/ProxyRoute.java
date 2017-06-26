/*
 * Copyright 2017 Kislay Verma.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kislayverma.nexus.routes;

import com.github.kislayverma.nexus.util.MiscUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kislay Verma
 */
public class ProxyRoute {
    private String route;
    private String targetBaseUrl;
    private String httpMethod;
    private Long timeout;
    private Integer port;
    private WebClient client;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final String START_TIME_KEY = "proxyHandlerStartTime";

    public Router configure(Vertx vertx, Router router) {
        WebClientOptions clientOptions = new WebClientOptions().setLogActivity(true).setMaxPoolSize(100);
        this.client = WebClient.create(vertx, clientOptions);

        switch (httpMethod.toUpperCase()) {
            case "GET":
                router.get(route).handler(this::doProxyGet);
                break;
            case "POST":
                router.post(route).handler(this::doProxyPost);
                break;
            case "PUT":
                router.put(route).handler(this::doProxyPut);
                break;
            default:
                throw new RuntimeException("Unsupported HTTP method on proxy route " + route);
        }

        return router;
    }

    public void doProxyGet(RoutingContext routingContext) {
        LOGGER.debug("Matched route " + route);

        long timeoutToUse = this.timeout == null ? 1000L : this.timeout;
        int portToUse = (this.port == null) ? 80 : this.port;

        routingContext.put(START_TIME_KEY, new Date().getTime());
        // Blind passthrough - only change the host and port part of the incoming request.
        // Sending the request URI will send both the path and the query params onwards
        HttpRequest request = client.get(portToUse, targetBaseUrl, routingContext.request().uri()).timeout(timeoutToUse);
        request = MiscUtil.setRequestHeaders(request, routingContext, this);

        request.send(r -> {
            AsyncResult<HttpResponse<Buffer>> ar = (AsyncResult) r;
            handleResponse(routingContext, ar);
        });
    }

    private void doProxyPost(RoutingContext routingContext) {
        LOGGER.debug("Matched route " + route);

        long timeoutToUse = this.timeout == null ? 1000L : this.timeout;
        int portToUse = (this.port == null) ? 80 : this.port;

        routingContext.put(START_TIME_KEY, new Date().getTime());
        // Blind passthrough - only change the host and port part of the incoming request.
        // Sending the request URI will send both the path and the query params onwards
        HttpRequest request = client.post(portToUse, targetBaseUrl, routingContext.request().uri()).timeout(timeoutToUse);
        request = MiscUtil.setRequestHeaders(request, routingContext, this);
        LOGGER.debug("Request body : " + routingContext.getBody());
        request.sendBuffer(routingContext.getBody(), r -> {
            AsyncResult<HttpResponse<Buffer>> ar = (AsyncResult) r;
            handleResponse(routingContext, ar);
        });
    }

    private void doProxyPut(RoutingContext routingContext) {
        LOGGER.debug("Matched route " + route);

        long timeoutToUse = this.timeout == null ? 1000L : this.timeout;
        int portToUse = (this.port == null) ? 80 : this.port;

        routingContext.put(START_TIME_KEY, new Date().getTime());
        // Blind passthrough - only change the host and port part of the incoming request.
        // Sending the request URI will send both the path and the query params onwards
        HttpRequest request = client.put(portToUse, targetBaseUrl, routingContext.request().uri()).timeout(timeoutToUse);
        request = MiscUtil.setRequestHeaders(request, routingContext, this);
        LOGGER.debug("Request body : " + routingContext.getBody());
        request.sendBuffer(routingContext.getBody(), r -> {
            AsyncResult<HttpResponse<Buffer>> ar = (AsyncResult) r;
            handleResponse(routingContext, ar);
        });
    }

    private void handleResponse(RoutingContext routingContext, AsyncResult<HttpResponse<Buffer>> ar) {
        if (ar.succeeded()) {
            HttpResponse<Buffer> response = ar.result();
            long timeTaken = (new Date().getTime()) - ((Long)routingContext.get(START_TIME_KEY));
            LOGGER.info("Request : " + (targetBaseUrl + routingContext.request().uri()) + 
                "\tResponse status code : " + response.statusCode() +
                "\tResponse time : " + timeTaken);
            LOGGER.debug("Response body : " + response.bodyAsString());

            MiscUtil.setResponseHeaders(response, routingContext);
            routingContext.response().setStatusCode(response.statusCode());
            routingContext.response().setStatusMessage(response.statusMessage());

            routingContext.response().end(response.bodyAsBuffer());
        } else {
            routingContext.fail(ar.cause());
        }
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTargetBaseUrl() {
        return targetBaseUrl;
    }

    public void setTargetBaseUrl(String targetBaseUrl) {
        this.targetBaseUrl = targetBaseUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
