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
package com.github.kburger.rdf4j.objectmapper.api

import org.eclipse.rdf4j.model.vocabulary.RDF
import org.eclipse.rdf4j.rio.RDFFormat
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.api.reader.ObjectReader
import com.github.kburger.rdf4j.objectmapper.api.reader.StringValueConverter
import com.github.kburger.rdf4j.objectmapper.api.writer.ObjectWriter
import com.github.kburger.rdf4j.objectmapper.test.Constants
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.MixInClasses.ExampleMixIn
import spock.lang.Specification

class AbstractObjectMapperSpec extends Specification {
    def "verify addmodule invocation"() {
        given:
        def analyzer = Mock(ClassAnalyzer)
        def reader = Mock(ObjectReader)
        def writer = Mock(ObjectWriter)
        def mapper = new AbstractObjectMapper(analyzer, reader, writer) {}
        def module = Mock(Module)
        
        when:
        mapper.addModule(module)
        
        then:
        with (module) {
            1 * setup(analyzer)
            1 * setup(reader)
            1 * setup(writer)
        }
    }
    
    def "verify discover mechanism"() {
        given:
        def analyzer = Mock(ClassAnalyzer)
        def reader = Mock(ObjectReader)
        def writer = Mock(ObjectWriter)
        def mapper = new AbstractObjectMapper(analyzer, reader, writer) {}
        
        when:
        mapper.discover()
        
        then:
        1 * analyzer.registerMixIn(BeanExampleClass, ExampleMixIn)
        and:
        1 * reader.registerValueConverter(Integer, _ as StringValueConverter)
        and:
        1 * writer.registerNamespace(RDF.NS)
    }
    
    def "verify read invocation"() {
        given:
        def reader = Mock(ObjectReader)
        def mapper = new AbstractObjectMapper(Mock(ClassAnalyzer), reader, Mock(ObjectWriter)) {}
        
        when:
        mapper.read(_, BeanExampleClass, Constants.SUBJECT, RDFFormat.TURTLE)
        
        then:
        1 * reader.read(_, BeanExampleClass, Constants.SUBJECT, RDFFormat.TURTLE)
    }
    
    def "verify write invocation"() {
        given:
        def writer = Mock(ObjectWriter)
        def mapper = new AbstractObjectMapper(Mock(ClassAnalyzer), Mock(ObjectReader), writer) {}
        def bean = new BeanExampleClass()
        
        when:
        mapper.write(_, bean, Constants.SUBJECT, RDFFormat.TURTLE)
        
        then:
        1 * writer.write(_, bean, Constants.SUBJECT, RDFFormat.TURTLE)
    }
}
