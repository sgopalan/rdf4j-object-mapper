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
package com.github.kburger.rdf4j.objectmapper.core.reader.argument

import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException
import com.github.kburger.rdf4j.objectmapper.core.reader.argument.SingleArgumentStrategy.Factory
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.BuilderExampleClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.ConstructorExampleClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.IntLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.IntegerLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.util.TestUtils
import spock.lang.Shared
import spock.lang.Specification

class SingleArgumentStrategySpec extends Specification {
    @Shared factory = new Factory()
    
    def create = { clz -> factory.create(TestUtils.findMethod(clz), 1) }
    
    def "factory type support verification"() {
        expect:
        factory.supports(type) == result
        
        where:
        type                    || result
        Object                  || true
        StringLiteralClass      || true
        BeanExampleClass        || true
        ConstructorExampleClass || true
        BuilderExampleClass     || true
        Object[]                || false
        String[]                || false
        BeanExampleClass[]      || false
        Collection              || false
        List                    || false
    }
    
    def "strategy intrinsic type"() {
        given:
        def strategy = create(type)
        
        expect:
        strategy.getType() == result
        
        where:
        type                || result
        StringLiteralClass  || String
        IntLiteralClass     || Integer
        IntegerLiteralClass || Integer
    }
    
    def "adding an additional value"() {
        given:
        def strategy = create(StringLiteralClass)
        
        when:
        strategy.addValue("first")
        and:
        strategy.addValue("second")
        
        then:
        thrown ObjectReaderException
    }
    
    def "verify the added value"() {
        given:
        def strategy = create(StringLiteralClass)
        
        when:
        strategy.addValue("example")
        
        then:
        strategy.build() == "example"
    }
}
