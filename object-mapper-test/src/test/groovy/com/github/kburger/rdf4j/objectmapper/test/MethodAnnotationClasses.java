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
import lombok.Getter;
import lombok.Setter;

public class MethodAnnotationClasses {
    public static class MethodPredicateAnnotationClass {
        private @Setter String value;
        
        @Predicate(Constants.PREDICATE_VALUE)
        public String getValue() { return value; }
    }
    
    public static class MixedFieldMethodAnnotationClass {
        @Predicate(Constants.PREDICATE_VALUE)
        private @Getter @Setter String value;
        
        private @Setter String description;
        
        @Predicate(value = Constants.PREDICATE_DESCRIPTION, literal = true)
        public String getDescription() { return description; }
    }
}
