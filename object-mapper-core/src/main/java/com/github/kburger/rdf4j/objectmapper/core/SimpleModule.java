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
package com.github.kburger.rdf4j.objectmapper.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.rdf4j.model.Namespace;
import com.github.kburger.rdf4j.objectmapper.api.Module;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.ValueConverter;
import com.github.kburger.rdf4j.objectmapper.api.writer.DatatypeWrapperStrategy;

public class SimpleModule implements Module {
    private final List<InstanceStrategy.Factory> instanceStrategies;
    private final List<ArgumentStrategy.Factory> argumentStrategies;
    private final Map<Class<?>, ValueConverter<?>> converters;
    private final List<Namespace> namespaces;
    private final List<DatatypeWrapperStrategy> datatypeWrapperStrategies;
    private final Map<Class<?>, Class<?>> mixIns;
    
    public SimpleModule() {
        instanceStrategies = new ArrayList<>();
        argumentStrategies = new ArrayList<>();
        converters = new HashMap<>();
        namespaces = new ArrayList<>();
        datatypeWrapperStrategies = new ArrayList<>();
        mixIns = new HashMap<>();
    }
    
    public SimpleModule addInstanceStrategy(InstanceStrategy.Factory strategy) {
        instanceStrategies.add(strategy);
        return this;
    }
    
    public SimpleModule addArgumentStrategy(ArgumentStrategy.Factory strategy) {
        argumentStrategies.add(strategy);
        return this;
    }
    
    public <T> SimpleModule addValueConverter(Class<T> clazz, ValueConverter<T> converter) {
        converters.put(clazz, converter);
        return this;
    }
    
    public SimpleModule addNamespace(Namespace namespace) {
        namespaces.add(namespace);
        return this;
    }
    
    public SimpleModule addDatatypeWrapperStrategy(DatatypeWrapperStrategy strategy) {
        datatypeWrapperStrategies.add(strategy);
        return this;
    }
    
    public <T, U> SimpleModule addMixIn(Class<T> target, Class<U> mixIn) {
        mixIns.put(target, mixIn);
        return this;
    }
    
    @Override
    public void setup(Context context) {
        instanceStrategies.forEach(context::registerInstanceStrategy);
        
        argumentStrategies.forEach(context::registerArgumentStrategy);
        
        converters.forEach(context::registerValueConverter);
        
        namespaces.forEach(context::registerNamespace);
        
        datatypeWrapperStrategies.forEach(context::registerDatatypeWrapperStrategy);
        
        mixIns.forEach(context::registerMixIn);
    }
}
