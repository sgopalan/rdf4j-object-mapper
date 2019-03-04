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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalysis;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectWriterException;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ValidationException;
import com.github.kburger.rdf4j.objectmapper.api.writer.DatatypeWrapperStrategy;
import com.github.kburger.rdf4j.objectmapper.api.writer.ObjectWriter;
import com.github.kburger.rdf4j.objectmapper.core.util.Utils;

public abstract class AbstractWriterBase<W> implements ObjectWriter<W> {
    /** Factory instance. */
    protected static final ValueFactory FACTORY = SimpleValueFactory.getInstance();
    
    /** Class analyzer. */
    protected final ClassAnalyzer analyzer;
    
    protected final Collection<DatatypeWrapperStrategy> datatypeWrappers;
    
    public AbstractWriterBase(ClassAnalyzer analyzer) {
        this.analyzer = analyzer;
        this.datatypeWrappers = new ArrayList<>();
    }
    
    @Override
    public void registerDatatypeWrapperStrategy(DatatypeWrapperStrategy strategy) {
        datatypeWrappers.add(strategy);
    }
    
    /**
     * Internal handling of the object to RDF4J {@link Model} serialization. Can be invoked
     * recursively from handling nested objects.
     * @param model the target sink.
     * @param analysis the target-under-serialization analysis.
     * @param source the source object.
     * @param subject the current object's subject.
     */
    protected void writeInternal(Model model, ClassAnalysis analysis, Object source, IRI subject) {
        analysis.getType()
                .map(type -> type.getAnnotation().value())
                .ifPresent(types -> {
                    Arrays.stream(types)
                            .map(FACTORY::createIRI)
                            .forEach(type -> model.add(subject, RDF.TYPE, type));
                });
        
        for (var property : analysis.getPredicates()) {
            var name = property.getName().orElseThrow();
            
            var getter = property.getGetter().orElseThrow(() ->
                    new ObjectWriterException("Could not find getter for property " + name));
            
            final Optional<Object> content;
            try {
                var intermediate = getter.invoke(source);
                
                content = datatypeWrappers.stream()
                        .filter(strategy -> strategy.supports(intermediate))
                        .findFirst()
                        .filter(strat -> strat.isPresent(intermediate))
                        .map(strat -> strat.unwrap(intermediate))
                        .or(() -> Optional.ofNullable(intermediate));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // TODO consider configuration flags to indicate strictness, and continue the loop if set to non-strict
                throw new ObjectWriterException("Could not invoke property getter");
            }
            
            var annotation = property.getAnnotation();
            
            if (!content.isPresent()) {
                if (annotation.required()) {
                    throw new ValidationException("Missing required property " + name);
                } else {
                    //TODO log debug message
                    continue;
                }
            }
            
            var predicate = FACTORY.createIRI(annotation.value());
            var value = content.orElseThrow();
            
            if (value.getClass().isArray()) {
                value = Arrays.asList((Object[])value);
            }
            
            if (value instanceof Iterable) {
                for (var element : (Iterable<?>)value) {
                    writeStatement(model, annotation, subject, predicate, element);
                }
            } else {
                writeStatement(model, annotation, subject, predicate, value);
            }
        }
    }
    
    /**
     * Writes a single statement to the {@code model} sink.
     * @param model the target sink.
     * @param annotation the {@link Predicate} annotation for the current statement.
     * @param subject the current object's subject.
     * @param predicate the current statement's predicate IRI.
     * @param value the current statement's object value. Can be a nested (complex) object.
     */
    private void writeStatement(Model model, Predicate annotation, IRI subject, IRI predicate, Object value) {
        var childType = value.getClass();
        
        if (!Utils.isSystemClass(childType)) {
            var analysis = analyzer.analyze(childType);
            
            var subjectProperty = analysis.getSubject().orElseThrow(() ->
                    new ObjectWriterException("Could not find @Subject annotation"));
            
            var subjectAnnotation = subjectProperty.getAnnotation();
            var getter = subjectProperty.getGetter();
            
            final Optional<Object> subjectContent;
            
            if (getter.isEmpty() && !subjectAnnotation.value().isBlank()) {
                // type level annotation
                subjectContent = Optional.of(subjectAnnotation.value());
            } else {
                // property/method level annotation
                var method = getter.orElseThrow(() -> new ObjectWriterException(""));
                try {
                    subjectContent = Optional.ofNullable(method.invoke(value));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new ObjectWriterException("Could not invoke subject property getter", e);
                }
            }
            
            var subjectValue = subjectContent.orElseThrow(() -> new ObjectWriterException(""));
            
            var sb = new StringBuilder();
            
            if (subjectAnnotation.relative()) {
                sb.append(subject);
                sb.append('#');
            }
            sb.append(subjectValue);
            
            var childSubject = FACTORY.createIRI(sb.toString());
            
            model.add(subject, predicate, childSubject);
            
            writeInternal(model, analysis, value, childSubject);
        } else if (annotation.literal()) {
            var datatype = FACTORY.createIRI(annotation.datatype());
            var literal = FACTORY.createLiteral(value.toString(), datatype);
            
            model.add(subject, predicate, literal);
        } else {
            // assume we are dealing with an IRI
            var object = FACTORY.createIRI(value.toString());
            
            model.add(subject, predicate, object);
        }
    }
}
