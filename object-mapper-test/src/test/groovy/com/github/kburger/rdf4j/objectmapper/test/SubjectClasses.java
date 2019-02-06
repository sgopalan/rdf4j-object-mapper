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
package com.github.kburger.rdf4j.objectmapper.test;

import com.github.kburger.rdf4j.objectmapper.annotations.Subject;
import lombok.Data;

public class SubjectClasses {
    @Subject("http://example.com/nested/1")
    public static class TypeSubjectClass {}
    
    @Subject(value = "nested-1", relative = true)
    public static class RelativeTypeSubjectClass {}
    
    @Data
    public static class SettableSubjectClass {
        @Subject
        private String subject;
    }
    
    @Data
    public static class SettableRelativeSubjectClass {
        @Subject(relative = true)
        private String subject;
    }
    
    public static class ThrowingSubjectGetterClass {
        @Subject
        private String subject;
        
        public String getSubject() {
            throw new RuntimeException("for testing");
        }
    }
}
