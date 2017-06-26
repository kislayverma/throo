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
package com.github.kislayverma.nexus.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Kislay Verma
 */
public class SpringContextLoader {
    private final AnnotationConfigApplicationContext ctx;
    private final ApplicationContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContextLoader.class);

    public SpringContextLoader(String[] springContextFilePaths) {
        LOGGER.info("Loading Spring configs...");
        ctx = new AnnotationConfigApplicationContext();
        ctx.refresh();
        ctx.registerShutdownHook();

        context = new ClassPathXmlApplicationContext(springContextFilePaths);
        for (String beanName : context.getBeanDefinitionNames()) {
            LOGGER.debug("Loading spring bean : " + beanName);
            ctx.getBeanFactory().registerSingleton(beanName, context.getBean(beanName));
        }

        LOGGER.info("Finished loading spring beans");
    }

    public <A> A getBean(Class<A> clazz) {
        return ctx.getBean(clazz);
    }
}
