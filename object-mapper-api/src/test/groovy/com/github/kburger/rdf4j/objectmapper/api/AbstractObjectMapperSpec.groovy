package com.github.kburger.rdf4j.objectmapper.api

import org.eclipse.rdf4j.rio.RDFFormat
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.api.reader.ObjectReader
import com.github.kburger.rdf4j.objectmapper.api.writer.ObjectWriter
import com.github.kburger.rdf4j.objectmapper.test.Constants
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import spock.lang.Specification

class AbstractObjectMapperSpec extends Specification {
    def "verify the addmodule invocations"() {
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
    
    def "verify read invocations"() {
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
