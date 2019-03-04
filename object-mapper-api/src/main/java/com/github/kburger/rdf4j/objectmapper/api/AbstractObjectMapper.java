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
package com.github.kburger.rdf4j.objectmapper.api;

import org.eclipse.rdf4j.rio.RDFFormat;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.api.reader.ObjectReader;
import com.github.kburger.rdf4j.objectmapper.api.writer.ObjectWriter;

public abstract class AbstractObjectMapper<R, W> implements ObjectMapper<R, W> {
    protected final ClassAnalyzer analyzer;
    protected final ObjectReader<R> reader;
    protected final ObjectWriter<W> writer;
    
    protected AbstractObjectMapper(ClassAnalyzer analyzer, ObjectReader<R> reader, ObjectWriter<W> writer) {
        this.analyzer = analyzer;
        this.reader = reader;
        this.writer = writer;
    }
    
    @Override
    public void addModule(Module module) {
        module.setup(analyzer);
        module.setup(reader);
        module.setup(writer);
    }
    
    @Override
    public <T> T read(R source, Class<T> clazz, CharSequence subject, RDFFormat format) {
        return reader.read(source, clazz, subject, format);
    }
    
    @Override
    public <T> void write(W sink, T object, CharSequence subject, RDFFormat format) {
        writer.write(sink, object, subject, format);
    }
}
