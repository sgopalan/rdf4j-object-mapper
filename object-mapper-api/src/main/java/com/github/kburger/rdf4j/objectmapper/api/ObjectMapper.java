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
package com.github.kburger.rdf4j.objectmapper.api;

import org.eclipse.rdf4j.rio.RDFFormat;

/**
 *
 * @param <R> source type
 * @param <W> sink type
 */
public interface ObjectMapper<R, W> {
    void addModule(Module module);
    
    <T> T read(R source, Class<T> clazz, CharSequence subject, RDFFormat format);
    
    <T> void write(W sink, T object, CharSequence subject, RDFFormat format);
}
