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
package com.github.kburger.rdf4j.objectmapper.core.analysis

import com.github.kburger.rdf4j.objectmapper.annotations.Predicate
import com.github.kburger.rdf4j.objectmapper.annotations.Subject
import com.github.kburger.rdf4j.objectmapper.annotations.Type
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis
import spock.lang.Specification

class ClassAnalysisSpec extends Specification {
    static PropertyAnalysis<Type> PROPERTY_TYPE = PropertyAnalysis.<Type>builder()
            .build()
    static PropertyAnalysis<Subject> PROPERTY_SUBJECT = PropertyAnalysis.<Subject>builder()
            .build()
    static PropertyAnalysis<Predicate> PROPERTY_PREDICATE = PropertyAnalysis.<Predicate>builder()
            .build()
    
    def "creating a ClassAnalysis instance through its Builder"() {
        when: "the builder is used to create a complete instance"
        def analysis = ClassAnalysis.builder()
                .type(PROPERTY_TYPE)
                .subject(PROPERTY_SUBJECT)
                .predicate(PROPERTY_PREDICATE)
                .build()
        
        then: "the expected values are returned through the getters"
        with (analysis) {
            type == Optional.of(PROPERTY_TYPE)
            subject == Optional.of(PROPERTY_SUBJECT)
            predicates == [ PROPERTY_PREDICATE ]
        }
    }
    
    def "invoking a builder method twice"() {
        given: "a fresh builder instance"
        def builder = ClassAnalysis.builder()
        
        when: "the property builder method is invoked once, no exceptions are expected"
        builder."$method"(arg)
        and: "the method is invoked for the second time"
        builder."$method"(arg)
        then: "an exception is expected"
        thrown IllegalStateException
        
        where:
        method      | arg
        "type"      | PROPERTY_TYPE
        "subject"   | PROPERTY_SUBJECT
        "predicate" | PROPERTY_PREDICATE
    }
}
