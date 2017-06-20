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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Kislay Verma
 */
public class ApplicationConfig {

    private final Properties prop;

    public ApplicationConfig(String propFileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop = new Properties();
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
        }
    }

    public String getProperty(String propertyName) {
        return prop.getProperty(propertyName);
    }
}
