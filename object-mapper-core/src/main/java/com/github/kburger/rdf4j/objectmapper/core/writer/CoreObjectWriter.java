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
package com.github.kburger.rdf4j.objectmapper.core.writer;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import com.github.kburger.rdf4j.objectmapper.api.writer.ObjectWriter;
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer;

/**
 * Core implementation of the {@link ObjectWriter} functionality.
 */
public class CoreObjectWriter extends AbstractWriterBase<Writer> {
    private final List<Namespace> namespaces;
    
    /**
     * Constructs a new writer with the given analyzer.
     * @param analyzer the shared analyzer.
     */
    public CoreObjectWriter(ClassAnalyzer analyzer) {
        super(analyzer);
        namespaces = new ArrayList<>();
    }
    
    @Override
    public void registerNamespace(Namespace namespace) {
        namespaces.add(namespace);
    }
    
    @Override
    public <T> void write(Writer writer, T object, CharSequence subject, RDFFormat format) {
        var analysis = analyzer.analyze(object.getClass());
        
        var builder = new ModelBuilder();
        namespaces.forEach(builder::setNamespace);
        var model = builder.build();
        
        writeInternal(model, analysis, object, FACTORY.createIRI(subject.toString()));
        
        Rio.write(model, writer, format);
    }
}
