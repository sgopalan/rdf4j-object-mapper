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
package com.github.kburger.rdf4j.objectmapper.core.reader;

import java.io.IOException;
import java.io.Reader;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException;

public class CoreObjectReader extends AbstractReaderBase<Reader> {
    public CoreObjectReader(ClassAnalyzer analyzer) {
        super(analyzer);
    }
    
    @Override
    public <T> T read(Reader reader, Class<T> clazz, CharSequence subject, RDFFormat format) {
        var analysis = analyzer.analyze(clazz);
        
        final Model model;
        try {
            model = Rio.parse(reader, subject.toString(), format);
        } catch (IOException | RDFParseException e) {
            throw new ObjectReaderException("Could not read input data", e);
        }
        
        return readInternal(model, clazz, analysis, FACTORY.createIRI(subject.toString()));
    }
}
