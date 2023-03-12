package com.vijane.cucumber.dressing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTableTypeRegistry;
import io.cucumber.datatable.DataTableTypeRegistryTableConverter;

public class DataCollectionTest
{

    @Test
    public void testPrepareSetters()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status", "balanceTypes" ), //
                    List.of( "(today-1)T00:00:00.000Z", "(today)T23:59:59.000Z", "19", "xml", "200", "CLBD" ) ) );

        DataCollection<RandomTestDataTableMapper> dataCollection = new DataCollection<>(
            dataTable,
            RandomTestDataTableMapper.class );

        ZonedDateTime from = LocalDate
            .now()
            .atStartOfDay()
            .minusDays( 1 )
            .atZone( ZoneOffset.UTC );

        ZonedDateTime to = LocalDate
            .now()
            .atTime( LocalTime.MAX )
            .truncatedTo( ChronoUnit.SECONDS )
            .atZone( ZoneOffset.UTC );

        RandomTestDataTableMapper dc = dataCollection.get();

        assertThat( dc.getFromDateTime(), Matchers.equalTo( from ) );
        assertThat( dc.getToDateTime(), Matchers.equalTo( to ) );
        assertThat( dc.getLimit(), Matchers.equalTo( 19 ) );
        assertThat( dc.getType(), Matchers.equalTo( "xml" ) );
        assertThat( dc.getStatus(), Matchers.equalTo( 200 ) );
        assertThat( dc.getIBAN(), Matchers.nullValue() );
        assertThat( dc.getCurrency(), Matchers.nullValue() );

        assertThat(
            dc
                .getBalanceTypes()
                .size(),
            Matchers.equalTo( 1 ) );
        assertThat(
            dc
                .getBalanceTypes()
                .get( 0 ),
            Matchers.equalTo( "CLBD" ) );
    }

    @Test
    public void testPrepareSetters_multipleRows()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status", "balanceTypes" ), //
                    List.of( "(today-1)T00:00:00.000Z", "(today)T23:59:59.000Z", "19", "xml", "200", "CLBD" ),
                    List
                        .of(
                            "(today-3)T00:00:00.000Z",
                            "(today)T19:29:39.000Z",
                            "42",
                            "json",
                            "404",
                            "CLBD, ITBD" ) ) );

        DataCollection<RandomTestDataTableMapper> dataCollection = new DataCollection<>(
            dataTable,
            RandomTestDataTableMapper.class );

        ZonedDateTime from = LocalDate
            .now()
            .atStartOfDay()
            .minusDays( 1 )
            .atZone( ZoneOffset.UTC );

        ZonedDateTime to = LocalDate
            .now()
            .atTime( LocalTime.MAX )
            .truncatedTo( ChronoUnit.SECONDS )
            .atZone( ZoneOffset.UTC );

        assertThat( dataCollection.size(), Matchers.equalTo( 2 ) );

        Iterator<RandomTestDataTableMapper> it = dataCollection.iterator();

        RandomTestDataTableMapper dc = it.next();

        assertThat( dc.getFromDateTime(), Matchers.equalTo( from ) );
        assertThat( dc.getToDateTime(), Matchers.equalTo( to ) );
        assertThat( dc.getLimit(), Matchers.equalTo( 19 ) );
        assertThat( dc.getType(), Matchers.equalTo( "xml" ) );
        assertThat( dc.getStatus(), Matchers.equalTo( 200 ) );
        assertThat( dc.getIBAN(), Matchers.nullValue() );
        assertThat( dc.getCurrency(), Matchers.nullValue() );

        assertThat(
            dc
                .getBalanceTypes()
                .size(),
            Matchers.equalTo( 1 ) );
        assertThat(
            dc
                .getBalanceTypes()
                .get( 0 ),
            Matchers.equalTo( "CLBD" ) );

        from = LocalDate
            .now()
            .atStartOfDay()
            .minusDays( 3 )
            .atZone( ZoneOffset.UTC );

        to = LocalDate
            .now()
            .atTime( 19, 29, 39 )
            .truncatedTo( ChronoUnit.SECONDS )
            .atZone( ZoneOffset.UTC );

        dc = it.next();

        assertThat( dc.getFromDateTime(), Matchers.equalTo( from ) );
        assertThat( dc.getToDateTime(), Matchers.equalTo( to ) );
        assertThat( dc.getLimit(), Matchers.equalTo( 42 ) );
        assertThat( dc.getType(), Matchers.equalTo( "json" ) );
        assertThat( dc.getStatus(), Matchers.equalTo( 404 ) );
        assertThat( dc.getIBAN(), Matchers.nullValue() );
        assertThat( dc.getCurrency(), Matchers.nullValue() );

        assertThat(
            dc
                .getBalanceTypes()
                .size(),
            Matchers.equalTo( 2 ) );
        assertThat(
            dc
                .getBalanceTypes()
                .get( 0 ),
            Matchers.equalTo( "CLBD" ) );
        assertThat(
            dc
                .getBalanceTypes()
                .get( 1 ),
            Matchers.equalTo( "ITBD" ) );
    }

    @Test
    public void testPrepareSetters_defaultFields()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status" ), //
                    Arrays.asList( "(today-1)T00:00:00.000Z", null, null, "xml", "200" ) ) );

        DataCollection<RandomTestDataTableMapper> dataCollection = new DataCollection<>(
            dataTable,
            RandomTestDataTableMapper.class );

        ZonedDateTime from = LocalDate
            .now()
            .atStartOfDay()
            .minusDays( 1 )
            .atZone( ZoneOffset.UTC );

        RandomTestDataTableMapper dc = dataCollection.get();

        assertThat( dc.getFromDateTime(), Matchers.equalTo( from ) );
        assertThat( dc.getToDateTime(), Matchers.nullValue() );
        assertThat( dc.getLimit(), Matchers.equalTo( 0 ) );
        assertThat( dc.getType(), Matchers.equalTo( "xml" ) );
        assertThat( dc.getStatus(), Matchers.equalTo( 200 ) );
        assertThat( dc.getIBAN(), Matchers.nullValue() );
        assertThat( dc.getCurrency(), Matchers.nullValue() );
    }

    @Test
    public void testPrepareSetters_nullFields()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status" ), //
                    Arrays.asList( "(today-1)T00:00:00.000Z", null, null, "xml", null ) ) );

        DataTableException exception = assertThrows(
            DataTableException.class,
            () -> new DataCollection<>( dataTable, RandomTestDataTableMapper.class ) );

        assertThat( exception.getMessage(), Matchers.equalTo( "No value for column='status'" ) );
    }

    @Test
    public void testPrepareSetters_emptyField()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status" ), //
                    List.of( "(today-1)T00:00:00.000Z", "", "19", "xml", "200" ) ) );

        DataCollection<RandomTestDataTableMapper> dataCollection = new DataCollection<>(
            dataTable,
            RandomTestDataTableMapper.class );

        ZonedDateTime from = LocalDate
            .now()
            .atStartOfDay()
            .minusDays( 1 )
            .atZone( ZoneOffset.UTC );

        RandomTestDataTableMapper dc = dataCollection.get();

        assertThat( dc.getFromDateTime(), Matchers.equalTo( from ) );
        assertThat( dc.getToDateTime(), Matchers.nullValue() );
        assertThat( dc.getLimit(), Matchers.equalTo( 19 ) );
        assertThat( dc.getType(), Matchers.equalTo( "xml" ) );
        assertThat( dc.getStatus(), Matchers.equalTo( 200 ) );
        assertThat( dc.getIBAN(), Matchers.nullValue() );
        assertThat( dc.getCurrency(), Matchers.nullValue() );
    }

    @Test
    public void testPrepareSetters_nullArray()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status", "balanceTypes" ), //
                    Arrays.asList( "(today-1)T00:00:00.000Z", null, null, "xml", "200", null ) ) );

        DataTableException exception = assertThrows(
            DataTableException.class,
            () -> new DataCollection<>( dataTable, RandomTestDataTableMapper.class ) );

        assertThat( exception.getMessage(), Matchers.equalTo( "No value for column='balanceTypes'" ) );
    }

    @Test
    public void testPrepareSetters_emptyArray()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status", "balanceTypes" ), //
                    Arrays.asList( "(today-1)T00:00:00.000Z", null, null, "xml", "200", "" ) ) );

        DataTableException exception = assertThrows(
            DataTableException.class,
            () -> new DataCollection<>( dataTable, RandomTestDataTableMapper.class ) );

        assertThat( exception.getMessage(), Matchers.equalTo( "No value for column='balanceTypes'" ) );
    }

    @Test
    public void testPrepareSetters_defaultArray()
    {
        DataTable dataTable = createDataTable(
            List
                .of( //
                    List.of( "frDtTm", "toDtTm", "limit", "type", "status", "balanceTypesWithDefault" ), //
                    Arrays.asList( "(today-1)T00:00:00.000Z", null, null, "xml", "200", null ) ) );

        DataCollection<RandomTestDataTableMapper> dataCollection = new DataCollection<>(
            dataTable,
            RandomTestDataTableMapper.class );

        RandomTestDataTableMapper dc = dataCollection.get();

        assertThat(
            dc
                .getBalanceTypesWithDefault()
                .size(),
            Matchers.equalTo( 2 ) );
        assertThat(
            dc
                .getBalanceTypesWithDefault()
                .get( 0 ),
            Matchers.equalTo( "no" ) );
        assertThat(
            dc
                .getBalanceTypesWithDefault()
                .get( 1 ),
            Matchers.equalTo( "nothing was set" ) );
    }

    private DataTable createDataTable( List<List<String>> rows )
    {
        DataTableTypeRegistry reg = new DataTableTypeRegistry( Locale.getDefault() );

        return DataTable.create( rows, new DataTableTypeRegistryTableConverter( reg ) );
    }
}
