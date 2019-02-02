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
package com.github.kburger.rdf4j.objectmapper.core.reader.instance

import static com.github.kburger.rdf4j.objectmapper.test.util.TestUtils.*
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException
import com.github.kburger.rdf4j.objectmapper.core.reader.instance.ConstructorInstanceStrategy.Factory
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.BuilderExampleClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.ConstructorExampleClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.MultiFieldConstructorClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.NonAnnotatedConstructorClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.OverflowingAnnotationArgsConstructor
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.ThrowingConstructorClass

class ConstructorInstanceStrategySpec extends BaseInstanceStrategySpec {
    /** Subject under test. */
    def strategy = new ConstructorInstanceStrategy()
    
    def "factory support check"() {
        expect:
        new Factory().supports(type) == result
        
        where:
        type                    || result
        BeanExampleClass        || false
        BuilderExampleClass     || false
        ConstructorExampleClass || true
    }
    
    def "factory creation method created a valid instance"() {
        expect:
        new Factory().create() instanceof ConstructorInstanceStrategy
    }
    
    def "initializing a constructor class with a non-annotated constructor"() {
        when:
        strategy.initialize(NonAnnotatedConstructorClass)
        
        then:
        thrown ObjectReaderException
    }
    
    def "initializing a constructor class with overflowing annotation arguments"() {
        when:
        strategy.initialize(OverflowingAnnotationArgsConstructor)
        
        then:
        thrown ObjectReaderException
    }
    
    def "initializing a constructor class with a throwing constructor"() {
        given:
        strategy.initialize(ThrowingConstructorClass)
        and:
        strategy.addProperty(prop(ThrowingConstructorClass), "hello")
        
        when:
        strategy.build()
        
        then:
        thrown ObjectReaderException
    }
    
    def "adding a non-existing property will be handled"() {
        given:
        strategy.initialize(ConstructorExampleClass)
        
        when:
        strategy.addProperty(prop(ConstructorExampleClass, "unknown"), "example")
        
        then:
        thrown ObjectReaderException
    }
    
    def "arguments in a different order will be properly resolved and applied to the constructor"() {
        given:
        strategy.initialize(MultiFieldConstructorClass)
        
        when:
        strategy.addProperty(prop(MultiFieldConstructorClass, "third"), "3")
        strategy.addProperty(prop(MultiFieldConstructorClass, "first"), "1")
        strategy.addProperty(prop(MultiFieldConstructorClass, "second"), "2")
        and:
        def bean = strategy.build()
        
        then:
        with (bean) {
            first == "1"
            second == "2"
            third == "3"
        }
    }
    
    
    def "perfect scenario"() {
        when:
        strategy.initialize(ConstructorExampleClass)
        and:
        strategy.addProperty(prop(ConstructorExampleClass), "example")
        strategy.addProperty(prop(ConstructorExampleClass, "desc"), "example desc")
        and:
        def bean = strategy.build()
        
        then:
        with (bean) {
            value == "example"
            desc == "example desc"
        }
    }
}
