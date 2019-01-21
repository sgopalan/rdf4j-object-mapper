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
package com.github.kburger.rdf4j.objectmapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Predicate {
    /**
     * Indicates the triple's predicate. The predicate is expected to be an absolute IRI string. Any
     * invalid IRI or relative IRI can be expected to cause exceptions.
     * @return an absolute IRI string.
     */
    String value();
    
    /**
     * Indicates whether the annotated property is a required or an optional statement. Required
     * properties can be expected to cause (validation) exceptions when absent.
     * 
     * <p>Defaults to {@code true}.</p>
     * @return {@code true} if this property is required; {@code false} if it is optional.
     */
    boolean required() default true;
    
    /**
     * Indicates whether the annotated property can be user-provided, or restricted to a server-side
     * provider. Some data models might restrict properties like timestamps to be server-side
     * generated. Read-only properties provided by a user can be expected to be ignored.
     * 
     * <p>Defaults to {@code false}.</p>
     * @return {@code false} if this property can be user-provided; {@code true} if it's defined at
     *         server-side. 
     */
    boolean readonly() default false;
    
    /**
     * Indicates the nature of the triple's object: if {@code false} the object is interpreted as an
     * IRI, if {@code true} the object is interpreted as a literal. When set to {@code true}, the
     * {@link #datatype()} attribute is used to specify the literal's type.
     * 
     * <p>Defaults to {@code false}.</p>
     * @return {@code false} if the triple's object captures an IRI; {@code true} if it captures a
     *         {@code literal} type. 
     */
    boolean literal() default false;
    
    /**
     * Indicates the datatype of the triple's literal object when the {@link #literal()} attribute
     * is {@code true}. When the literal attribute is {@code false}, this attribute can be expected
     * to be ignored.
     * 
     * <p>Defaults to {@code http://www.w3.org/2001/XMLSchema#string}.</p>
     * @return an absolute IRI string that defines the literal datatype. 
     */
    String datatype() default "http://www.w3.org/2001/XMLSchema#string";
}
