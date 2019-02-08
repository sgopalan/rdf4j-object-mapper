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
package com.github.kburger.rdf4j.objectmapper.repository.analysis

import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingExampleClass
import com.github.kburger.rdf4j.objectmapper.test.ValidationClasses.MixedRequiredClass
import spock.lang.Shared
import spock.lang.Specification

class QueryGeneratorSpec extends Specification {
    @Shared analyzer = new ClassAnalyzer()
    
    /** Subject under test. */
    def generator = new QueryGenerator(analyzer)
    
    def "analyzing required string literal property"() {
        when:
        def query = generator.generate(StringLiteralClass)
        
        then:
        query == """\
                CONSTRUCT {
                  ?s <http://example.com/value> ?o1
                } WHERE {
                  ?s <http://example.com/value> ?o1
                }
                """.stripIndent()
    }
    
    def "analyzing class with mixed constraints"() {
        when:
        def query = generator.generate(MixedRequiredClass)
        
        then:
        query == """\
                CONSTRUCT {
                  ?s <http://example.com/value> ?o1 .
                  ?s <http://example.com/description> ?o2
                } WHERE {
                  ?s <http://example.com/value> ?o1 .
                  OPTIONAL { ?s <http://example.com/description> ?o2 }
                }
                """.stripIndent()
    }
    
    def "analyzing class with nested complex properties"() {
        when:
        def query = generator.generate(NestingExampleClass)
        
        then:
        query == """\
                CONSTRUCT {
                  ?s <http://example.com/nested> ?o1 .
                  ?o1 <http://example.com/value> ?o2
                } WHERE {
                  ?s <http://example.com/nested> ?o1 .
                  ?o1 <http://example.com/value> ?o2
                }
                """.stripIndent()
    }
}
