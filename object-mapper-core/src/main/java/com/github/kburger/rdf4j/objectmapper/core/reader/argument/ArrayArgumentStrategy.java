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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;

public class ArrayArgumentStrategy extends AbstractArgumentStrategy<Object[]> {
    private int index;
    
    public ArrayArgumentStrategy(Class<?> type, int size) {
        super(type);
        
        value = (Object[])Array.newInstance(type, size);
    }
    
    @Override
    public <V> void addValue(V value) {
        if (index >= this.value.length) {
            throw new ObjectReaderException("Could not add more values than expected");
        }
        
        this.value[index++] = value;
    }
    
    public static class Factory implements ArgumentStrategy.Factory {
        @Override
        public <T> boolean supports(Class<T> clazz) {
            return clazz.isArray();
        }
        
        @Override
        public ArgumentStrategy<?> create(Method getter, int size) {
            var returnType = getter.getReturnType();
            
            return new ArrayArgumentStrategy(returnType.getComponentType(), size);
        }
    }
}
