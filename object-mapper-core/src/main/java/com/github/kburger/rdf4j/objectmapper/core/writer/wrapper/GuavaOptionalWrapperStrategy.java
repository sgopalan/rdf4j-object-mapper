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
package com.github.kburger.rdf4j.objectmapper.core.writer.wrapper;

import com.github.kburger.rdf4j.objectmapper.api.writer.DatatypeWrapperStrategy;
import com.google.common.base.Optional;

public class GuavaOptionalWrapperStrategy implements DatatypeWrapperStrategy {
    @Override
    public <T> boolean supports(T instance) {
        return instance != null && Optional.class.isAssignableFrom(instance.getClass());
    }
    @Override
    public <T> boolean isPresent(T instance) {
        return ((Optional<?>)instance).isPresent();
    }
    @Override
    public <T> Object unwrap(T instance) {
        return ((Optional<?>)instance).get();
    }
}
