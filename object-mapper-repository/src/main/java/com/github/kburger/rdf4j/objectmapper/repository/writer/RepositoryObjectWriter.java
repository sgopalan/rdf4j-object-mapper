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
package com.github.kburger.rdf4j.objectmapper.repository.writer;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.core.writer.AbstractWriterBase;

public class RepositoryObjectWriter extends AbstractWriterBase<Repository> {
    public RepositoryObjectWriter(ClassAnalyzer analyzer) {
        super(analyzer);
    }
    
    @Override
    public <T> void write(Repository repository, T object, CharSequence subject, RDFFormat format) {
        var model = new ModelBuilder().build();
        
        var analysis = analyzer.analyze(object.getClass());
        
        writeInternal(model, analysis, object, FACTORY.createIRI(subject.toString()));
        
        Repositories.consume(repository, connection -> connection.add(model));
    }
}
