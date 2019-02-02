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

import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException
import com.github.kburger.rdf4j.objectmapper.core.reader.instance.BuilderInstanceStrategy.Factory
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.AbsentBuildMethodClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.AbsentBuilderMethodClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.AbsentPropertyBuilderMethodClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.BuilderExampleClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.PerElementMethodExamplesClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.ThrowingBuildMethodClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.ThrowingBuilderMethodClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.ThrowingPropertyBuilderMethodClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.ConstructorExampleClass

class BuilderInstanceStrategySpec extends BaseInstanceStrategySpec {
    /** Subject under test. */
    def strategy = new BuilderInstanceStrategy()
    
    def "factory support check"() {
        expect:
        new Factory().supports(type) == result
        
        where:
        type                    || result
        BeanExampleClass        || false
        ConstructorExampleClass || false
        BuilderExampleClass     || true
    }
    
    def "factory creation method creates a valid instance"() {
        expect:
        new Factory().create() instanceof BuilderInstanceStrategy
    }
    
    def "initializing a builder class with an absent static builder method"() {
        when:
        strategy.initialize(AbsentBuilderMethodClass)
        
        then:
        thrown ObjectReaderException
    }
    
    def "initializing a builder class with a throwing static builder method"() {
        when:
        strategy.initialize(ThrowingBuilderMethodClass)
        
        then:
        thrown ObjectReaderException
    }
    
    def "test per element handling"() {
        given:
        strategy.initialize(PerElementMethodExamplesClass)
        
        expect:
        strategy.requiresElementHandling(prop(PerElementMethodExamplesClass, name)) == result
        
        where:
        name                 || result
        "singleValue"        || false
        "arrayValue"         || false
        "collectionValue"    || false
        "perCollectionValue" || true
    }
    
    def "adding a non-existing property will be handled"() {
        given:
        strategy.initialize(BuilderExampleClass)
        
        when:
        strategy.addProperty(prop(BuilderExampleClass, "unknown"), "example")
        
        then:
        thrown ObjectReaderException
    }
    
    def "adding a property with an absent property builder method"() {
        given:
        strategy.initialize(AbsentPropertyBuilderMethodClass)
        
        when:
        strategy.addProperty(prop(AbsentPropertyBuilderMethodClass), "example")
        
        then:
        thrown ObjectReaderException
    }
    
    def "adding a property with a throwing property builder method"() {
        given:
        strategy.initialize(ThrowingPropertyBuilderMethodClass)
        
        when:
        strategy.addProperty(prop(ThrowingPropertyBuilderMethodClass), "example")
        
        then:
        thrown ObjectReaderException
    }
    
    def "finalizing a builder class with an absent build method"() {
        given:
        strategy.initialize(AbsentBuildMethodClass)
        
        when:
        strategy.build()
        
        then:
        thrown ObjectReaderException
    }
    
    def "finalizing a builder class with a throwing build method"() {
        given:
        strategy.initialize(ThrowingBuildMethodClass)
        
        when:
        strategy.build()
        
        then:
        thrown ObjectReaderException
    }
    
    def "perfect scenario"() {
        when:
        strategy.initialize(BuilderExampleClass)
        and:
        strategy.addProperty(prop(BuilderExampleClass), "example")
        and:
        strategy.addProperty(prop(BuilderExampleClass, "description"), "example description")
        and:
        def bean = strategy.build()
        
        then:
        with (bean) {
            value == "example"
            description == "example description"
        }
    }
}
