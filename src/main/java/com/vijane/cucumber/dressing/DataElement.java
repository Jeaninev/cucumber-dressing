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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface DataElement
{
    String DATE_REGEX = "\\(today(?:(?<days>[-+]\\d+))?\\)";

    String TIME_REGEX = "(?:T(?<hr>\\d+):(?<mi>\\d+)(?::(?<sec>\\d+))?(?:\\.(?<msec>\\d+))?)?Z";

    Pattern datePattern = Pattern.compile( DATE_REGEX );

    Pattern dateTimePattern = Pattern.compile( DATE_REGEX + TIME_REGEX );

    DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern( "yyyy-MM-dd['T'HH:mm:ss[.SSS]z]" )
        .parseDefaulting( ChronoField.HOUR_OF_DAY, 0 )
        .parseDefaulting( ChronoField.MINUTE_OF_HOUR, 0 )
        .parseDefaulting( ChronoField.SECOND_OF_MINUTE, 0 )
        .parseDefaulting( ChronoField.NANO_OF_SECOND, 0 )
        .parseDefaulting( ChronoField.OFFSET_SECONDS, 0 )
        .parseDefaulting( ChronoField.ERA, 1 /* era is AD */ )
        .toFormatter()
        .withResolverStyle( ResolverStyle.STRICT );

    static ZonedDateTime getZonedDateTimeFromString( String dateTime )
    {
        if ( dateTime.isEmpty() )
        {
            return null;
        }

        Matcher m = dateTimePattern.matcher( dateTime );

        if ( m.matches() )
        {
            Function<String, Integer> group = f -> m.group( f ) == null ? 0 : Integer.parseInt( m.group( f ) );

            return ZonedDateTime
                .now( ZoneOffset.UTC )
                .plusDays( group.apply( "days" ) )
                .withHour( group.apply( "hr" ) )
                .withMinute( group.apply( "mi" ) )
                .withSecond( group.apply( "sec" ) )
                .withNano( group.apply( "msec" ) * 1000 * 1000 );
        }

        Matcher md = datePattern.matcher( dateTime );

        if ( md.matches() )
        {
            Function<String, Integer> group = f -> md.group( f ) == null ? 0 : Integer.parseInt( md.group( f ) );

            return ZonedDateTime
                .now( ZoneOffset.UTC )
                .plusDays( group.apply( "days" ) );
        }

        return ZonedDateTime.parse( dateTime, DATE_TIME_FORMATTER );
    }

    default Instant getInstantFromString( String dateTime )
    {
        return Optional
            .ofNullable( DataElement.getZonedDateTimeFromString( dateTime ) )
            .map( ZonedDateTime::toInstant )
            .orElse( null );
    }

    default LocalDate getLocalDateFromString( String date )
    {
        if ( date.isEmpty() )
        {
            return null;
        }

        Matcher md = datePattern.matcher( date );

        if ( md.matches() )
        {
            Function<String, Integer> group = f -> md.group( f ) == null ? 0 : Integer.parseInt( md.group( f ) );

            return LocalDate
                .now( ZoneOffset.UTC )
                .plusDays( group.apply( "days" ) );
        }

        return LocalDate.parse( date, DATE_TIME_FORMATTER );
    }
}
