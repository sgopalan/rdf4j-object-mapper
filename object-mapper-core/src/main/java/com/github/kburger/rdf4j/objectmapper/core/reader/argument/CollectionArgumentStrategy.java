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
package com.github.kburger.rdf4j.objectmapper.core.reader.argument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;
import com.github.kburger.rdf4j.objectmapper.core.util.Utils;

public class CollectionArgumentStrategy extends AbstractArgumentStrategy<Collection<Object>> {
    public CollectionArgumentStrategy(Method getter, int size) {
        super(Utils.resolveGenericTypeArgument(getter));
        this.value = new ArrayList<>(size);
    }
    
    @Override
    public <V> void addValue(V value) {
        this.value.add(value);
    }
    
    @Override
    public <A extends Annotation> void addInstanceProperty(InstanceStrategy instanceStrategy,
            PropertyAnalysis<A> property) {
        if (instanceStrategy.requiresElementHandling(property)) {
            value.forEach(val -> instanceStrategy.addProperty(property, val));
        } else {
            super.addInstanceProperty(instanceStrategy, property);
        }
    }
    
    public static class Factory implements ArgumentStrategy.Factory {
        @Override
        public <T> boolean supports(Class<T> clazz) {
            return Collection.class.isAssignableFrom(clazz);
        }
        
        @Override
        public ArgumentStrategy<?> create(Method getter, int size) {
            return new CollectionArgumentStrategy(getter, size);
        }
    }
}
