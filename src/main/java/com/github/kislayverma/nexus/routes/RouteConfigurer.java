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
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;

/**
 *
 * @author Kislay Verma
 */
public class RouteConfigurer extends BaseRouteConfigurer {

    private static ProxyRouteFactory proxyRouteFactory;
    private WebClient client;

    @Override
    public Router setRouteHandlers(Vertx vertx) {
        LOGGER.error("Configuring proxy routes and registering handlers...");

        this.vertx = vertx;
        Router router = Router.router(vertx);
        WebClientOptions clientOptions = new WebClientOptions().setLogActivity(true).setMaxPoolSize(1000);
        client = WebClient.create(vertx, clientOptions);

        // Enable body handlers for all POST and PUT requests
        router.route(HttpMethod.POST, "/*").handler(BodyHandler.create());
        router.route(HttpMethod.PUT, "/*").handler(BodyHandler.create());

        // Load all the proxy routes
        proxyRouteFactory.getAllRoutes().forEach((proxyRoute) -> {
            LOGGER.error("Configuring route : [" + proxyRoute.getHttpMethod() + "] " + proxyRoute.getRoute());
            configureProxyRoute(proxyRoute, router);
        });

        return router;
    }

    private void configureProxyRoute(ProxyRoute proxyRoute, Router router) {
        switch (proxyRoute.getHttpMethod().toUpperCase()) {
            case "GET":
                router.get(proxyRoute.getRoute()).handler(this::doProxyGet);
                break;
            case "POST":
                router.post(proxyRoute.getRoute()).handler(this::doProxyPost);
                break;
            case "PUT":
                router.put(proxyRoute.getRoute()).handler(this::doProxyPut);
                break;
            default:
                throw new RuntimeException("Unsupported HTTP method on proxy route " + proxyRoute.toString());
        }
    }

    private void doProxyGet(RoutingContext routingContext) {
        String path = routingContext.request().path();
        for (String pathParam : routingContext.pathParams().values()) {
            path = path.replaceFirst("/" + pathParam, "");
        }

        ProxyRoute proxyRoute = proxyRouteFactory.getBean(path);
        long timeout = proxyRoute.getTimeout() == null ? 1000L : proxyRoute.getTimeout();
        int port = proxyRoute.getPort() == null ? 80 : proxyRoute.getPort();

        // Blind passthrough - only change the host and port part of the incoming request.
        // Sending the request URI will send both the path and the query params onwards
        LOGGER.error("CAlling : " + proxyRoute.getTargetBaseUrl() + "---" + routingContext.request().uri());
        HttpRequest request = client.get(port, proxyRoute.getTargetBaseUrl(), routingContext.request().uri()).timeout(timeout);
        request = MiscUtil.setRequestHeaders(request, routingContext, proxyRoute);

        request.send(r -> {
            AsyncResult<HttpResponse<Buffer>> ar = (AsyncResult) r;
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                LOGGER.error("Response status code : " + response.statusCode());
                LOGGER.debug("Reponse body : " + response.bodyAsString());

                MiscUtil.setResponseHeaders(response, routingContext);
                routingContext.response().setStatusCode(response.statusCode());
                routingContext.response().setStatusMessage(response.statusMessage());

                routingContext.response().end(response.bodyAsBuffer());
            } else {
                routingContext.fail(ar.cause());
            }
        });
    }

    private void doProxyPost(RoutingContext routingContext) {
        String path = routingContext.request().path();
        for (String pathParam : routingContext.pathParams().values()) {
            path = path.replaceFirst("/" + pathParam, "");
        }

        ProxyRoute proxyRoute = proxyRouteFactory.getBean(path);
        long timeout = proxyRoute.getTimeout() == null ? 1000L : proxyRoute.getTimeout();
        int port = proxyRoute.getPort() == null ? 80 : proxyRoute.getPort();

        // Blind passthrough - only change the host and port part of the incoming request.
        // Sending the request URI will send both the path and the query params onwards
        HttpRequest request = client.post(port, proxyRoute.getTargetBaseUrl(), routingContext.request().uri()).timeout(timeout);
        request = MiscUtil.setRequestHeaders(request, routingContext, proxyRoute);
        LOGGER.error("Request body : " + routingContext.getBody());
        request.sendBuffer(routingContext.getBody(), r -> {
            AsyncResult<HttpResponse<Buffer>> ar = (AsyncResult) r;
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                LOGGER.error("Response status code : " + response.statusCode());
                LOGGER.error("Reponse body : " + response.bodyAsString());

                MiscUtil.setResponseHeaders(response, routingContext);
                routingContext.response().setStatusCode(response.statusCode());
                routingContext.response().setStatusMessage(response.statusMessage());

                routingContext.response().end(response.bodyAsBuffer());
            } else {
                routingContext.fail(ar.cause());
            }
        });
    }

    private void doProxyPut(RoutingContext routingContext) {
        String path = routingContext.request().path();
        for (String pathParam : routingContext.pathParams().values()) {
            path = path.replaceFirst("/" + pathParam, "");
        }

        ProxyRoute proxyRoute = proxyRouteFactory.getBean(path);
        long timeout = proxyRoute.getTimeout() == null ? 1000L : proxyRoute.getTimeout();
        int port = proxyRoute.getPort() == null ? 80 : proxyRoute.getPort();

        // Blind passthrough - only change the host and port part of the incoming request.
        // Sending the request URI will send both the path and the query params onwards
        HttpRequest request = client.put(port, proxyRoute.getTargetBaseUrl(), routingContext.request().uri()).timeout(timeout);
        request = MiscUtil.setRequestHeaders(request, routingContext, proxyRoute);
        LOGGER.error("Request body : " + routingContext.getBody());
        request.sendBuffer(routingContext.getBody(), r -> {
            AsyncResult<HttpResponse<Buffer>> ar = (AsyncResult) r;
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                LOGGER.error("Response status code : " + response.statusCode());
                LOGGER.error("Reponse body : " + response.bodyAsString());

                MiscUtil.setResponseHeaders(response, routingContext);
                routingContext.response().setStatusCode(response.statusCode());
                routingContext.response().setStatusMessage(response.statusMessage());

                routingContext.response().end(response.bodyAsBuffer());
            } else {
                routingContext.fail(ar.cause());
            }
        });
    }

    public void setProxyRouteFactory(ProxyRouteFactory aProxyRouteFactory) {
        proxyRouteFactory = aProxyRouteFactory;
    }
}
