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
package com.github.kburger.rdf4j.objectmapper.api;

import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import com.github.kburger.rdf4j.objectmapper.annotations.ext.MixIn;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.StringValueConverter;
import com.github.kburger.rdf4j.objectmapper.api.reader.ValueConverter;
import com.github.kburger.rdf4j.objectmapper.api.writer.DatatypeWrapperStrategy;

/**
 * Configuration module to extend receptive classes (through the {@link Context} interface) with
 * new components or mappings.
 */
public interface Module {
    /**
     * Setup the receptive module {@link Context} and apply all the registered configured component
     * to the implementing context.
     * @param context the implementing context.
     */
    void setup(Context context);
    
    /**
     * Module context interface for classes receptive to the extension components and/or mappings.
     * The default behaviour of the methods in this interface is to no-op. Implementing classes can
     * cherry pick the methods to implement.
     */
    interface Context {
        /**
         * Register a new {@link InstanceStrategy} factory. The {@link InstanceStrategy.Factory}
         * instance is assumed to be stateless, and assumed to produce a stateful
         * {@code InstanceStrategy} instance.
         * @param factory factory instance.
         */
        default void registerInstanceStrategy(InstanceStrategy.Factory factory) {
            // empty default impl
        }
        
        /**
         * Register a new {@link ArgumentStrategy} factory. The {@link ArgumentStrategy.Factory}
         * instance is assumed to be stateless, and assumed to produce a stateful
         * {@code ArgumentStrategy} instance.
         * @param factory factory instance.
         */
        default void registerArgumentStrategy(ArgumentStrategy.Factory factory) {
            // empty default impl
        }
        
        /**
         * Register a new {@link ValueConverter} against a target {@code clazz} type mapping.
         * @param clazz target type.
         * @param converter {@code ValueConverter} instance that converts into the target type.
         */
        default void registerValueConverter(Class<?> clazz, ValueConverter<?> converter) {
            // empty default impl
        }
        
        /**
         * Register a new {@link StringValueConverter} against a target {@code clazz} type mapping.
         * This method allows for easy {@link ValueConverter} registration of method references that
         * comply to the {@code StringValueConverter} signature without the need for a cast to the
         * {@code StringValueConverter} interface, e.g. the convenience of writing
         * <pre>context.registerValueConverter(Integer.class, Integer::parseInteger);</pre>
         * vs.
         * <pre>context.registerValueConverter(Integer.class, (StringValueConverter&lt;Integer&gt;)Integer::parseInt);</pre>
         * @param clazz target type.
         * @param converter {@code StringValueConverter} instance that converts into the target type.
         */
        default void registerValueConverter(Class<?> clazz, StringValueConverter<?> converter) {
            registerValueConverter(clazz, (ValueConverter<?>)converter);
        }
        
        /**
         * Register a new namespace declaration.
         * @param namespace the new namespace.
         */
        default void registerNamespace(Namespace namespace) {
            // empty default impl
        }
        
        /**
         * Register a new prefix and namespace declaration. The default behaviour of this method is
         * to capture the prefix and namespace in a {@link Namespace} instance and pass it to the
         * {@link #registerNamespace(Namespace)} method.
         * @param prefix the namespace prefix.
         * @param namespace the namespace address.
         */
        default void registerNamespace(String prefix, String namespace) {
            registerNamespace(new SimpleNamespace(prefix, namespace));
        }
        
        /**
         * Register a new {@link DatatypeWrapperStrategy} instance.
         * @param strategy strategy instance.
         */
        default void registerDatatypeWrapperStrategy(DatatypeWrapperStrategy strategy) {
            // empty default impl
        }
        
        /**
         * Register a {@link MixIn} type against a {@code target} type.
         * @param <T> the target's type.
         * @param <U> the mix-in's type.
         * @param target type targeted by the mix-in type.
         * @param mixIn mix-in type providing additional (meta)data about the target type.
         */
        default <T, U> void registerMixIn(Class<T> target, Class<U> mixIn) {
            // empty default impl
        }
    }
}
