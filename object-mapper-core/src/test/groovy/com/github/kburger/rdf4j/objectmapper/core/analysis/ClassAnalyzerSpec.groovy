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
import com.github.kburger.rdf4j.objectmapper.annotations.Predicate
import com.github.kburger.rdf4j.objectmapper.annotations.Type
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.InheritingClasses.ChildClass
import com.github.kburger.rdf4j.objectmapper.test.InheritingClasses.ParentClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.MethodAnnotationClasses.MethodPredicateAnnotationClass
import com.github.kburger.rdf4j.objectmapper.test.MethodAnnotationClasses.MixedFieldMethodAnnotationClass
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.OverridingMixIn
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.PreventInheritMixIn
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingTypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.RecursiveNodeClass
import com.github.kburger.rdf4j.objectmapper.test.OptionalClasses.JavaOptionalStringClass
import com.github.kburger.rdf4j.objectmapper.test.TypeClasses.SingleTypeClass
import spock.lang.Specification

class ClassAnalyzerSpec extends Specification {
    /** Subject under test. */
    def analyzer = new ClassAnalyzer()
    
    def "analyzing a JDK class"() {
        when: "a JDK class is analyzed"
        analyzer.analyze(Object)
        
        then: "the analyzer throws an exception"
        thrown AnalysisException
    }
    
    def "analysis results are cached"() {
        when: "a new type is analyzed"
        def first = analyzer.analyze(StringLiteralClass)
        then: "the cache size increments to one"
        analyzer.cache.size() == 1
        
        when: "the same type is analyzed again"
        def second = analyzer.analyze(StringLiteralClass)
        then: "the results are returned from the cache"
        analyzer.cache.size() == 1
        and: "the results from both analysis are equal"
        first == second
    }
    
    def "analysis results contain expected values"() {
        when:
        def analysis = analyzer.analyze(StringLiteralClass)
        
        then:
        with (analysis) {
            type == Optional.empty()
            subject == Optional.empty()
        }
        with (analysis.predicates[0]) {
            annotation == findAnnotation(StringLiteralClass)
            field == Optional.of(findField(StringLiteralClass))
            name == Optional.of("value")
            getter == findMethodOptional(StringLiteralClass)
            setter == findMethodOptional(StringLiteralClass, "setValue")
            nested == false
        }
    }
    
    def "type annotations on the top level are analyzed"() {
        when:
        def analysis = analyzer.analyze(SingleTypeClass)
        
        then:
        with (analysis.type.get()) {
            annotation == SingleTypeClass.getAnnotation(Type)
        }
    }
    
    def "method level annotations are analyzed"() {
        when:
        def analysis = analyzer.analyze(MethodPredicateAnnotationClass)
        
        then:
        with (analysis) {
            type == Optional.empty()
            subject == Optional.empty()
        }
        and:
        with (analysis.predicates[0]) {
            annotation == findMethod(MethodPredicateAnnotationClass).getAnnotation(Predicate)
            field == Optional.of(findField(MethodPredicateAnnotationClass))
            name == Optional.of("value")
            getter == findMethodOptional(MethodPredicateAnnotationClass)
            setter == findMethodOptional(MethodPredicateAnnotationClass, "setValue")
            nested == false
        }
    }
    
    def "mixed field and method level annotations are analyzed"() {
        when:
        def analysis = analyzer.analyze(MixedFieldMethodAnnotationClass)
        
        then:
        with (analysis) {
            type == Optional.empty()
            subject == Optional.empty()
        }
        and:
        with (analysis.predicates[0]) {
            annotation == findAnnotation(MixedFieldMethodAnnotationClass)
            field == Optional.of(findField(MixedFieldMethodAnnotationClass))
            name == Optional.of("value")
            getter == findMethodOptional(MixedFieldMethodAnnotationClass)
            setter == findMethodOptional(MixedFieldMethodAnnotationClass, "setValue")
            nested == false
        }
        and:
        with (analysis.predicates[1]) {
            annotation == findMethod(MixedFieldMethodAnnotationClass, "getDescription").getAnnotation(Predicate)
            field == Optional.of(findField(MixedFieldMethodAnnotationClass, "description"))
            name == Optional.of("description")
            getter == findMethodOptional(MixedFieldMethodAnnotationClass, "getDescription")
            setter == findMethodOptional(MixedFieldMethodAnnotationClass, "setDescription")
            nested == false
        }
    }
    
    def "child classes inherit the annotated properties from the parent"() {
        when:
        def analysis = analyzer.analyze(ChildClass)
        
        then:
        analyzer.cache.size() == 2
        and:
        with (analysis) {
            type == Optional.empty()
            subject == Optional.empty()
        }
        and:
        with (analysis.predicates[0]) {
            annotation == findAnnotation(ParentClass, "parentValue")
            field == Optional.of(findField(ParentClass, "parentValue"))
            name == Optional.of("parentValue")
            getter == findMethodOptional(ParentClass, "getParentValue")
            setter == findMethodOptional(ParentClass, "setParentValue")
            nested == false
        }
        and:
        with (analysis.predicates[1]) {
            annotation == findAnnotation(ChildClass, "childValue")
            field == Optional.of(findField(ChildClass, "childValue"))
            name == Optional.of("childValue")
            getter == findMethodOptional(ChildClass, "getChildValue")
            setter == findMethodOptional(ChildClass, "setChildValue")
            nested == false
        }
    }
    
    def "nesting classes "() {
        when:
        def analysis = analyzer.analyze(NestingTypeSubjectClass)
        
        then:
        analyzer.cache.size() == 2
        and:
        with (analysis) {
            type == Optional.empty()
            subject == Optional.empty()
        }
        and:
        with (analysis.predicates[0]) {
            annotation == findAnnotation(NestingTypeSubjectClass, "nested")
            field == Optional.of(findField(NestingTypeSubjectClass, "nested"))
            name == Optional.of("nested")
            getter == findMethodOptional(NestingTypeSubjectClass, "getNested")
            setter == findMethodOptional(NestingTypeSubjectClass, "setNested")
            nested == true
        }
    }
    
    def "recursive nested classes do not cause stack overflows"() {
        when:
        analyzer.analyze(RecursiveNodeClass)
        
        then:
        notThrown StackOverflowError
    }
    
    def "properties are ignored when an overriding mix-in type is present"() {
        given:
        analyzer.registerMixIn(BeanExampleClass, OverridingMixIn)
        
        when:
        def analysis = analyzer.analyze(BeanExampleClass)
        
        then:
        with (analysis) {
            type.empty
            subject.empty
            predicates.size() == 1
        }
        and:
        with (analysis.predicates[0]) {
            annotation == findMethod(OverridingMixIn).getAnnotation(Predicate)
            getter == findMethodOptional(BeanExampleClass)
        }
    }
    
    def "inherited properties are ignored when a appropriately configured mix-in type is present"() {
        given:
        analyzer.registerMixIn(ChildClass, PreventInheritMixIn)
        
        when:
        def analysis = analyzer.analyze(ChildClass)
        
        then:
        analyzer.cache.size() == 1
        and:
        with (analysis.predicates[0]) {
            annotation == findAnnotation(ChildClass, "childValue")
            getter == findMethodOptional(ChildClass, "getChildValue")
        }
    }
    
    def "java.util.Optional wrapped property getters are found"() {
        when:
        def analysis = analyzer.analyze(JavaOptionalStringClass)
        
        then:
        with (analysis.predicates[0]) {
            getter == findMethodOptional(JavaOptionalStringClass)
        }
    }
}
