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
package com.github.kburger.rdf4j.objectmapper.api.reader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;

@NotThreadSafe
public interface ArgumentStrategy<T> {
    Class<?> getType();
    
    <V> void addValue(V value);
    
    T build();
    
    default <A extends Annotation> void addInstanceProperty(InstanceStrategy instanceStrategy, PropertyAnalysis<A> property) {
        instanceStrategy.addProperty(property, build());
    }
    
    @ThreadSafe
    interface Factory {
        <T> boolean supports(Class<T> clazz);
        
        ArgumentStrategy<?> create(Field field, int size);
    }
}
