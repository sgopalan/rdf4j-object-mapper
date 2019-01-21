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
package com.github.kburger.rdf4j.objectmapper.api.analysis

import static com.github.kburger.rdf4j.objectmapper.test.util.TestUtils.*
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import spock.lang.Ignore
import spock.lang.Specification

class PropertyAnalysisSpec extends Specification {
    def "creating a PropertyAnalysis instance through the builder class"() {
        when: "the builder is used to create a complete instance"
        def propertyAnalysis = PropertyAnalysis.builder()
                .name("test")
                .annotation(findAnnotation(StringLiteralClass))
                .getter(findMethod(StringLiteralClass))
                .setter(findMethod(StringLiteralClass, "setValue"))
                .nested(true)
                .build()
        
        then: "the expected values are returned through the property getters"
        with (propertyAnalysis) {
            name == "test"
            annotation == findAnnotation(StringLiteralClass)
            getter == findMethodOptional(StringLiteralClass)
            setter == findMethodOptional(StringLiteralClass, "setValue")
            nested == true
        }
    }
    
    def "invoking a builder method twice"() {
        given: "a clean builder instance"
        def builder = PropertyAnalysis.builder()
        
        when: "the property builder method is invoked once, no exceptions are expected"
        builder."$method"(arg)
        and: "the method is invoked for the second time"
        builder."$method"(arg)
        then: "an illegal state exception is expected"
        thrown IllegalStateException
        
        where:
        method       | arg
        "name"       | "test"
        "annotation" | findAnnotation(StringLiteralClass)
        "getter"     | findMethod(StringLiteralClass)
        "setter"     | findMethod(StringLiteralClass, "setValue")
        "nested"     | true
    }
}
