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
import lombok.Getter;

public class BeanClasses {
    @Data
    public static class BeanExampleClass {
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
    }
    
    public static class NonDefaultConstructorBeanClass {
        public NonDefaultConstructorBeanClass(String foo) {}
    }
    
    public static class ThrowingConstructorBeanClass {
        public ThrowingConstructorBeanClass() {
            throw new RuntimeException("for testing");
        }
    }
    
    public static class AbsentSetterBeanClass {
        @Getter
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
    }
    
    public static class ThrowingSetterBeanClass {
        @Getter
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        public void setValue(String value) {
            throw new RuntimeException("for testing");
        }
    }
}
