package com.github.kburger.rdf4j.objectmapper.test;

import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import lombok.Setter;

public class InvalidClasses {
    public static class GetterlessClass {
        @Setter @Predicate(Constants.PREDICATE_VALUE)
        private String value;
    }
    
    public static class ThrowingGetterClass {
        @Setter @Predicate(Constants.PREDICATE_VALUE)
        private String value;
        
        public String getValue() {
            throw new RuntimeException("for testing");
        }
    }
}
