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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import com.github.kburger.rdf4j.objectmapper.annotations.Subject;
import com.github.kburger.rdf4j.objectmapper.annotations.Type;
import com.github.kburger.rdf4j.objectmapper.annotations.ext.MixIn;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalysis;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;
import com.github.kburger.rdf4j.objectmapper.core.util.Utils;

/**
 * 
 */
public class CoreClassAnalyzer extends AbstractAnalyzer implements ClassAnalyzer {
    /** Logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(CoreClassAnalyzer.class);
    
    /** Cached analysis results. */
    private final Map<Class<?>, ClassAnalysis> cache;
    private final MixInAnalyzer mixInAnalyzer;
    
    /**
     * Constructs a new analyzer.
     */
    public CoreClassAnalyzer() {
        cache = Collections.synchronizedMap(new HashMap<>());
        mixInAnalyzer = new MixInAnalyzer();
    }
    
    @Override
    public <T, U> void registerMixIn(Class<T> target, Class<U> mixIn) {
        mixInAnalyzer.registerMixIn(target, mixIn);
    }
    
    /**
     * 
     * @param clazz
     * @return
     * @throws AnalysisException
     */
    @Override
    public <T> ClassAnalysis analyze(Class<T> clazz) {
        return analyzeInternal(clazz, new Stack<>());
    }
    
    /**
     * 
     * @param clazz
     * @param stack
     * @return
     * @throws AnalysisException
     */
    private <T> ClassAnalysis analyzeInternal(Class<T> clazz, Stack<Class<?>> stack) {
        if (Utils.isSystemClass(clazz)) {
            throw new AnalysisException("JDK classes are not supported");
        }
        
        if (cache.containsKey(clazz)) {
            logger.debug("Retrieving analysis results for {} from cache", clazz);
            return cache.get(clazz);
        }
        
        stack.push(clazz);
        
        var builder = ClassAnalysis.builder();
        
        var mixInAnnotation = mixInAnalyzer.analyze(clazz, builder);
        
        var inheritFlag = mixInAnnotation.map(MixIn::inherit).orElse(MixIn.DEFAULT_INHERIT);
        var overrideFlag = mixInAnnotation.map(MixIn::override).orElse(MixIn.DEFAULT_OVERRIDE);
        
        if (inheritFlag && !Utils.isSystemClass(clazz.getSuperclass())) {
            var parent = analyzeInternal(clazz.getSuperclass(), stack);
            
            parent.getType()
                    .ifPresent(builder::type);
            
            parent.getSubject()
                    .ifPresent(builder::subject);
            
            parent.getPredicates()
                    .forEach(builder::predicate);
        }
        
        if (!overrideFlag) {
            addTypeProperty(clazz, builder);
            addSubjectProperty(clazz, builder);
            
            for (var property : getTypeInfo(clazz)) {
                analyzeProperty(clazz, property, builder, stack);
            }
        }
        
        var analysis = builder.build();
        
        cache.put(clazz, analysis);
        
        return analysis;
    }
    
    /**
     * Lightweight {@link java.beans.Introspector#getBeanInfo(Class, Class)} alternative.
     * @param target
     * @return
     */
    private static <T> Collection<PropertyInfo> getTypeInfo(Class<T> target) {
        var fields = target.getDeclaredFields();
        var properties = new ArrayList<PropertyInfo>(fields.length);
        
        for (var field : fields) {
            var name = Utils.capitalize(field.getName());
            
            @Nullable Method getter;
            try {
                getter = target.getDeclaredMethod("get".concat(name));
            } catch (NoSuchMethodException e) {
                logger.debug("No read method with name 'get{}' was found on {}", name, target);
                getter = null;
            }
            
            @Nullable Method setter;
            try {
                setter = target.getDeclaredMethod("set".concat(name), field.getType());
            } catch (NoSuchMethodException e) {
                logger.debug("No write method with name 'set{}' was found on {}", name, target);
                setter = null;
            }
            
            properties.add(new PropertyInfo(field, getter, setter));
        }
        
        return properties;
    }
    
    /**
     * 
     * @param clazz
     * @param property
     * @param builder
     * @param stack
     * @throws AnalysisException when a nested call to {@link #analyzeInternal(Class, Stack)} fails.
     */
    private <T> void analyzeProperty(Class<T> clazz, PropertyInfo property, ClassAnalysis.Builder builder, Stack<Class<?>> stack) {
        var typeAnnotation = getAnnotation(Type.class, property);
        var subjectAnnotation = getAnnotation(Subject.class, property);
        var predicateAnnotation = getAnnotation(Predicate.class, property);
        
        if (predicateAnnotation.isPresent()) {
            var predicate = preparePropertyAnalysis(predicateAnnotation, property);
            
            var propertyType = property.getField().getType();
            
            // TODO do we need to distinguish collections here?
            if (!propertyType.isArray() && !Utils.isSystemClass(propertyType)) {
                if (!stack.contains(propertyType)) {
                    analyzeInternal(propertyType, stack);
                }
                
                predicate.nested(true);
            }
            
            builder.predicate(predicate.build());
        } else if (typeAnnotation.isPresent()) {
            var type = preparePropertyAnalysis(typeAnnotation, property);
            
            builder.type(type.build());
        } else if (subjectAnnotation.isPresent()) {
            var subject = preparePropertyAnalysis(subjectAnnotation, property);
            
            builder.subject(subject.build());
        }
    }
    
    /**
     * 
     * @param annotation
     * @param property
     * @return
     */
    private <A extends Annotation> Optional<A> getAnnotation(Class<A> annotation, PropertyInfo property) {
        if (property.getField().isAnnotationPresent(annotation)) {
            return Optional.of(property.getField().getAnnotation(annotation));
        }
        
        return Optional.ofNullable(property.getRead())
                .map(getter -> getter.getAnnotation(annotation));
    }
    
    /**
     * 
     * @param annotation
     * @param property
     * @return
     */
    private <A extends Annotation> PropertyAnalysis.Builder<A> preparePropertyAnalysis(Optional<A> annotation, PropertyInfo property) {
        return PropertyAnalysis.<A>builder()
                .annotation(annotation.get())
                .field(property.getField())
                .getter(property.getRead())
                .setter(property.getWrite());
    }
    
    /**
     * Lightweight {@link java.beans.BeanInfo} alternative.
     */
    @Immutable
    private static class PropertyInfo {
        private final Field field;
        private final Method read;
        private final Method write;
        
        public PropertyInfo(Field field, @Nullable Method read, @Nullable Method write) {
            this.field = field;
            this.read = read;
            this.write = write;
        }
        
        public Field getField() {
            return field;
        }
        
        public @Nullable Method getRead() {
            return read;
        }
        
        public @Nullable Method getWrite() {
            return write;
        }
    }
}
