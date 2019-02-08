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
package com.github.kburger.rdf4j.objectmapper.repository.reader;

import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.core.reader.AbstractReaderBase;
import com.github.kburger.rdf4j.objectmapper.repository.analysis.QueryGenerator;

public class RepositoryObjectReader extends AbstractReaderBase<Repository> {
    private final QueryGenerator generator;
    
    public RepositoryObjectReader(ClassAnalyzer analyzer) {
        super(analyzer);
        generator = new QueryGenerator(analyzer);
    }
    
    @Override
    public <T> T read(Repository repository, Class<T> clazz, CharSequence subject, RDFFormat format) {
        return Repositories.getNoTransaction(repository, connection -> {
            var analysis = analyzer.analyze(clazz);
            var query = generator.generate(clazz);
            var graphQuery = connection.prepareGraphQuery(query);
            var iri = connection.getValueFactory().createIRI(subject.toString());
            
            graphQuery.setBinding(QueryGenerator.VAR_SUBJECT, iri);
            
            var model = QueryResults.asModel(graphQuery.evaluate());
            
            return readInternal(model, clazz, analysis, iri);
        });
    }
}
