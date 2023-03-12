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

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Product {

    private final String name;
    private final BigDecimal amount;
    private final BigDecimal price;
    private final ZonedDateTime expirationDate;

    public Product(String name, BigDecimal amount, BigDecimal price, ZonedDateTime expirationDate) {
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

}
