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
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import com.github.kburger.rdf4j.objectmapper.annotations.Subject;
import com.github.kburger.rdf4j.objectmapper.annotations.Type;
import com.github.kburger.rdf4j.objectmapper.annotations.ext.MixIn;
import com.github.kburger.rdf4j.objectmapper.api.Module;
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis;

public class MixInAnalyzer extends AbstractAnalyzer implements Module.Context {
    private final Map<Class<?>, Class<?>> mixIns;
    
    public MixInAnalyzer() {
        mixIns = Collections.synchronizedMap(new HashMap<>());
    }
    
    @Override
    public <T, U> void registerMixIn(Class<T> target, Class<U> mixIn) {
        if (!mixIn.isAnnotationPresent(MixIn.class)) {
            throw new AnalysisException("Could not register mix-in type without annotation");
        }
        
        mixIns.put(target, mixIn);
    }
    
    /**
     * 
     * @param target
     * @param builder
     * @return
     */
    public <T> Optional<MixIn> analyze(Class<T> target, ClassAnalysis.Builder builder) {
        if (!mixIns.containsKey(target)) {
            return Optional.empty();
        }
        
        var mixIn = mixIns.get(target);
        
        addTypeProperty(mixIn, builder);
        addSubjectProperty(mixIn, builder);
        
        for (var mixInMethod : mixIn.getDeclaredMethods()) {
            if (mixInMethod.isAnnotationPresent(Predicate.class)) {
                analyzeMethod(target, mixInMethod, Predicate.class, builder::predicate);
            }
            
            else if (mixInMethod.isAnnotationPresent(Type.class)) {
                analyzeMethod(target, mixInMethod, Type.class, builder::type);
            }
            
            else if (mixInMethod.isAnnotationPresent(Subject.class)) {
                analyzeMethod(target, mixInMethod, Subject.class, builder::subject);
            }
        }
        
        var annotation = mixIn.getAnnotation(MixIn.class);
        
        return Optional.of(annotation);
    }
    
    private <T, A extends Annotation> void analyzeMethod(Class<T> target, Method mixInMethod,
            Class<A> annotationType, Consumer<PropertyAnalysis<A>> consumer) {
        var annotation = mixInMethod.getAnnotation(annotationType);
        
        var builder = PropertyAnalysis.<A>builder()
                .annotation(annotation);
        
        final Method targetMethod;
        if (mixInMethod.isDefault()) {
            targetMethod = mixInMethod;
        } else {
            try {
                targetMethod = target.getDeclaredMethod(mixInMethod.getName(), mixInMethod.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new AnalysisException("Could not find target method defined by mix-in");
            }
        }
        
        builder.getter(targetMethod);
        
        // TODO from the target class, find the property name and setter
        
        consumer.accept(builder.build());
    }
}
