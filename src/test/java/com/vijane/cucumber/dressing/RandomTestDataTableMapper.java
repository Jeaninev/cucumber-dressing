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

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class RandomTestDataTableMapper
    implements DataElement
{

    @DataElementTransformer
    private String IBAN;

    @DataElementTransformer
    private String currency;

    @DataElementTransformer( value = "frDtTm", converter = DataElement.class, method = "getZonedDateTimeFromString(java.lang.String)" )
    private ZonedDateTime fromDateTime;

    @DataElementTransformer( value = "toDtTm", converter = DataElement.class, method = "getZonedDateTimeFromString(java.lang.String)", mandatory = false )
    private ZonedDateTime toDateTime;

    @DataElementTransformer
    private String type;

    @DataElementTransformer( value = "status", converter = Integer.class, method = "parseInt(java.lang.String)" )
    private Integer status;

    @DataElementTransformer( value = "limit", converter = Integer.class, mandatory = false, defaultValue = "0" )
    private Integer limit;

    @DataElementTransformer( converter = Arrays.class, method = "asList(java.lang.Object[])" )
    private List<String> balanceTypes;

    @DataElementTransformer( converter = Arrays.class, method = "asList(java.lang.Object[])", mandatory = false, defaultValue = "no, nothing was set" )
    private List<String> balanceTypesWithDefault;

    public String getIBAN()
    {
        return IBAN;
    }

    public String getCurrency()
    {
        return currency;
    }

    public ZonedDateTime getFromDateTime()
    {
        return fromDateTime;
    }

    public ZonedDateTime getToDateTime()
    {
        return toDateTime;
    }

    public String getType()
    {
        return type;
    }

    public Integer getStatus()
    {
        return status;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public List<String> getBalanceTypes()
    {
        return balanceTypes;
    }

    public List<String> getBalanceTypesWithDefault()
    {
        return balanceTypesWithDefault;
    }

    @Override
    public String toString()
    {
        return "RandomTestDataTableMapper [IBAN=" + IBAN + ", currency=" + currency + ", fromDateTime=" + fromDateTime
            + ", toDateTime=" + toDateTime
            + ", type=" + type + ", status=" + status + ", limit=" + limit + "]";
    }
}
