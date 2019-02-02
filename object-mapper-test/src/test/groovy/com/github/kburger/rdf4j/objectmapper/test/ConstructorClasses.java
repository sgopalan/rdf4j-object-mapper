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

import java.beans.ConstructorProperties;
import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import lombok.Getter;

public class ConstructorClasses {
    public static class ConstructorExampleClass {
        @Getter @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        @Getter @Predicate(value = Constants.PREDICATE_DESCRIPTION, literal = true)
        private String desc;
        
        @ConstructorProperties({ "value", "desc"})
        public ConstructorExampleClass(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }
    
    public static class NonAnnotatedConstructorClass {
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        public NonAnnotatedConstructorClass(String value) {
            this.value = value;
        }
    }
    
    public static class ThrowingConstructorClass {
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        @ConstructorProperties("value")
        public ThrowingConstructorClass(String value) {
            throw new RuntimeException("for testing");
        }
    }
    
    public static class OverflowingAnnotationArgsConstructor {
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        @ConstructorProperties({ "value", "foo" })
        public OverflowingAnnotationArgsConstructor(String value) {
            this.value = value;
        }
    }
    
    public static class MismatchedAnnotationConstructor {
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        @ConstructorProperties("foo")
        public MismatchedAnnotationConstructor(String value) {
            this.value = value;
        }
    }
    
    public static class MultiFieldConstructorClass {
        @Getter @Predicate(Constants.NAMESPACE+"first")
        private String first;
        @Getter @Predicate(Constants.NAMESPACE+"second")
        private String second;
        @Getter @Predicate(Constants.NAMESPACE+"third")
        private String third;
        
        @ConstructorProperties({ "first", "second", "third" })
        public MultiFieldConstructorClass(String first, String second, String third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }
}
