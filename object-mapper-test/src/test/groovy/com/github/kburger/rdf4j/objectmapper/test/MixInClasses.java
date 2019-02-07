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
import com.github.kburger.rdf4j.objectmapper.annotations.Type;
import com.github.kburger.rdf4j.objectmapper.annotations.ext.MixIn;

public class MixInClasses {
    @MixIn
    public static interface ExampleMixIn {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true)
        String getValue();
    }
    
    @MixIn
    public static interface EmptyMixIn {}
    
    @MixIn
    public static interface DefaultProvidingMixIn {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true)
        default String getValue() {
            return "example";
        }
    }
    
    @MixIn
    public static interface MismatchedNameMixIn {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true)
        String foo();
    }
    
    @MixIn
    public static interface MismatchedParameterMixIn {
        @Predicate(value = Constants.PREDICATE_VALUE, literal = true)
        String getValue(String foo);
    }
    
    @MixIn
    @Subject(Constants.SUBJECT)
    public static interface SubjectMixIn {}
    
    @MixIn
    @Type(Constants.TYPE)
    public static interface TypeMixIn {}
    
    @MixIn
    public static interface DefaultSubjectMixIn {
        @Subject
        default String getSubject() {
            return Constants.SUBJECT;
        }
    }
    
    @MixIn
    public static interface DefaultTypeMixIn {
        @Type
        default String getType() {
            return Constants.TYPE;
        }
    }
    
    @MixIn(override = true)
    public static interface OverridingMixIn {
        @Predicate(value = Constants.NAMESPACE+"new-value", literal = true)
        String getValue();
    }
    
    @MixIn(inherit = false)
    public static interface PreventInheritMixIn {}
}
