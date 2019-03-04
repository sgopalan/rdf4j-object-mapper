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
package com.github.kburger.rdf4j.objectmapper.api.reader;

import org.eclipse.rdf4j.rio.RDFFormat;
import com.github.kburger.rdf4j.objectmapper.api.Module;

/**
 * 
 * @param <R>
 */
public interface ObjectReader<R> extends Module.Context {
    /**
     * 
     * @param reader
     * @param clazz
     * @param subject
     * @param format
     * @return
     */
    <T> T read(R reader, Class<T> clazz, CharSequence subject, RDFFormat format);
}
