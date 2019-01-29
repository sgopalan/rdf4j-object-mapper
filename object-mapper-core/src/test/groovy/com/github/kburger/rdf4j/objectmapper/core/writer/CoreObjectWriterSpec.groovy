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
package com.github.kburger.rdf4j.objectmapper.core.writer

import org.eclipse.rdf4j.rio.RDFFormat
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.test.Constants
import com.github.kburger.rdf4j.objectmapper.test.IriClasses.StringUrlPropertyClass
import com.github.kburger.rdf4j.objectmapper.test.IriClasses.UriPropertyClass
import com.github.kburger.rdf4j.objectmapper.test.IriClasses.UrlPropertyClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingRelativeTypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingTypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.RelativeTypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.TypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.TypeClasses.DualTypeClass
import com.github.kburger.rdf4j.objectmapper.test.TypeClasses.SingleTypeClass
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class CoreObjectWriterSpec extends Specification {
    /** Shared analyzer instance. */
    @Shared analyzer = new ClassAnalyzer()
    
    /** Subject under test. */
    def writer = new CoreObjectWriter(analyzer)
    
    // convenience closure
    def write = { obj, wrt = new StringWriter(), subject = Constants.SUBJECT, format = RDFFormat.TURTLE ->
        writer.write(wrt, obj, subject, format)
        
        return wrt.toString()
    }
    
    def "single class level type is written"() {
        when:
        def output = write new SingleTypeClass()  
        
        then:
        output == """\
            
            <http://example.com/1> a <http://example.com/Type> .
            """.stripIndent()
    }
    
    def "multi class level types are written"() {
        when:
        def output = write new DualTypeClass()
        
        then:
        output == """\
                
                <http://example.com/1> a <http://example.com/Other>, <http://example.com/Type> .
                """.stripIndent()
    }
    
    def "string literal property is written"() {
        given:
        def object = new StringLiteralClass()
        object.value = "hello"
        
        when:
        def output = write object
        
        then:
        output == """\
                
                <http://example.com/1> <http://example.com/value> "hello" .
                """.stripIndent()
    }
    
    def "url property types are written"() {
        given:
        object.value = val
        
        when:
        def output = write object
        
        then:
        output == """\
                
                <http://example.com/1> <http://example.com/value> <$url> .
                """.stripIndent()
        
        where:
        object                       | val                             || url
        new UrlPropertyClass()       | new URL(Constants.NAMESPACE)    || Constants.NAMESPACE
        new UriPropertyClass()       | URI.create(Constants.NAMESPACE) || Constants.NAMESPACE
        new StringUrlPropertyClass() | Constants.NAMESPACE             || Constants.NAMESPACE
    }
    
    @Ignore
    def "nested absolute type level subject class is written"() {
        given:
        def object = new NestingTypeSubjectClass()
        object.nested = new TypeSubjectClass()
        
        when:
        def output = write object
        
        then:
        output == """\
                
                <http://example.com/1> <http://example.com/nested> <http://example.com/nested/1> .
                """.stripIndent()
    }
    
    @Ignore
    def "nested relative type level subject class is written"() {
        given:
        def object = new NestingRelativeTypeSubjectClass()
        object.nested = new RelativeTypeSubjectClass()
        
        when:
        def output = write object
        
        then:
        output == """\
                
                <http://example.com/1> <http://example.com/nested> <http://example.com/1#nested-1> .
                """.stripIndent()
    }
}
