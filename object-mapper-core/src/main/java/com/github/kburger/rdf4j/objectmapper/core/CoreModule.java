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
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import com.github.kburger.rdf4j.objectmapper.api.Module;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.StringValueConverter;
import com.github.kburger.rdf4j.objectmapper.api.reader.ValueConverter;
import com.github.kburger.rdf4j.objectmapper.api.writer.DatatypeWrapperStrategy;

public class CoreModule implements Module {
    private final List<InstanceStrategy.Factory> instanceStrategies;
    private final List<ArgumentStrategy.Factory> argumentStrategies;
    private final Map<Class<?>, ValueConverter<?>> converters;
    private final List<Namespace> namespaces;
    private final List<DatatypeWrapperStrategy> datatypeWrapperStrategies;
    private final Map<Class<?>, Class<?>> mixIns;
    
    public CoreModule() {
        instanceStrategies = new ArrayList<>();
        argumentStrategies = new ArrayList<>();
        converters = new HashMap<>();
        namespaces = new ArrayList<>();
        datatypeWrapperStrategies = new ArrayList<>();
        mixIns = new HashMap<>();
    }
    
    /**
     * Add a {@link InstanceStrategy.Factory} instance to the module. The factory instance will be
     * applied to a {@link Module.Context} in the {@link #setup(Module.Context)} method.
     * @param factory factory instance.
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerInstanceStrategy(InstanceStrategy.Factory)
     */
    public CoreModule add(InstanceStrategy.Factory factory) {
        instanceStrategies.add(factory);
        return this;
    }
    
    /**
     * Add a {@link ArgumentStrategy.Factory} instance to the module. The factory instance will be
     * applied to a {@link Module.Context} in the {@link #setup(Module.Context)} method.
     * @param factory factory instance.
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerArgumentStrategy(ArgumentStrategy.Factory)
     */
    public CoreModule add(ArgumentStrategy.Factory factory) {
        argumentStrategies.add(factory);
        return this;
    }
    
    /**
     * Add a {@link ValueConverter} instance and its target {@code clazz} to the module. The
     * converter instance mapping will be applied to a {@Link Module.Context} in the
     * {@link #setup(Module.Context)} method.
     * @param clazz target type.
     * @param converter converter instance that converts into the target type.
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerValueConverter(Class, ValueConverter)
     */
    public <T> CoreModule add(Class<T> clazz, ValueConverter<T> converter) {
        converters.put(clazz, converter);
        return this;
    }
    
    /**
     * Convenience method for registering {@code StringValueConverter} mappings. The converter
     * instance will be applied to a {@link Module.Context} in the {@link #setup(Module.Context)}
     * method.
     * @param clazz target type.
     * @param converter converter instance that converts into the target type.
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerValueConverter(Class, StringValueConverter)
     */
    public <T> CoreModule add(Class<T> clazz, StringValueConverter<T> converter) {
        return add(clazz, (ValueConverter<T>)converter);
    }
    
    /**
     * Add a namespace to the module. The namespace will be applied to a {@link Module.Context} in
     * the {@link #setup(Module.Context)} method.
     * @param namespace namespace instance.
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerNamespace(Namespace)
     */
    public CoreModule add(Namespace namespace) {
        namespaces.add(namespace);
        return this;
    }
    
    /**
     * Convenience method for registering a namespace and prefix mapping. The namespace will be
     * applied to a {@link Module.Context} in the {@link #setup(Module.Context)} method.
     * @param prefix namespace prefix.
     * @param namespace namespace address.
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerNamespace(String, String)
     */
    public CoreModule add(String prefix, String namespace) {
        return add(new SimpleNamespace(prefix, namespace));
    }
    
    /**
     * Add a {@link DatatypeWrapperStrategy} to the module. The strategy instance will be applied to
     * a {@link Module.Context} in the {@link #setup(Module.Context)} method.
     * @param strategy
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerDatatypeWrapperStrategy(DatatypeWrapperStrategy)
     */
    public CoreModule add(DatatypeWrapperStrategy strategy) {
        datatypeWrapperStrategies.add(strategy);
        return this;
    }
    
    /**
     * Add a mix-in mapping against its target type. The mix-in mapping will be applied to a
     * {@link Module.Context} in the {@link #setup(Module.Context)} method.
     * @param target target type.
     * @param mixIn the mix-in type.
     * @return this module instance, to allow for method chaining.
     * @see Module.Context#registerMixIn(Class, Class)
     */
    public <T, U> CoreModule add(Class<T> target, Class<U> mixIn) {
        mixIns.put(target, mixIn);
        return this;
    }
    
    @Override
    public void setup(Module.Context context) {
        instanceStrategies.forEach(context::registerInstanceStrategy);
        
        argumentStrategies.forEach(context::registerArgumentStrategy);
        
        converters.forEach(context::registerValueConverter);
        
        namespaces.forEach(context::registerNamespace);
        
        datatypeWrapperStrategies.forEach(context::registerDatatypeWrapperStrategy);
        
        mixIns.forEach(context::registerMixIn);
    }
}
