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
package com.github.kislayverma.throo.verticle;

import com.github.kislayverma.throo.routes.BaseRouteConfigurer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kislay Verma
 */
public class HttpProxyVerticle extends AbstractVerticle {

    private static BaseRouteConfigurer routeConfigurer;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyVerticle.class);

    @Override
    public void start(Future<Void> fut) {
        LOGGER.info("Starting proxy verticle...");

        // Set up all the routes and their handlers
        Router router = routeConfigurer.setRouteHandlers(vertx);
        HttpServerOptions options = new HttpServerOptions().setLogActivity(true);
        // Create the HTTP server
        vertx.createHttpServer(options)
             .requestHandler(router::accept)
             .listen(
                    // Retrieve the port from the configuration,
                    // default to 8080.
                    config().getInteger("http.port", 8080),
                    result -> {
                        if (result.succeeded()) {
                            fut.complete();
                        } else {
                            fut.fail(result.cause());
                        }
                    }
            );
    }

    public void setRouteConfigurer(BaseRouteConfigurer aRouteConfigurer) {
        routeConfigurer = aRouteConfigurer;
    }
}
