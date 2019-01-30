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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectReaderException;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ValidationException;
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy;
import com.github.kburger.rdf4j.objectmapper.api.reader.ObjectReader;
import com.github.kburger.rdf4j.objectmapper.api.reader.ValueConverter;
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalysis;
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer;

public class CoreObjectReader implements ObjectReader<Reader> {
    private static final ValueFactory FACTORY = SimpleValueFactory.getInstance();
    
    private final ClassAnalyzer analyzer;
    
    private Collection<InstanceStrategy.Factory> instanceStrategies;
    private Collection<ArgumentStrategy.Factory> argumentStrategies;
    private Map<Class<?>, ValueConverter<?>> converters;
    
    public CoreObjectReader(ClassAnalyzer analyzer) {
        this.analyzer = analyzer;
        
        instanceStrategies = Collections.emptyList();
        argumentStrategies = Collections.emptyList();
        converters = Collections.emptyMap();
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
    
    private <T> T readInternal(Model model, Class<T> clazz, ClassAnalysis analysis, IRI subject) {
        var instanceStrategy = instanceStrategies.stream()
                .filter(factory -> factory.supports(clazz))
                .map(InstanceStrategy.Factory::create)
                .findFirst()
                .orElseThrow(() -> new ObjectReaderException("Could not find a supporting instance strategy"));
        
        instanceStrategy.initialize(clazz);
        
        // set type
        analysis.getType().ifPresent(type -> {
            type.getGetter().ifPresent(getter -> {
                var types = model.filter(subject, RDF.TYPE, null);
                
                var argumentStrategy = createArgumentStrategy(getter, types.size());
                
                types.stream()
                        .map(Statement::getObject)
                        .map(obj -> readObject(model, false, argumentStrategy.getType(), obj))
                        .forEach(argumentStrategy::addValue);
                
                instanceStrategy.addProperty(type, argumentStrategy.build());
            });
        });
        
        // set subject
        analysis.getSubject().ifPresent(subjectProperty -> {
            subjectProperty.getGetter().ifPresent(getter -> {
                var argumentStrategy = createArgumentStrategy(getter, 1);
                
                final Value subjectValue;
                if (subjectProperty.getAnnotation().relative()) {
                    subjectValue = FACTORY.createLiteral(subject.getLocalName());
                } else {
                    subjectValue = subject;
                }
                
                argumentStrategy.addValue(readObject(model, false, argumentStrategy.getType(), subjectValue));
                
                instanceStrategy.addProperty(subjectProperty, argumentStrategy.build());
            });
        });
        
        for (var property : analysis.getPredicates()) {
            var annotation = property.getAnnotation();
            var predicate = FACTORY.createIRI(annotation.value());
            
            var statements = model.filter(subject, predicate, null);
            
            if (statements.isEmpty()) {
                if (annotation.required()) {
                    throw new ValidationException("Required property is missing");
                } else {
                    continue;
                }
            } else {
                if (annotation.readonly()) {
                    throw new ValidationException("Provided property is read-only");
                }
            }
            
            var argumentStrategy = createArgumentStrategy(property.getGetter().get(), statements.size());
            
            statements.stream()
                    .map(Statement::getObject)
                    .map(obj -> readObject(model, property.isNested(), argumentStrategy.getType(), obj))
                    .forEach(argumentStrategy::addValue);
            
            argumentStrategy.addInstanceProperty(instanceStrategy, property);
        }
        
        return clazz.cast(instanceStrategy.build());
    }
    
    private ArgumentStrategy<?> createArgumentStrategy(Method getter, int size) {
        return argumentStrategies.stream()
                .filter(factory -> factory.supports(getter.getReturnType()))
                .findFirst()
                .map(factory -> factory.create(getter, size))
                .orElseThrow(() -> new ObjectReaderException("Could not find a supporting argument strategy"));
    }
    
    private <T> Object readObject(Model model, boolean nested, Class<T> targetType, Value object) {
        if (nested) {
            var analysis = analyzer.analyze(targetType);
            return readInternal(model, targetType, analysis, (IRI)object);
        } else {
            if (!converters.containsKey(targetType)) {
                throw new ObjectReaderException("Could not find a value converter");
            }
            
            var converter = converters.get(targetType);
            return converter.convert(object);
        }
    }
}
