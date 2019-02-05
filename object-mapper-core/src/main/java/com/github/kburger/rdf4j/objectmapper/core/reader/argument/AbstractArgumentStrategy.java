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

import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.google.common.primitives.Primitives;

public abstract class AbstractArgumentStrategy<T> implements ArgumentStrategy<T> {
    protected Class<?> type;
    
    protected T value;
    
    protected AbstractArgumentStrategy(Class<?> type) {
        this.type = type;
    }
    
    @Override
    public Class<?> getType() {
        return Primitives.wrap(type);
    }
    
    @Override
    public T build() {
        return value;
    }
}
