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
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectWriterException
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ValidationException
import com.github.kburger.rdf4j.objectmapper.core.SimpleModule
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.core.writer.wrapper.GuavaOptionalWrapperStrategy
import com.github.kburger.rdf4j.objectmapper.core.writer.wrapper.JavaOptionalWrapperStrategy
import com.github.kburger.rdf4j.objectmapper.test.Constants
import com.github.kburger.rdf4j.objectmapper.test.ArrayClasses.PrimitiveArrayClass
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.StringCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.InvalidClasses.GetterlessClass
import com.github.kburger.rdf4j.objectmapper.test.InvalidClasses.ThrowingGetterClass
import com.github.kburger.rdf4j.objectmapper.test.IriClasses.StringUrlPropertyClass
import com.github.kburger.rdf4j.objectmapper.test.IriClasses.UriPropertyClass
import com.github.kburger.rdf4j.objectmapper.test.IriClasses.UrlPropertyClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.IntLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingNonSubjectAnnotationClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingSettableSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingThrowingSubjectGetterClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingTypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.OptionalClasses.GuavaOptionalStringClass
import com.github.kburger.rdf4j.objectmapper.test.OptionalClasses.JavaOptionalStringClass
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.SettableSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.ThrowingSubjectGetterClass
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.TypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.TypeClasses.DualTypeClass
import com.github.kburger.rdf4j.objectmapper.test.TypeClasses.SingleTypeClass
import com.github.kburger.rdf4j.objectmapper.test.ValidationClasses.AllNonRequiredClass
import com.github.kburger.rdf4j.objectmapper.test.ValidationClasses.AllRequiredClass
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
    
    def rdf = { ...lines -> ([Constants.RDF_HEADER] + lines).flatten().join("\n") + "\n" }
    
    def setup() {
        writer.registerNamespace(Constants.PREFIX, Constants.NAMESPACE)
    }
    
    def "single class level type is written"() {
        when: "a class providing a static type on the class level is written"
        def output = write new SingleTypeClass()  
        
        then: "the only expected output is the rdf type definition"
        output == rdf("ex:1 a ex:Type .")
    }
    
    def "multi class level types are written"() {
        when: "a class providing multiple static types on the class level is written"
        def output = write new DualTypeClass()
        
        then: "the expected output contains the multiple types"
        output == rdf("ex:1 a ex:Other, ex:Type .")
    }
    
    def "handling absent property getter"() {
        when: "a class with an absent property getter is written"
        write new GetterlessClass()
        
        then: "an exception with a detail message indicating the missing property is expected"
        def ex = thrown ObjectWriterException
        ex.message == "Could not find getter for property value"
    }
    
    def "handling a throwing getter"() {
        when: "a class with an exception causing getter is written"
        write new ThrowingGetterClass()
        
        then: "an exception with a detail message indicating the cause is expected"
        def ex = thrown ObjectWriterException
        ex.message == "Could not invoke property getter"
    }
    
    def "absent required properties are handled"() {
        when: "a class with all required properties, but without values, is written"
        write new AllRequiredClass()
        
        then: "an exception with a detail message indicating the first violating property is expected"
        def ex = thrown ValidationException
        ex.message == "Missing required property value"
    }
    
    def "absent optional properties are handled"() {
        when: "a class with all optional properties, without any values, is written"
        write new AllNonRequiredClass()
        
        then: "it is expected that no validation specific exceptions are thrown"
        notThrown ValidationException
    }
    
    def "string literal property is written"() {
        given: "a class with a string property populated with a value"
        def object = new StringLiteralClass()
        object.value = "hello"
        
        when: "the object is written"
        def output = write object
        
        then: "the output contains the statement"
        output == rdf("""ex:1 ex:value "hello" .""")
    }
    
    def "int literal property is written"() {
        given: "a class with a int primitive property populated with a value"
        def object = new IntLiteralClass()
        object.value = 42
        
        when: "the object is written"
        def output = write object
        
        then: "the output contains the primitive value with the appropriate datatype"
        output == rdf("""ex:1 ex:value "42"^^<http://www.w3.org/2001/XMLSchema#int> .""")
    }
    
    def "url property types are written"() {
        given: "the value on the object instance is populated"
        object.value = val
        
        when: "the object is written"
        def output = write object
        
        then: "the output contains the url value"
        output == rdf("ex:1 ex:value <$Constants.NAMESPACE> .") 
        
        where: "different types of URL interpretable types"
        object                       | val
        new UrlPropertyClass()       | new URL(Constants.NAMESPACE)
        new UriPropertyClass()       | URI.create(Constants.NAMESPACE)
        new StringUrlPropertyClass() | Constants.NAMESPACE
    }
    
    def "array properties are written"() {
        given: "a class with an array property populated with sequential values"
        def object = new PrimitiveArrayClass()
        object.value = [ "first", "second", "third" ]
        
        when: "the object is written"
        def output = write object
        
        then: "the output contains the sequential values is order"
        output == rdf("""ex:1 ex:value "first", "second", "third" .""")
    }
    
    def "collection properties are written"() {
        given: "a class with a collection property populated with sequential values"
        def object = new StringCollectionClass()
        object.value = [ "first", "second", "third" ]
        
        when: "the object is written"
        def output = write object
        
        then: "the output contains the sequential values in order"
        output == rdf("""ex:1 ex:value "first", "second", "third" .""")
    }
    
    def "handling a nested type with an absent subject property"() {
        given: "a class with a nested type missing a subject property"
        def object = new NestingNonSubjectAnnotationClass()
        object.nested = new BeanExampleClass()
        
        when: "the object is written"
        write object
        
        then: "an exception with a detail message indicating the missing annotation is expected"
        def ex = thrown ObjectWriterException
        ex.message == "Could not find @Subject annotation"
    }
    
    def "handling a nested type level subject annotation"() {
        given: "a class containing a nested type with a class level annotation"
        def object = new NestingTypeSubjectClass()
        object.nested = new TypeSubjectClass()
        
        when: "the object is written"
        def output = write object
        
        then: "the output contains the nested type with the expected subject value"
        output == rdf("ex:1 ex:nested <http://example.com/nested/1> .")
    }
    
    def "handling a nested throwing subject property getter"() {
        given: "a class with a nested type with a throwing property getter method"
        def object = new NestingThrowingSubjectGetterClass()
        object.nested = new ThrowingSubjectGetterClass()
        
        when: "the object is written"
        write object
        
        then: "an exception with a detail message indicating the causing property is expected"
        def ex = thrown ObjectWriterException
        ex.message == "Could not invoke subject property getter"
    }
    
    def "nested stuff"() {
        given: "a class with a nested type that allows for dynamic subject values"
        def object = new NestingSettableSubjectClass()
        object.nested = new SettableSubjectClass()
        object.nested.subject = "http://example.com/foo"
        
        when: "the object is written"
        def output = write object
        
        then: "the output contains the expected dynamic value"
        output == rdf("ex:1 ex:nested ex:foo .")
    }
    
    def "writing a java.util.Optional getter property"() {
        given:
        new SimpleModule()
                .addDatatypeWrapperStrategy(new JavaOptionalWrapperStrategy())
                .setup(writer)
        and:
        def object = new JavaOptionalStringClass()
        object.value = "example"
        
        when:
        def output = write object
        
        then:
        output == rdf("""ex:1 ex:value "example" .""")
    }
    
    def "wiring a guava Optional getter property"() {
        given:
        new SimpleModule()
                .addDatatypeWrapperStrategy(new GuavaOptionalWrapperStrategy())
                .setup(writer)
        and:
        def object = new GuavaOptionalStringClass()
        object.value = "example"
        
        when:
        def output = write object
        
        then:
        output == rdf("""ex:1 ex:value "example" .""")
    }
}
