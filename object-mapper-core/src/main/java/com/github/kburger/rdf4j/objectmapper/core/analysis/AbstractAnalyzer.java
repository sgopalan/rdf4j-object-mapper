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
package com.github.kburger.rdf4j.objectmapper.core.analysis;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.github.kburger.rdf4j.objectmapper.annotations.Subject;
import com.github.kburger.rdf4j.objectmapper.annotations.Type;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;

/**
 * Base class for analyzers.
 */
public abstract class AbstractAnalyzer {
    /**
     * Checks for type level {@link Type} annotations and adds it to the analysis under construction
     * if it is present.
     * @param clazz the target type under analysis.
     * @param builder the analysis instance under construction.
     */
    protected <T> void addTypeProperty(Class<T> clazz, ClassAnalysis.Builder builder) {
        checkAnnotation(clazz, Type.class, annotation -> {
            var type = annotation.value();
            
            if (type.length == 0) {
                throw new AnalysisException("Type level @Type annotation is missing a value");
            }
                    
            return true;
        }, builder::type);
    }
    
    /**
     * Checks for type level {@link Subject} annotations and adds it to the analysis under
     * construction if it is present.
     * @param clazz the target type under analysis.
     * @param builder the analysis instance under construction.
     */
    protected <T> void addSubjectProperty(Class<T> clazz, ClassAnalysis.Builder builder) {
        checkAnnotation(clazz, Subject.class, annotation -> {
            var value = annotation.value();
            
            if (value.isBlank()) {
                throw new AnalysisException("@Subject attribute value does not contain a valid IRI");
            }
            
            return true;
        }, builder::subject);
    }
    
    /**
     * Convenience method for checking the existence of type level annotations.
     * @param <T> the target type under analysis.
     * @param <A> the annotation type to check for.
     * @param clazz the target type under analysis.
     * @param annotationType the annotation type to check for.
     * @param check typically a lambda providing some checking logic.
     * @param builderMethod method reference to the matching {@link ClassAnalysis.Builder} method.
     */
    private <T, A extends Annotation> void checkAnnotation(Class<T> clazz, Class<A> annotationType,
            Predicate<A> check, Consumer<PropertyAnalysis<A>> builderMethod) {
        Optional.ofNullable(clazz.getAnnotation(annotationType))
                .filter(check)
                .map(annotation -> PropertyAnalysis.<A>builder()
                        .annotation(annotation)
                        .build())
                .ifPresent(builderMethod);
    }
}
