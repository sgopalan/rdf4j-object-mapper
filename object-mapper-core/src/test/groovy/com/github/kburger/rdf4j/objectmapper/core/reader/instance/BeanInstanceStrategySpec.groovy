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
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException
import com.github.kburger.rdf4j.objectmapper.core.reader.instance.BeanInstanceStrategy.Factory
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.AbsentSetterBeanClass
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.NonDefaultConstructorBeanClass
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.ThrowingConstructorBeanClass
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.ThrowingSetterBeanClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.BuilderExampleClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.ConstructorExampleClass
import spock.lang.Specification

class BeanInstanceStrategySpec extends BaseInstanceStrategySpec {
    /** Subject under test. */
    def strategy = new BeanInstanceStrategy()
    
    def "factory support check"() {
        expect:
        new Factory().supports(type) == result
        
        where:
        type                    || result
        ConstructorExampleClass || false
        BuilderExampleClass     || false
        BeanExampleClass        || true
    }
    
    def "factory creation method creates a valid instance"() {
        expect:
        new Factory().create() instanceof BeanInstanceStrategy
    }
    
    def "initializing a bean with a non-default constructor"() {
        when:
        strategy.initialize(NonDefaultConstructorBeanClass)
        
        then:
        thrown ObjectReaderException
    }
    
    def "initializing a bean with an exception throwing constructor"() {
        when:
        strategy.initialize(ThrowingConstructorBeanClass)
        
        then:
        thrown ObjectReaderException
    }
    
    def "adding a property to a bean without a setter method"() {
        given:
        strategy.initialize(AbsentSetterBeanClass)
        
        when:
        strategy.addProperty(prop(AbsentSetterBeanClass), "hello")
        
        then:
        thrown ObjectReaderException
    }
    
    def "invoking a throwing setter"() {
        given:
        strategy.initialize(ThrowingSetterBeanClass)
        
        when:
        strategy.addProperty(prop(ThrowingSetterBeanClass), "hello")
        
        then:
        thrown ObjectReaderException
    }
    
    def "perfect scenario"() {
        when:
        strategy.initialize(BeanExampleClass)
        and:
        strategy.addProperty(prop(BeanExampleClass), "example")
        and:
        def bean = strategy.build()
        
        then:
        with (bean) {
            value == "example"
        }
    }
}
