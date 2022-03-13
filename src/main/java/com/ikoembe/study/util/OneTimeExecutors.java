package com.ikoembe.study.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * "@interface" is used to create your own (custom) Java annotations.
 * Annotations are defined in their own file, just like a Java class or interface. Here is custom Java annotation
 * example: @interface MyAnnotation { String value(); String name(); int age(); String[] newNames(); }
 * */

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention( RetentionPolicy.RUNTIME)
public @interface OneTimeExecutors {
    String taskId();
}
