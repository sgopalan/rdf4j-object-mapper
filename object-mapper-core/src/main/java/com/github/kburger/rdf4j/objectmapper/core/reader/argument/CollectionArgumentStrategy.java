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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;
import com.github.kburger.rdf4j.objectmapper.core.util.Utils;

public class CollectionArgumentStrategy extends AbstractArgumentStrategy<Collection<Object>> {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends Collection>, Function<Integer, ? extends Collection<Object>>> MAPPING;
    private static final Function<Integer, ? extends Collection<Object>> DEFAULT = ArrayList::new;
    
    static {
        MAPPING = new HashMap<>();
        MAPPING.put(List.class, ArrayList::new);
        MAPPING.put(Set.class, HashSet::new);
    }
    
    public CollectionArgumentStrategy(Field field, int size) {
        super(Utils.resolveTypeArgument(field));
        
        this.value = MAPPING.getOrDefault(field.getType(), DEFAULT).apply(size);
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
        public ArgumentStrategy<?> create(Field field, int size) {
            return new CollectionArgumentStrategy(field, size);
        }
    }
}
