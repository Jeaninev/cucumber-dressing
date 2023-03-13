/*
 * Copyright 2023 vijane.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vijane.cucumber.dressing;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.cucumber.datatable.DataTable;

/**
 * {@code @DataElementTransformer} is used to mark that annotated field is part of a <em>DataTable</em> element.
 * <p>
 * {@code @DataElementTransformer} fields should be {@code private} at class level.
 * <p>
 * <h2>Usage</h2>
 * <p>
 * The simple {@code DataTable} can be converted using the field declarations below.
 * <p>
 * <pre>
 *    Given the following dataTable
 *    | amount | listTypes | someName |
 *    | 1.23   | aa,bb,cc  | VALUE1   |
 *    | 4.56   | dd,ee,ff  | VALUE2   |
 * </pre>
 * <p>
 * By default annotated fields simply converts string values from columns in {@code DataTable} to their type. This works
 * for most types that have a constructor accepting a single {@code String} parameter:
 * <p>
 * <pre>
 *    &#064;DataElementTransformer
 *    private BigDecimal amount;
 * </pre>
 * <p>
 * Lists need a little help. This can be achieved by passing a few simple parameters to the annotation, like:
 * <p>
 * <pre> 
 *    &#064;DataElementTransformer( converter = Arrays.class, method = "asList(java.lang.Object[])" )
 *    private List<String> listTypes; 
 * </pre>
 * <p>
 * <em>Note: Be careful to use the proper method signature</em> By default all fields in the class are mandatory. Set
 * the mandatory parameter to false to make a field optional, or provide a default value for the field:
 * <p>
 * <pre>
 *    &#064;DataElementTransformer( converter = Arrays.class, method = "asList(java.lang.Object[])", mandatory = false, defaultValue = "no, nothing was set" )
 *    private List<String> listTypesWithDefault;
 * </pre>
 * <p>
 * Enum's can be implemented as below:
 * <p>
 * <pre>
 *    &#064;DataElementTransformer(value = "someName", converter = EnumType.class, method = "valueOf(java.lang.String)")
 *    private EnumType enumType;
 * </pre>
 *
 * @see DataTable
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { TYPE, FIELD } )
public @interface DataElementTransformer
{
    /** name of the {@code DataTable} column, when the field name differs */
    String value() default "";

    /** type of the field to convert the {@code DataTable} field to */
    Class<?> converter() default String.class;

    /** method in the converter to use. Mind the method signature! */
    String method() default "";

    /** by default all fields in a {@code DataTable} are mandatory, this parameter can be set to make it optional */
    boolean mandatory() default true;

    /** provide a default value for the field */
    String defaultValue() default "";

    /** this parameter is used to split the array fields, by default it is comma separated */
    String splitter() default "\\s*,\\s*";
}
