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
package com.github.kburger.rdf4j.objectmapper.test;

import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import lombok.Data;

public class ValidationClasses {
    @Data
    public static class AllRequiredClass {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true, required = true)
        private String value;
        @Predicate(value = Constants.PREDICATE_DESCRIPTION, literal = true, required = true)
        private String description;
    }
    
    @Data
    public static class AllNonRequiredClass {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true, required = false)
        private String value;
        @Predicate(value = Constants.PREDICATE_DESCRIPTION, literal = true, required = false)
        private String description;
    }
    
    @Data
    public static class MixedRequiredClass {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true, required = true)
        private String value;
        @Predicate(value = Constants.PREDICATE_DESCRIPTION, literal = true, required = false)
        private String description;
    }
    
    public static class ReadOnlyPropertyClass {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true, readonly = true)
        private String value;
    }
}
