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

import java.util.ArrayList;
import java.util.Collection;
import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import lombok.Getter;

public class BuilderClasses {
    public static class BuilderExampleClass {
        @Getter @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        @Getter @Predicate(Constants.PREDICATE_DESCRIPTION)
        private String description;
        
        private BuilderExampleClass() {}
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private BuilderExampleClass example;
            
            private Builder() {
                example = new BuilderExampleClass();
            }
            
            public Builder value(String value) {
                example.value = value;
                return this;
            }
            
            public Builder description(String description) {
                example.description = description;
                return this;
            }
            
            public BuilderExampleClass build() {
                return example;
            }
        }
    }
    
    public static class AbsentBuilderMethodClass {}
    
    public static class ThrowingBuilderMethodClass {
        public static Builder builder() {
            throw new RuntimeException("for testing");
        }
        public static class Builder {}
    }
    
    public static class AbsentPropertyBuilderMethodClass {
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Builder() {}
            public AbsentPropertyBuilderMethodClass build() {
                return new AbsentPropertyBuilderMethodClass();
            }
        }
    }
    
    public static class ThrowingPropertyBuilderMethodClass {
        @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Builder() {}
            
            public Builder value(String value) {
                throw new RuntimeException("for testing");
            }
        }
    }
    
    public static class PerElementMethodExamplesClass {
        @Getter @Predicate(Constants.PREDICATE_VALUE)
        private String singleValue;
        @Getter @Predicate(Constants.PREDICATE_VALUE)
        private String[] arrayValue;
        @Getter @Predicate(Constants.PREDICATE_VALUE)
        private Collection<String> collectionValue;
        @Getter @Predicate(Constants.PREDICATE_VALUE)
        private Collection<String> perCollectionValue;
        
        private PerElementMethodExamplesClass() {
            collectionValue = new ArrayList<>();
            perCollectionValue = new ArrayList<>();
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private PerElementMethodExamplesClass examples;
            
            private Builder() {
                examples = new PerElementMethodExamplesClass();
            }
            
            public Builder singleValue(String singleValue) {
                examples.singleValue = singleValue;
                return this;
            }
            
            public Builder arrayValue(String[] arrayValue) {
                examples.arrayValue = arrayValue;
                return this;
            }
            
            public Builder collectionValue(Collection<String> collectionValue) {
                examples.collectionValue = collectionValue;
                return this;
            }
            
            public Builder perCollectionValue(String perCollectionValue) {
                examples.perCollectionValue.add(perCollectionValue);
                return this;
            }
            
            public PerElementMethodExamplesClass build() {
                return examples;
            }
        }
    }
    
    public static class AbsentBuildMethodClass {
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Builder() {}
        }
    }
    
    public static class ThrowingBuildMethodClass {
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Builder() {}
            
            public ThrowingBuilderMethodClass build() {
                throw new RuntimeException("for testing");
            }
        }
    }
}
