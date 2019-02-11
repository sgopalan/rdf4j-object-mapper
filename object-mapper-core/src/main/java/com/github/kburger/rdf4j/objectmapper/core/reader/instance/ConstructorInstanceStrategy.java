/**
 * Copyright © 2019 https://github.com/kburger (burger.github@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kburger.rdf4j.objectmapper.core.reader.instance;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;

public class ConstructorInstanceStrategy implements InstanceStrategy {
    private Constructor<?> constructor;
    private String[] properties;
    private Object[] arguments;
    
    @Override
    public <T> void initialize(Class<T> clazz) {
        constructor = Arrays.stream(clazz.getConstructors())
                .filter(ctor -> ctor.isAnnotationPresent(ConstructorProperties.class))
                .findFirst()
                .orElseThrow(() -> new ObjectReaderException("Could not find an annotated constructor"));
        
        var constructorProperties = constructor.getAnnotation(ConstructorProperties.class);
        
        if (constructorProperties.value().length != constructor.getParameterCount()) {
            throw new ObjectReaderException("");
        }
        
        properties = constructorProperties.value();
        arguments = new Object[constructor.getParameterCount()];
    }

    @Override
    public <T extends Annotation> void addProperty(PropertyAnalysis<T> property, Object value) {
        var name = property.getName().orElseThrow(() -> new ObjectReaderException("Could not find property name"));
        
        for (int i = 0; i < properties.length; i++) {
            if (properties[i].equals(name)) {
                arguments[i] = value;
                return;
            }
        }
        
        throw new ObjectReaderException("Could not add unmatched property");
    }

    @Override
    public Object build() {
        try {
            return constructor.newInstance(arguments);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new ObjectReaderException("Could not invoke constructor", e);
        }
    }
    
    public static class Factory implements InstanceStrategy.Factory {
        @Override
        public <T> boolean supports(Class<T> clazz) {
            return Arrays.stream(clazz.getConstructors())
                    .anyMatch(ctor -> ctor.isAnnotationPresent(ConstructorProperties.class));
        }
        
        @Override
        public InstanceStrategy create() {
            return new ConstructorInstanceStrategy();
        }
    }
}
