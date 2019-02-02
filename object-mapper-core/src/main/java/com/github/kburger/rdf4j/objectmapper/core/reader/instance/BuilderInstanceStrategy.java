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
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;

public class BuilderInstanceStrategy implements InstanceStrategy {
    private static final String BUILDER_METHOD = "builder";
    private static final String BUILD_METHOD = "build";
    
    
    private Object builder;
    
    @Override
    public <T> void initialize(Class<T> clazz) {
        try {
            var builderMethod = clazz.getMethod(BUILDER_METHOD);
            
            builder = builderMethod.invoke(null);
        } catch (NoSuchMethodException e) {
            throw new ObjectReaderException("Could not find builder method");
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ObjectReaderException("Could not invoke builder method", e);
        }
    }
    
    @Override
    public <T extends Annotation> boolean requiresElementHandling(PropertyAnalysis<T> property) {
        var getter = property.getGetter();
        
        if (getter.isEmpty()) {
            return false;
        }
        
        var method = getter.orElseThrow();
        var type = method.getGenericReturnType();
        
        if (type instanceof ParameterizedType == false) {
            return false;
        }
        
        var types = ((ParameterizedType)type).getActualTypeArguments();
        var builderType = builder.getClass();
        
        try {
            builderType.getMethod(property.getName(), (Class<?>)types[0]);
        } catch (NoSuchMethodException e) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public <T extends Annotation> void addProperty(PropertyAnalysis<T> property, Object value) {
        var builderType = builder.getClass();
        try {
            var builderPropertyMethod = builderType.getMethod(property.getName(), value.getClass());
            
            builderPropertyMethod.invoke(builder, value);
        } catch (NoSuchMethodException e) {
            throw new ObjectReaderException("Could not find builder property method");
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ObjectReaderException("Could not invoke builder property method", e);
        }
    }

    @Override
    public Object build() {
        var builderType = builder.getClass();
        
        try {
            var builderMethod = builderType.getMethod(BUILD_METHOD);
            
            return builderMethod.invoke(builder);
        } catch (NoSuchMethodException e) {
            throw new ObjectReaderException("Could not find build method on builder class");
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ObjectReaderException("Could not invoke build method on builder class", e);
        }
    }
    
    public static class Factory implements InstanceStrategy.Factory {
        @Override
        public <T> boolean supports(Class<T> clazz) {
            try {
                var builderMethod = clazz.getMethod(BUILDER_METHOD);
                
                return Modifier.isStatic(builderMethod.getModifiers());
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        
        @Override
        public InstanceStrategy create() {
            return new BuilderInstanceStrategy();
        }
    }
}
