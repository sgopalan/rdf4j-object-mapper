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
package com.github.kburger.rdf4j.objectmapper.core.util;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Collection of convenience or shorthand utility methods.
 */
public final class Utils {
    /** Regular expression for testing against JDK types. */
    private static final Pattern JDK_REGEX = Pattern.compile("^(?:\\[L)?java(?:x)?\\..+");
    
    /**
     * Checks the given {@code clazz} against the following rules to determine whether it is
     * considered a system (e.g JDK) class:
     * <ul>
     *   <li>Is the class a primitive type?</li>
     *   <li>Is the class an array and is its {@link Class#getComponentType() component type}
     *   primitive?</li>
     *   <li>Does the FQCN indicate it belongs to a JDK package?</li>
     * </ul>
     * @param clazz the target type to check
     * @return {@code true} if the target {@code clazz} is considered a system class;
     *         {@code false} otherwise.
     */
    public static <T> boolean isSystemClass(Class<T> clazz) {
        return clazz.isPrimitive() ||
                (clazz.isArray() && clazz.getComponentType().isPrimitive()) || 
                JDK_REGEX.matcher(clazz.getName()).matches();
    }
    
    public static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    public static Class<?> resolveGenericTypeArgument(Method getter) {
        var type = getter.getGenericReturnType();
        
        if (type instanceof ParameterizedType == false) {
            // raw type
            if (Collection.class.isAssignableFrom((Class<?>)type)) {
                return Object.class;
            }
            
            // non-generic type
            return (Class<?>)type;
        }

        var actualTypeArguments = ((ParameterizedType)type).getActualTypeArguments();
        
        // wildcard type
        if (actualTypeArguments[0] instanceof WildcardType) {
            return Object.class;
        }
        
        return (Class<?>)actualTypeArguments[0];
    }
    
    public static Class<?> resolveTypeArgument(Field field) {
        var annotatedType = field.getAnnotatedType();
        
        if (annotatedType instanceof AnnotatedParameterizedType == false) {
            var type = (Class<?>)annotatedType.getType();
            
            if (type.getTypeParameters().length > 0) {
                return Object.class;
            } else {
                return type;
            }
        }
        
        var annotatedParameterizedType = (AnnotatedParameterizedType)annotatedType;
        var actualTypes = annotatedParameterizedType.getAnnotatedActualTypeArguments();
        
        if (actualTypes[0].getType() instanceof WildcardType) {
            return Object.class;
        }
        
        return (Class<?>)actualTypes[0].getType();
    }
    
    /** Private constructor. */
    private Utils() {}
}
