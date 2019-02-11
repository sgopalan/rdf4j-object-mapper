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

import java.util.Collection;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;

public class SingleArgumentStrategy extends AbstractArgumentStrategy<Object> {
    public SingleArgumentStrategy(Class<?> type) {
        super(type);
    }
    
    @Override
    public <V> void addValue(V value) {
        if (this.value != null) {
            throw new ObjectReaderException("Will not overwrite the previously set value");
        }
        
        this.value = value;
    }
    
    public static class Factory implements ArgumentStrategy.Factory {
        @Override
        public <T> boolean supports(Class<T> clazz) {
            return !(clazz.isArray() || Collection.class.isAssignableFrom(clazz));
        }
        
        @Override
        public <T> ArgumentStrategy<?> create(Class<T> field, int size) {
            return new SingleArgumentStrategy(field);
        }
    }
}
