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
package com.github.kislayverma.throo.bootstrap;

import com.github.kislayverma.throo.verticle.HttpProxyVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the class that launches the application.
 * Lifted from https://github.com/yasha-tymoshenko/vertx-rest-crud/blob/master/src/main/java/com/tymoshenko/MainApp.java
 * 
 * @author Kislay Verma
 */
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        ApplicationConfig config = new ApplicationConfig("conf/application.properties");
        String springFilesStr = config.getProperty("spring.configs");
        if (springFilesStr != null) {
            String[] springFilesArr = springFilesStr.split(",");
            if (springFilesArr.length > 0) {
            LOGGER.info("Loading spring config files : " + springFilesStr);
                SpringContextLoader contextLoader = new SpringContextLoader(springFilesArr);
            }
        }

        int verticleInstances = 1;
        String configuredVerticleInstances = config.getProperty("application.verticleInstances");
        if (configuredVerticleInstances != null && !configuredVerticleInstances.isEmpty()) {
            verticleInstances = Integer.parseInt(configuredVerticleInstances);
        }
        LOGGER.info("Number of proxy verticles to instantiate : " + verticleInstances);

        int workerPoolSize = 25;
        String configuredWorkerPoolSize = config.getProperty("application.workerPoolSize");
        if (configuredWorkerPoolSize != null && !configuredWorkerPoolSize.isEmpty()) {
            workerPoolSize = Integer.parseInt(configuredWorkerPoolSize);
        }
        LOGGER.info("Worker pool size : " + workerPoolSize);

        String workerPoolName = "proxyWorkerPool";
        String configuredWorkerPoolName = config.getProperty("application.workerPoolName");
        if (configuredWorkerPoolName != null && !configuredWorkerPoolName.isEmpty()) {
            workerPoolName = configuredWorkerPoolName;
        }
        LOGGER.info("Worker pool name : " + workerPoolName);

        final Vertx vertx = Vertx.vertx();

        LOGGER.info("Deploying verticles...");
        vertx.deployVerticle(HttpProxyVerticle.class.getName(),new DeploymentOptions()
            .setInstances(verticleInstances).setWorkerPoolSize(workerPoolSize).setWorkerPoolName(workerPoolName));
    }
}
