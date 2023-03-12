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
package com.vijane.cucumber.dressing.model;

import com.vijane.cucumber.dressing.DataElement;
import com.vijane.cucumber.dressing.DataElementTransformer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class GroceryDataTableMapper implements DataElement {

    @DataElementTransformer
    private String name;

    @DataElementTransformer(value = "amount", converter = BigDecimal.class, mandatory = false, defaultValue = "0.0")
    private BigDecimal amount;

    @DataElementTransformer(value = "price", converter = BigDecimal.class)
    private BigDecimal price;

    @DataElementTransformer(value = "expirationDate", converter = DataElement.class, method = "getZonedDateTimeFromString(java.lang.String)", mandatory = false)
    private ZonedDateTime expirationDate;


    public static class Builder {
        public Product from(GroceryDataTableMapper mapper) {
            return new Product(mapper.name, mapper.amount, mapper.price, mapper.expirationDate);
        }
    }


}
