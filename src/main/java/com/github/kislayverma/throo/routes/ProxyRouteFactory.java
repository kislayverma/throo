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
package com.github.kislayverma.throo.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Kislay Verma
 */
public class ProxyRouteFactory implements ApplicationContextAware {

    private ApplicationContext context;

    private Map<String, ProxyRoute> keyToBeanMap;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyRouteFactory.class);
    public void init() {
        Map<String, ProxyRoute> h = new HashMap<>();

        for (ProxyRoute bean : getBeans()) {
            try {
                LOGGER.info("Adding bean with key :" + bean.getRoute() + "-- class name :" + bean.getClass());
                h.put(bean.getRoute(), bean);
            } catch (Exception e) {
                LOGGER.debug("Error initializing ProxyRouteFactory", e);
            }
        }
        synchronized (this.getClass()) {
            keyToBeanMap = Collections.unmodifiableMap(h);
        }
    }

    public ProxyRoute getBean(String key) {
        ProxyRoute bean = keyToBeanMap.get(key);
        if (bean == null) {
            LOGGER.error("No bean with key " + key);
        }

        return bean;
    }

    public List<ProxyRoute> getAllRoutes() {
        return this.getBeans();
    }

    private List<ProxyRoute> getBeans() {
        return new ArrayList<>(context.getBeansOfType(ProxyRoute.class, true, true).values());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
