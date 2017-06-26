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

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;

/**
 * This class gets and loads all the proxy routes.
 * @author Kislay Verma
 */
public class ProxyRouteConfigurer extends BaseRouteConfigurer {

    private static ProxyRouteFactory proxyRouteFactory;

    @Override
    public Router setRouteHandlers(Vertx vertx) {
        LOGGER.info("Configuring proxy routes and registering handlers...");

        Router router = Router.router(vertx);

        // Enable response time handlers fr all requests and body handler for all POST and PUT requests
        router.route().handler(ResponseTimeHandler.create());
        router.route(HttpMethod.POST, "/*").handler(BodyHandler.create());
        router.route(HttpMethod.PUT, "/*").handler(BodyHandler.create());

        // Load all the proxy routes
        for (ProxyRoute proxyRoute : proxyRouteFactory.getAllRoutes()) {
            LOGGER.info("Configuring route : [" + proxyRoute.getHttpMethod() + "] " + proxyRoute.getRoute());
            router = proxyRoute.configure(vertx, router);
        }

        return router;
    }

    public void setProxyRouteFactory(ProxyRouteFactory aProxyRouteFactory) {
        proxyRouteFactory = aProxyRouteFactory;
    }
}
