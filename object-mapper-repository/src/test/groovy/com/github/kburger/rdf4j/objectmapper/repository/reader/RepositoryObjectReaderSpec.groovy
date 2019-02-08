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
package com.github.kburger.rdf4j.objectmapper.repository.reader

import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.util.Repositories
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.sail.memory.MemoryStore
import com.github.kburger.rdf4j.objectmapper.api.reader.StringValueConverter
import com.github.kburger.rdf4j.objectmapper.core.SimpleModule
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.core.reader.argument.SingleArgumentStrategy
import com.github.kburger.rdf4j.objectmapper.core.reader.instance.BeanInstanceStrategy
import com.github.kburger.rdf4j.objectmapper.test.Constants
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import spock.lang.Shared
import spock.lang.Specification

class RepositoryObjectReaderSpec extends Specification {
    @Shared analyzer = new ClassAnalyzer()
    
    /** Subject under test. */
    def reader = new RepositoryObjectReader(analyzer);
    /** Repository instance. */
    def repository = new SailRepository(new MemoryStore())
    
    def write = { params ->
        Repositories.get(repository, { conn ->
            def iri = conn.valueFactory.&createIRI
            def literal = conn.valueFactory.&createLiteral
            conn.add(
                iri(params["s"] ?: Constants.SUBJECT),
                iri(params["p"] ?: Constants.PREDICATE_VALUE),
                literal(params["o"]))
        })
    }
    
    def read = { clazz, subject = Constants.SUBJECT ->
        reader.read(repository, clazz, subject, RDFFormat.TURTLE)
    }
    
    def setup() {
        repository.initialize()
        
        def module = new SimpleModule()
                .addInstanceStrategy(new BeanInstanceStrategy.Factory())
                .addArgumentStrategy(new SingleArgumentStrategy.Factory())
                .addValueConverter(String, { it } as StringValueConverter)
        module.setup(reader)
    }
    
    def "reading a basic property class from the repository"() {
        given:
        write o: "example"
        
        when:
        def object = read StringLiteralClass
        
        then:
        object.value == "example"
    }
}
