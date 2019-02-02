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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;

public class BeanInstanceStrategy implements InstanceStrategy {
    private Object instance;
    
    @Override
    public <T> void initialize(Class<T> clazz) {
        try {
            var constructor = clazz.getConstructor();
            instance = constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new ObjectReaderException("Could not find a default constructor");
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new ObjectReaderException("Could not invoke the default constructor", e);
        }
    }

    @Override
    public <T extends Annotation> void addProperty(PropertyAnalysis<T> property, Object value) {
        var method = property.getSetter()
                .orElseThrow(() -> new ObjectReaderException("Could not find setter for property"));
        
        try {
            method.invoke(instance, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ObjectReaderException("Could not invoke setter for property", e);
        }
    }

    @Override
    public Object build() {
        return instance;
    }
    
    public static class Factory implements InstanceStrategy.Factory {
        @Override
        public <T> boolean supports(Class<T> clazz) {
            try {
                clazz.getConstructor();
                
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        
        @Override
        public InstanceStrategy create() {
            return new BeanInstanceStrategy();
        }
    }
}
