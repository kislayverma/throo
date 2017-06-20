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
package com.github.kislayverma.nexus.util;

import com.github.kislayverma.nexus.routes.ProxyRoute;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kislay Verma
 */
public class MiscUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscUtil.class);

    public static HttpRequest setRequestHeaders(HttpRequest request, RoutingContext routingContext, ProxyRoute proxyRoute) {
        routingContext.request().headers().entries().forEach((entry) -> {
            LOGGER.debug("Setting request header : " + entry.getKey() + " : " + entry.getValue());
            request.putHeader(entry.getKey(), entry.getValue());
        });
        request.putHeader("Host", proxyRoute.getTargetBaseUrl());

        return request;
    }

    public static void setResponseHeaders(HttpResponse response, RoutingContext routingContext) {
        response.headers().entries().forEach((entry) -> {
            LOGGER.debug("Setting response header : " + entry.getKey() + " : " + entry.getValue());
            routingContext.response().putHeader(entry.getKey(), entry.getValue());
        });
    }

}
