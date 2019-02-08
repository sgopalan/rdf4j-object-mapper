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
package com.github.kburger.rdf4j.objectmapper.repository.analysis;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalysis;
import com.github.kburger.rdf4j.objectmapper.core.analysis.ClassAnalyzer;

public class QueryGenerator {
    public static final String VAR_SUBJECT = "s";
    public static final String VAR_OBJECT = "o";
    public static final String INDENT = "  ";
    
    private final ClassAnalyzer analyzer;
    private final Map<Class<?>, String> cache;
    
    public QueryGenerator(ClassAnalyzer analyzer) {
        this.analyzer = analyzer;
        cache = new HashMap<>();
    }
    
    public <T> String generate(Class<T> clazz) {
        return cache.computeIfAbsent(clazz, this::generateQuery);
    }
    
    private <T> String generateQuery(Class<T> clazz) {
        var analysis = analyzer.analyze(clazz);
        var subject = '?' + VAR_SUBJECT;
        
        var query = new StringBuilder(256)
                .append("CONSTRUCT {\n")
                .append(getPattern(subject, analysis, new AtomicInteger(1), false))
                .append("\n} WHERE {\n")
                .append(getPattern(subject, analysis, new AtomicInteger(1), true))
                .append("\n}\n")
                .toString();
        
        return query;
    }
    
    private String getPattern(String subjectVar, ClassAnalysis analysis, AtomicInteger count, boolean withConstraints) {
        var patterns = new StringBuilder(128);
        
        var propertyPattern = analysis.getPredicates()
                .stream()
                .map(property -> {
                    var annotation = property.getAnnotation();
                    var pattern = new StringBuilder(64);
                    
                    pattern.append(INDENT);
                    
                    if (!annotation.required() && withConstraints) {
                        pattern.append("OPTIONAL { ");
                    }
                    
                    pattern.append(subjectVar)
                            .append(" <")
                            .append(annotation.value())
                            .append("> ");
                    
                    var objectVar = '?' + VAR_OBJECT + count.getAndIncrement();
                    pattern.append(objectVar);
                    
                    if (property.isNested()) {
                        pattern.append(" .\n");
                        
                        var nested = property.getGetter()
                                .map(Method::getReturnType)
                                .map(analyzer::analyze)
                                .orElseThrow();
                        var nestedPattern = getPattern(objectVar, nested, count, withConstraints);
                        pattern.append(nestedPattern);
                    }
                    
                    if (!annotation.required() && withConstraints) {
                        pattern.append(" }");
                    }
                    
                    return pattern.toString();
                })
                .collect(Collectors.joining(" .\n"));
        
        patterns.append(propertyPattern);
        
        return patterns.toString();
    }
}
