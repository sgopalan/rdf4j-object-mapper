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

import static com.github.kburger.rdf4j.objectmapper.test.util.TestUtils.*
import com.github.kburger.rdf4j.objectmapper.annotations.Subject
import com.github.kburger.rdf4j.objectmapper.annotations.Type
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalysis
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.DefaultProvidingMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.DefaultSubjectMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.DefaultTypeMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.EmptyMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.ExampleMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.MismatchedNameMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.MismatchedParameterMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.SubjectMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.TypeMixIn
import com.github.kburger.rdf4j.objectmapper.test.NonAnnotatedClasses.NonAnnotatedExampleClass
import spock.lang.Specification

class MixInAnalyzerSpec extends Specification {
    /** Subject under test. */
    def analyzer = new MixInAnalyzer()
    
    // convenience closure
    def analyze = { mixIn, target = NonAnnotatedExampleClass ->
        analyzer.registerMixIn(target, mixIn)
        
        def builder = ClassAnalysis.builder()
        def annotation = analyzer.analyze(target, builder)
        
        return [ annotation, builder.build() ]
    }
    
    def "registering a mixin"() {
        when: "a non mix-in type is registered"
        analyzer.registerMixIn(NonAnnotatedExampleClass, Object)
        
        then:
        def ex = thrown AnalysisException
        ex.message == "Could not register mix-in type without annotation"
    }
    
    def "verifying the registration of a mix-in"() {
        when:
        analyzer.registerMixIn(NonAnnotatedExampleClass, ExampleMixIn)
        
        then:
        analyzer.mixIns.size() == 1
    }
    
    def "analyzing a target class without a registered mix-in"() {
        when:
        def mixIn = analyzer.analyze(Object, ClassAnalysis.builder())
        
        then:
        mixIn == Optional.empty()
    }
    
    def "analyzing an empty mix-in"() {
        when:
        def (annotation, analysis) = analyze(EmptyMixIn)
        
        then:
        with (analysis) {
            type.empty
            subject.empty
            predicates.empty
        }
    }
    
    def "analyzing a mix-in interface with a default method"() {
        when:
        def (annotation, analysis) = analyze(DefaultProvidingMixIn)
        
        then:
        with (analysis) {
            type.empty
            subject.empty
            predicates.size() == 1
        }
        and:
        with (analysis.predicates[0]) {
            getter == findMethodOptional(DefaultProvidingMixIn)
        }
    }
    
    def "analyzing a mismatching mix-in on the method name"() {
        when:
        analyze(MismatchedNameMixIn)
        
        then:
        def ex = thrown AnalysisException
        ex.message == "Could not find target method defined by mix-in"
    }
    
    def "analyzing a mismatching mix-in on the method parameters"() {
        when:
        analyze(MismatchedParameterMixIn)
        
        then:
        def ex = thrown AnalysisException
        ex.message == "Could not find target method defined by mix-in"
    }
    
    def "analyzing a type level @Type annotation mix-in"() {
        when:
        def (_, analysis) = analyze(TypeMixIn)
        
        then:
        with (analysis) {
            type.present
            subject.empty
            predicates.empty
        }
        and:
        with (analysis.type.get()) {
            annotation == TypeMixIn.getAnnotation(Type)
        }
    }
    
    def "analyzing an @Type method mix-in"() {
        when:
        def (_, analysis) = analyze(DefaultTypeMixIn)
        
        then:
        with (analysis) {
            type.present
            subject.empty
            predicates.empty
        }
        and:
        with (analysis.type.get()) {
            getter == findMethodOptional(DefaultTypeMixIn, "getType")
        }
    }
    
    def "analyzing a type level @Subject annotation mix-in"() {
        when:
        def (_, analysis) = analyze(SubjectMixIn)
        
        then:
        with (analysis) {
            type.empty
            subject.present
            predicates.empty
        }
        and:
        with (analysis.subject.get()) {
            annotation == SubjectMixIn.getAnnotation(Subject)
        }
    }
    
    def "analyzing an @Subject method mix-in"() {
        when:
        def (_, analysis) = analyze(DefaultSubjectMixIn)
        
        then:
        with (analysis) {
            type.empty
            subject.present
            predicates.empty
        }
        and:
        with (analysis.subject.get()) {
            getter == findMethodOptional(DefaultSubjectMixIn, "getSubject")
        }
    }
    
    def "happy flow"() {
        when:
        def (_, analysis) = analyze(ExampleMixIn)
        
        then:
        with (analysis.predicates[0]) {
            getter == findMethodOptional(NonAnnotatedExampleClass)
        }
    }
}
