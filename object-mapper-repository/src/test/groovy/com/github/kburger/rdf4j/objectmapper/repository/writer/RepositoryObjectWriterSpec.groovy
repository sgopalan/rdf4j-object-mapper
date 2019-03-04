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
package com.github.kburger.rdf4j.objectmapper.repository.writer

import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.RepositoryConnection
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.sail.memory.MemoryStore
import com.github.kburger.rdf4j.objectmapper.api.reader.StringValueConverter
import com.github.kburger.rdf4j.objectmapper.core.SimpleModule
import com.github.kburger.rdf4j.objectmapper.core.analysis.CoreClassAnalyzer
import com.github.kburger.rdf4j.objectmapper.core.reader.argument.SingleArgumentStrategy
import com.github.kburger.rdf4j.objectmapper.core.reader.instance.BeanInstanceStrategy
import com.github.kburger.rdf4j.objectmapper.repository.reader.RepositoryObjectReader
import com.github.kburger.rdf4j.objectmapper.test.Constants
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import spock.lang.Shared
import spock.lang.Specification

class RepositoryObjectWriterSpec extends Specification {
    @Shared analyzer = new CoreClassAnalyzer()
    
    /** Subject under test. */
    def writer = new RepositoryObjectWriter(analyzer)
    
    def repository = new SailRepository(new MemoryStore())
    
    def setup() {
        repository.initialize()
    }
    
    def "writing a basic property class to the repository"() {
        given:
        def bean = new StringLiteralClass()
        bean.value = "example"
        and:
        def conn = Mock(RepositoryConnection)
        def repo = Mock(Repository) {
            getConnection() >> conn 
        }
        
        when:
        writer.write(repo, bean, Constants.SUBJECT, RDFFormat.TURTLE)
        
        then:
        with (conn) {
            1 * conn.add(_ as Model)
        }
    }
    
    def "happy flow"() {
        given:
        def bean = new StringLiteralClass()
        bean.value = "example"
        and:
        def reader = new RepositoryObjectReader(analyzer)
        def module = new SimpleModule()
                .addInstanceStrategy(new BeanInstanceStrategy.Factory())
                .addArgumentStrategy(new SingleArgumentStrategy.Factory())
                .addValueConverter(String, { it } as StringValueConverter)
        module.setup(reader)
        
        when:
        writer.write(repository, bean, Constants.SUBJECT, RDFFormat.TURTLE)
        
        then:
        def result = reader.read(repository, StringLiteralClass, Constants.SUBJECT, RDFFormat.TURTLE)
        result.value == "example"
    }
}
