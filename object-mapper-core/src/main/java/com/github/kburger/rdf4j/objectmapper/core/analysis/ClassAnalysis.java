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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import com.github.kburger.rdf4j.objectmapper.annotations.Subject;
import com.github.kburger.rdf4j.objectmapper.annotations.Type;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;

/**
 * An analysis of a class. The analysis contains metadata about the possible annotations on parts of
 * the target type.
 */
public class ClassAnalysis {
    /** Target class's {@link Type} property analysis. */
    private PropertyAnalysis<Type> type;
    /** Target class's {@link Subject} property analysis. */
    private PropertyAnalysis<Subject> subject;
    /** Target class's {@link Predicate} properties analysis. */
    private Collection<PropertyAnalysis<Predicate>> predicates;
    
    /** Private constructor. */
    private ClassAnalysis() {
        predicates = new ArrayList<>();
    }
    
    /**
     * Returns the class's {@link Type} property, which could be absent.
     * @return an {@link Optional} containing the {@code Type} property; otherwise {@link Optional#empty()}.
     */
    public Optional<PropertyAnalysis<Type>> getType() {
        return Optional.ofNullable(type);
    }
    
    /**
     * Returns the class's {@link Subject} property, which could be absent.
     * @return an {@link Optional} containing the {@code Subject} property; otherwise {@link Optional#empty()}.
     */
    public Optional<PropertyAnalysis<Subject>> getSubject() {
        return Optional.ofNullable(subject);
    }
    
    /**
     * Returns a collection of {@link Predicate} properties, which could be empty.
     * @return a {@code Collection} of {@code Predicate} properties.
     */
    public Collection<PropertyAnalysis<Predicate>> getPredicates() {
        return predicates;
    }
    
    /**
     * Creates a {@link Builder} instance to construct a new {@code ClassAnalysis} object.
     * @return builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder pattern type that allows for easy construction of immutable {@link ClassAnalysis}
     * instances. Each property method is guarded to prevent accidental multiple invocations.
     */
    public static class Builder {
        /** The {@code ClassAnalysis} instance under construction. */
        private ClassAnalysis analysis;
        
        /**
         * Private constructor. Instances are to be created through the {@link ClassAnalysis#builder()}
         * method.
         */
        private Builder() {
            analysis = new ClassAnalysis();
        }
        
        /**
         * Sets the {@link ClassAnalysis#type} property on the {@link #analysis} under construction.
         * @param type the target's {@link Type}.
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the type has already been set.
         */
        public Builder type(PropertyAnalysis<Type> type) {
            if (analysis.type != null) {
                throw new IllegalStateException("Type is already set");
            }
            
            analysis.type = type;
            
            return this;
        }
        
        /**
         * Sets the {@link ClassAnalysis#subject} property on the {@link #analysis} under construction.
         * @param subject the target's {@link Subject}.
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the subject has already been set.
         */
        public Builder subject(PropertyAnalysis<Subject> subject) {
            if (analysis.subject != null) {
                throw new IllegalStateException("Subject is already set");
            }
            
            analysis.subject = subject;
            
            return this;
        }
        
        /**
         * Adds a {@link ClassAnalysis#predicates} property on the {@link #analysis} under construction.
         * @param predicate a {@link Predicate} on the target.
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the {@code Predicate} has already been added.
         */
        public Builder predicate(PropertyAnalysis<Predicate> predicate) {
            if (analysis.predicates.contains(predicate)) {
                throw new IllegalStateException("Predicate is already added");
            }
            
            analysis.predicates.add(predicate);
            
            return this;
        }
        
        /**
         * Finishes the construction of the {@link #analysis} under construction and returns the
         * newly created immutable instance.
         * @return an immutable {@code ClassAnalysis} instance.
         */
        public ClassAnalysis build() {
            return analysis;
        }
    }
}
