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

import com.github.kburger.rdf4j.objectmapper.core.reader.argument.CollectionArgumentStrategy.Factory
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.BuilderExampleClass
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.RawCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.StringCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.WildcardCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.ConstructorClasses.ConstructorExampleClass
import spock.lang.Shared

class CollectionArgumentStrategySpec extends BaseArgumentStrategySpec {
    @Shared factory = new Factory()
    
    def "factory type support verification"() {
        expect:
        factory.supports(type) == result
        
        where:
        type                    || result
        Collection              || true
        List                    || true
        ArrayList               || true
        LinkedList              || true
        Set                     || true
        HashSet                 || true
        LinkedHashSet           || true
        Object                  || false
        String                  || false
        Object[]                || false
        java.util.Collections   || false
        BeanExampleClass        || false
        ConstructorExampleClass || false
        BuilderExampleClass     || false
    }
    
    def "strategy intrinsic type"() {
        given:
        def strategy = create(type)
        
        expect:
        strategy.getType() == result
        
        where:
        type                    || result
        RawCollectionClass      || Object
        WildcardCollectionClass || Object
        StringCollectionClass   || String
    }
    
    def "happy flow"() {
        given:
        def strategy = create(StringCollectionClass, "value", 3)
        
        when:
        strategy.addValue("first")
        strategy.addValue("second")
        strategy.addValue("third")
        
        then:
        strategy.build() == [ "first", "second", "third" ]
    }
}
