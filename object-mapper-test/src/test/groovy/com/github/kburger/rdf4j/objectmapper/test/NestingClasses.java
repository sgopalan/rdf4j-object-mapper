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
import com.github.kburger.rdf4j.objectmapper.annotations.Subject;
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass;
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.RelativeTypeSubjectClass;
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.SettableSubjectClass;
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.ThrowingSubjectGetterClass;
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.TypeSubjectClass;
import lombok.Data;

public class NestingClasses {
    @Data
    public static class NestingTypeSubjectClass {
        @Predicate(Constants.NAMESPACE+"nested")
        private TypeSubjectClass nested;
    }
    
    @Data
    public static class NestingRelativeTypeSubjectClass {
        @Predicate(Constants.NAMESPACE+"nested")
        private RelativeTypeSubjectClass nested;
    }
    
    @Data
    public static class RecursiveNodeClass {
        @Predicate(value = Constants.NAMESPACE+"node", required = false)
        private RecursiveNodeClass node;
    }
    
    @Data
    public static class NestingSettableSubjectClass {
        @Predicate(value = Constants.NAMESPACE+"nested")
        private SettableSubjectClass nested;
    }
    
    @Data
    public static class NestingNonSubjectAnnotationClass {
        @Predicate(Constants.NAMESPACE+"nested")
        private BeanExampleClass nested;
    }
    
    @Data
    public static class NestingThrowingSubjectGetterClass {
        @Predicate(Constants.NAMESPACE+"nested")
        private ThrowingSubjectGetterClass nested;
    }
    
    @Data
    public static class NestingExampleClass {
        @Predicate(Constants.NAMESPACE + "nested")
        private NestedExampleClass nested;
    }
    
    @Data
    public static class NestedExampleClass {
        @Subject
        private String subject;
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true)
        private String value;
    }
}
