package com.workify.worksphere.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)   // can only be used on method parameters
@Retention(RetentionPolicy.RUNTIME)  // available at runtime

public @interface BoardIdParam {
}
