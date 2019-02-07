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
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.ValueConverter;

public interface Module {
    void setup(Context context);
    
    interface Context {
        default void registerInstanceStrategy(InstanceStrategy.Factory strategy) {
            // empty default impl
        }
        
        default void registerArgumentStrategy(ArgumentStrategy.Factory strategy) {
            // empty default impl
        }
        
        default void registerValueConverter(Class<?> clazz, ValueConverter<?> converter) {
            // empty default impl
        }
        
        default void registerNamespace(Namespace namespace) {
            // empty default impl
        }
        
        default void registerNamespace(String prefix, String namespace) {
            registerNamespace(new SimpleNamespace(prefix, namespace));
        }
        
        default <T, U> void registerMixIn(Class<T> target, Class<U> mixIn) {
            // empty default impl
        }
    }
}
