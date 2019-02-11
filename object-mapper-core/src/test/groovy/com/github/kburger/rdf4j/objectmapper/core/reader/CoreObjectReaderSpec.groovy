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
package com.github.kburger.rdf4j.objectmapper.core.reader

import org.eclipse.rdf4j.rio.RDFFormat
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ValidationException
import com.github.kburger.rdf4j.objectmapper.api.reader.StringValueConverter
import com.github.kburger.rdf4j.objectmapper.core.SimpleModule
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.core.reader.argument.CollectionArgumentStrategy
import com.github.kburger.rdf4j.objectmapper.core.reader.argument.SingleArgumentStrategy
import com.github.kburger.rdf4j.objectmapper.core.reader.instance.BeanInstanceStrategy
import com.github.kburger.rdf4j.objectmapper.test.Constants
import com.github.kburger.rdf4j.objectmapper.test.BeanClasses.BeanExampleClass
import com.github.kburger.rdf4j.objectmapper.test.BuilderClasses.BuilderExampleClass
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.StringCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.IntLiteralClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.NestingTypeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.NestingClasses.RecursiveNodeClass
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.SettableRelativeSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.SubjectClasses.SettableSubjectClass
import com.github.kburger.rdf4j.objectmapper.test.TypeClasses.SettableTypeClass
import com.github.kburger.rdf4j.objectmapper.test.ValidationClasses.AllNonRequiredClass
import com.github.kburger.rdf4j.objectmapper.test.ValidationClasses.MixedRequiredClass
import com.github.kburger.rdf4j.objectmapper.test.ValidationClasses.ReadOnlyPropertyClass
import spock.lang.Shared
import spock.lang.Specification

class CoreObjectReaderSpec extends Specification {
    @Shared analyzer = new ClassAnalyzer()
    
    /** Subject under test. */
    def reader = new CoreObjectReader(analyzer)
    
    // Convenience closure for reading a class from input rdf.
    def read = { clz, input = Constants.EXAMPLE_RDF, subject = Constants.SUBJECT, format = RDFFormat.TURTLE ->
        reader.read(new StringReader(input), clz, subject, format)
    }
    
    // Convenience closure for shorthand rdf input construction
    def rdf = { ...lines -> Constants.RDF_HEADER + lines.join("\n") }
    
    def setup() {
        def module = new SimpleModule()
                .addInstanceStrategy(new BeanInstanceStrategy.Factory())
                .addArgumentStrategy(new SingleArgumentStrategy.Factory())
                .addValueConverter(String, { it } as StringValueConverter)
        
        module.setup(reader)
    }
    
    def "reading invalid input"() {
        given: "an invalid rdf document"
        def input = "this is not valid rdf data."
        
        when: "the invalid input data is read"
        read(BeanExampleClass, input)
        
        then: "an exception is thrown"
        def ex = thrown(ObjectReaderException)
        ex.message == "Could not read input data"
    }
    
    def "reading an unsupported instance type"() {
        when: "a target class of an unsupported instance type is read"
        read BuilderExampleClass
        
        then: "an exception indicating the unsupported instance type is thrown"
        def ex = thrown(ObjectReaderException)
        ex.message == "Could not find a supporting instance strategy"
    }
    
    def "reading a type with a property setter available"() {
        given: "an input document defining a type"
        def input = rdf "ex:1 a ex:Type ."
        
        when: "the target class is read from input"
        def bean = read SettableTypeClass, input
        
        then: "the expected type value is present"
        bean.type == Constants.TYPE
    }
    
    def "reading a subject with a property setter available"() {
        when: "the target class is read from input"
        def bean = read SettableSubjectClass
        
        then: "the expected absolute subject value is present"
        bean.subject == Constants.SUBJECT
    }
    
    def "reading a relative subject with a property setter available"() {
        given: "an input document simulating a nested subject fragment"
        def input = rdf """<$Constants.SUBJECT#x> ex:value "example" ."""
        
        when: "the target class is read from input with the simulated subject as the root subject"
        def bean = read SettableRelativeSubjectClass, input, "$Constants.SUBJECT#x"
        
        then: "the expected relative subject is present"
        bean.subject == "x"
    }
    
    def "reading an exemplar Bean type class"() {
        when: "the target class is read from input"
        def bean = read BeanExampleClass
        
        then: "the expected property value is present"
        bean.value == "example"
    }
    
    def "handling an absent required property"() {
        given: "an input document that does not define a required property (ex:value)"
        def input = rdf "ex:1 ex:foo ex:bar ."
        
        when: "the target class is read from input"
        read BeanExampleClass, input
        
        then: "an exception is expected with the appropriate detail message"
        def ex = thrown(ValidationException)
        ex.message == "Required property is missing"
    }
    
    def "handling an absent optional property"() {
        given: "an input document that does not define an optional property (ex:value)"
        def input = rdf "ex:1 a ex:Type ."
        
        when: "the target class is read from input"
        read AllNonRequiredClass, input
        
        then: "the absent optional property does not trigger a validation exception"
        notThrown ValidationException
    }
    
    def "handling a present optional property"() {
        given: "an input document that defines both required and optional properties"
        def input = rdf """ex:1 ex:value "example" ;""",
                """ex:description "example description" ."""
        
        when: "the target class is read from input"
        def bean = read MixedRequiredClass, input
        
        then: "both required and optional property values are present"
        with (bean) {
            value == "example"
            description == "example description"
        }
    }
    
    def "handling a readonly property"() {
        when: "the target class is read from input"
        read ReadOnlyPropertyClass
        
        then: "an exception is expected with the appropriate detail message"
        def ex = thrown(ValidationException)
        ex.message == "Provided property is read-only"
    }
    
    def "reading an unsupported argument type"() {
        when: "the target class, which requires a specific argument strategy to be present, is read from input"
        read StringCollectionClass
        
        then: "an exception is expected detailing the need for a specific argument strategy"
        def ex = thrown(ObjectReaderException)
        ex.message == "Could not find a supporting argument strategy"
    }
    
    def "reading a supported argument type"() {
        given: "a module providing the supporting argument strategy"
        def module = new SimpleModule()
                .addArgumentStrategy(new CollectionArgumentStrategy.Factory())
        module.setup(reader)
        
        when: "the target class, which requires a specific argument strategy to be present, is read from input"
        def bean = read StringCollectionClass
        
        then: "the expected property value is correctly read and is present"
        bean.value == [ "example" ]
    }
    
    def "reading an unsupported value type"() {
        given: "an input document that defines an integer type value"
        def input = rdf "ex:1 ex:value 1 ."
        
        when: "the target class is read from input"
        read IntLiteralClass, input
        
        then: "an exception is expected detailing the need for a specific value converter"
        def ex = thrown(ObjectReaderException)
        ex.message == "Could not find a value converter for type java.lang.Integer"
    }
    
    def "reading a supported value converter"() {
        given: "a module providing the supporting value converter"
        def module = new SimpleModule()
                .addValueConverter(Integer, { Integer.parseInt(it) } as StringValueConverter)
        module.setup(reader)
        and: "an input document that defines an integer type value"
        def input = rdf "ex:1 ex:value 1 ."
        
        when: "the target class is read from input"
        def bean = read IntLiteralClass, input
        
        then: "the expected property value is correctly read and is present"
        bean.value == 1
    }
    
    def "reading a nested class"() {
        given: "an input document that defines a nested class structure"
        def input = rdf """ex:1 ex:nested <${Constants.NAMESPACE}nested/2> .""",
                """<${Constants.NAMESPACE}nested/2> a ex:Type ."""
        
        when: "the target class is read from input"
        def bean = read NestingTypeSubjectClass, input
        
        then: "the expected nested structure is present"
        bean.nested != null
    }
    
    def "reading a recursive nested class"() {
        given: "an input document that defines a nested (recursive) structure"
        def input = rdf "ex:1 ex:node ex:2 .",
                "ex:2 ex:node ex:3 .",
                "ex:3 ex:node ex:4 ."
        
        when: "the target class is read from input"
        def bean = read RecursiveNodeClass, input
        
        then: "the expected nested structure is present"
        bean.node != null // ex:1 ex:node ex:2
        bean.node.node != null // ex:2 ex:node ex:3
        bean.node.node.node != null // ex:3 ex:node ex:4
        bean.node.node.node.node == null // ex:4 is not defined
    }
 }
