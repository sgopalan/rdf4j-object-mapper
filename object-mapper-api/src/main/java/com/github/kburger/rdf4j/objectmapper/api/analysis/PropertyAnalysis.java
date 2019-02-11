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
package com.github.kburger.rdf4j.objectmapper.api.analysis;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * An analysis of a type's property. The analysis contains metadata about the property, like its
 * {@link #name name}, its {@link #getter getter} and {@link #setter setter}.
 * @param <A> property's annotation type
 */
public class PropertyAnalysis<A extends Annotation> {
    /** Property's annotation. */
    private A annotation;
    /** Property field. */
    private Field field;
    /** Property's getter, could be absent. */
    private Method getter;
    /** Property's setter, could be absent. */
    private Method setter;
    /** Flag to indicate whether the property contains a nested structure. */
    private Boolean nested;
    
    /** Private constructor. */
    private PropertyAnalysis() {}
    
    /**
     * Returns the property's annotation, which should always be present.
     * @return the annotation instance.
     */
    public A getAnnotation() {
        return annotation;
    }

    /**
     * Returns the underlying property field.
     * @return an {@link Optional} containing the underlying field; otherwise {@link Optional#empty()}.
     */
    public Optional<Field> getField() {
        return Optional.ofNullable(field);
    }
    
    /**
     * Returns the property name.
     * @return an {@link Optional} containing the property name; otherwise {@link Optional#empty()}.
     */
    public Optional<String> getName() {
        return Optional.ofNullable(field)
                .map(Field::getName);
    }
    
    /**
     * Returns the property's getter, which could be absent e.g. if the property exists on a type
     * level.
     * @return an {@link Optional} containing the getter method; otherwise {@link Optional#empty()}.
     */
    public Optional<Method> getGetter() {
        return Optional.ofNullable(getter);
    }
    
    /**
     * Returns the property's setter, which could be absent e.g. if the parent class is immutable.
     * @return an {@link Optional} containing the setter method; otherwise {@link Optional#empty()}.
     */
    public Optional<Method> getSetter() {
        return Optional.ofNullable(setter);
    }
    
    /**
     * Returns a flag to indicate the nature of the property's value: complex structures are
     * considered nested, IRIs and literals are not considered nested.
     * @return
     */
    public boolean isNested() {
        return nested;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(annotation, field, getter, setter, nested);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyAnalysis) {
            var other = (PropertyAnalysis<?>)obj;
            
            return Objects.equals(annotation, other.annotation) &&
                    Objects.equals(field, other.field) &&
                    Objects.equals(getter, other.getter) &&
                    Objects.equals(setter, other.setter) &&
                    Objects.equals(nested, other.nested);
        }
        
        return false;
    }
    
    /**
     * Creates a {@link Builder} instance to construct a new {@code PropertyAnalysis} object. 
     * @return builder instance.
     */
    public static <T extends Annotation> Builder<T> builder() {
        return new Builder<>();
    }
    
    /**
     * Builder pattern type that allows for easy construction of immutable {@link PropertyAnalysis}
     * instances. Each property method is guarded to prevent accidental multiple invocations.
     * @param <T> the annotation type for the {@code PropertyAnalysis} under construction.
     */
    public static class Builder<T extends Annotation> {
        /** The {@code PropertyAnalysis} instance under construction. */
        private PropertyAnalysis<T> property;
        
        /**
         * Private constructor. Instances are to be created through the
         * {@link PropertyAnalysis#builder()} method.
         */
        private Builder() {
            property = new PropertyAnalysis<>();
        }
        
        /**
         * Sets the {@link PropertyAnalysis#annotation} property on the {@link #property instance
         * under construction}.
         * @param annotation the property's annotation
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the property has already been set.
         */
        public Builder<T> annotation(T annotation) {
            if (property.annotation != null) {
                throw new IllegalStateException("Annotation on property is already set");
            }
            
            property.annotation = annotation;
            
            return this;
        }
        
        /**
         * Sets the underlying {@link PropertyAnalysis#field} property on the {@link #property
         * instance under construction}.
         * @param field the property's underlying field.
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the property has already been set.
         */
        public Builder<T> field(Field field) {
            if (property.field != null) {
                throw new IllegalStateException("Field on property is already set");
            }
            
            property.field = field;
            return this;
        }
        
        /**
         * Sets the {@link PropertyAnalysis#getter} property on the {@link #property instance
         * under construction}.
         * @param getter the property's getter method
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the property has already been set.
         */
        public Builder<T> getter(@Nullable Method getter) {
            if (property.getter != null) {
                throw new IllegalStateException("Getter on property is already set");
            }
            
            property.getter = getter;
            
            return this;
        }
        
        /**
         * Sets the {@link PropertyAnalysis#setter} property on the {@link #property instance
         * under construction}.
         * @param setter the property's setter
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the property has already been set.
         */
        public Builder<T> setter(@Nullable Method setter) {
            if (property.setter != null) {
                throw new IllegalStateException("Setter on property is already set");
            }
            
            property.setter = setter;
            
            return this;
        }
        
        /**
         * Sets the {@link PropertyAnalysis#nested} property on the {@link #property instance
         * under construction}.
         * @param nested flag to indicates the complex nature of the property's value
         * @return the {@code Builder} instance, allows for method chaining.
         * @throws IllegalStateException if the property has already been set.
         */
        public Builder<T> nested(boolean nested) {
            if (property.nested != null) {
                throw new IllegalStateException("Nested flag on property is already set");
            }
            
            property.nested = nested;
            
            return this;
        }
        
        /**
         * Finishes the construction of the {@link #property} under construction and returns the
         * newly created immutable instance.
         * @return an immutable {@code PropertyAnalysis} instance.
         */
        public PropertyAnalysis<T> build() {
            if (property.nested == null) {
                property.nested = false;
            }
            
            return property;
        }
    }
}
