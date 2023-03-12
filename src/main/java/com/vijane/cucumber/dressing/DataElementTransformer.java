package com.vijane.cucumber.dressing;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( { TYPE, FIELD } )
public @interface DataElementTransformer
{
    String value() default "";

    Class<?> converter() default String.class;

    String method() default "";

    boolean mandatory() default true;

    String defaultValue() default "";

    String splitter() default "\\s*,\\s*";
}
