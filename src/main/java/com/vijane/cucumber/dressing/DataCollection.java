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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.cucumber.datatable.DataTable;

public class DataCollection<T extends DataElement>
    implements Iterable<T>, Supplier<T>
{

    private final List<T> elements;

    public static <U extends DataElement> DataCollection<U> create( DataTable dataTable, Class<U> clazz )
    {
        return new DataCollection<U>( dataTable, clazz );
    }

    public DataCollection( DataTable dataTable, Class<T> clazz )
    {

        List<String> headers = dataTable
            .cells()
            .get( 0 );
        validateHeaders( headers );

        elements = dataTable
            .asMaps( String.class, String.class )
            .stream()
            .collect( ArrayList::new, ( l, row ) -> {
                T element = getInstance( clazz );

                Map<String, Function<String, Field>> preparedSetters = prepareSetters( element );

                preparedSetters
                    .entrySet()
                    .stream()
                    .forEach( ps -> {
                        if ( row.containsKey( ps.getKey() ) )
                        {
                            // TODO verify the functionality
                        }
                    } );

                row
                    .entrySet()
                    .stream()
                    .filter( e -> preparedSetters.containsKey( e.getKey() ) )
                    .forEach( e -> {
                        String string = Optional
                            .ofNullable( e.getValue() )
                            .orElse( "" );
                        preparedSetters
                            .get( e.getKey() )
                            .apply( string );
                    } );

                l.add( element );
            }, ArrayList::addAll );
    }

    Map<String, Function<String, Field>> prepareSetters( T element )
    {
        return Stream
            .of(
                element
                    .getClass()
                    .getDeclaredFields() )
            .filter( field -> field.isAnnotationPresent( DataElementTransformer.class ) )
            .collect( HashMap::new, ( collector, field ) -> {
                final DataElementTransformer annotation = field.getAnnotation( DataElementTransformer.class );

                String name = field.getName();
                if ( !annotation
                    .value()
                    .isBlank() )
                {
                    name = annotation.value();
                }

                Function<String, Field> fn = arg -> {

                    if ( arg == null || arg.isBlank() )
                    {
                        if ( annotation.mandatory() )
                        {
                            throw new DataTableException( "No value for column='" + field.getName() + "'" );
                        }

                        if ( annotation
                            .defaultValue()
                            .isBlank() && Number.class.isAssignableFrom( field.getType() ) )
                        {
                            throw new DataTableException( "No default value set for column='" + field.getName() + "'" );
                        }

                        arg = annotation.defaultValue();
                    }

                    field.setAccessible( true );

                    try
                    {
                        if ( !annotation
                            .method()
                            .isBlank() )
                        {
                            String methodName = annotation.method();

                            Method method = Stream
                                .of(
                                    annotation
                                        .converter()
                                        .getMethods() )
                                .filter(
                                    mn -> mn
                                        .toString()
                                        .contains( methodName ) )
                                .findFirst()
                                .orElseThrow( () -> new NoSuchMethodException( methodName ) );

                            if ( method.isDefault() )
                            {
                                String targ = arg;
                                InvocationHandler handler = ( proxy, mth, args ) -> MethodHandles
                                    .lookup()
                                    .findSpecial(
                                        annotation.converter(),
                                        method.getName(),
                                        MethodType.methodType( field.getType(), String.class ),
                                        annotation.converter() )
                                    .bindTo( proxy )
                                    .invokeWithArguments( targ );
                                DataElement newProxyInstance = (DataElement) Proxy
                                    .newProxyInstance(
                                        Thread
                                            .currentThread()
                                            .getContextClassLoader(),
                                        new Class[] { annotation.converter() },
                                        handler );

                                Object result = handler.invoke( newProxyInstance, method, new Object[] { arg } );

                                field.set( element, result );
                            }
                            else
                            {
                                Object newInstance = null;
                                if ( !Modifier.isStatic( method.getModifiers() ) )
                                {
                                    newInstance = annotation
                                        .converter()
                                        .getConstructor()
                                        .newInstance();
                                }

                                // TODO Improve this when other variants come, but can also
                                // adopted for the default method here above
                                Object result = null;
                                Class<?>[] parameterTypes = method.getParameterTypes();
                                if ( parameterTypes[0].isArray() )
                                {
                                    result = method
                                        .invoke( newInstance, new Object[] { arg.split( annotation.splitter() ) } );
                                }
                                else
                                {
                                    result = method.invoke( newInstance, arg );
                                }

                                field.set( element, result );
                            }
                        }
                        else
                        {

                            Class<?> resultType = field.getType();

                            Object result = resultType
                                .getConstructor( String.class )
                                .newInstance( arg );

                            field.set( element, result );
                        }
                        return field;
                    }
                    catch ( Throwable e1 )
                    {
                        throw new RuntimeException( e1 );
                    }
                };

                collector.put( name, fn );
            }, HashMap::putAll );
    }

    private void validateHeaders( List<String> headers )
    {
        for ( String header : headers )
        {
            if ( header.isEmpty() || header.equals(" "))
            {
                throw new RuntimeException( "Header contains empty column names" );
            }
        }
    }

    private T getInstance( Class<T> clazz )
    {
        try
        {
            return clazz
                .getConstructor()
                .newInstance();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public int size()
    {
        return elements.size();
    }

    public T get( int index )
    {
        return elements.get( index );
    }

    @Override
    public T get()
    {
        return elements
            .iterator()
            .next();
    }

    @Override
    public Iterator<T> iterator()
    {
        return elements.iterator();
    }

    public Stream<T> stream()
    {
        return elements.stream();
    }
}
