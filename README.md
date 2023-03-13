[![Actions Status](https://github.com/Jeaninev/cucumber-dressing/workflows/Build/badge.svg)](https://github.com/Jeaninev/cucumber-dressing/actions)
[![Apache2 license](https://img.shields.io/badge/license-Aache2.0-blue.svg)](https://github.com/Jeaninev/cucumber-dressing/blob/main/LICENSE)

# Cucumber Dressing 🥒✨

# Install library with:
### Install with Maven
```xml
<dependency>
    <groupId>com.vijane.cucumber</groupId>
    <artifactId>cucumber-dressing</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
### Install with Gradle
```groovy
implementation 'com.vijane.cucumber:cucumber-dressing:0.0.1-SNAPSHOT'
```
### Install with Gradle Kotlin DSL
```kotlin
implementation("com.vijane.cucumber:cucumber-dressing:0.0.1-SNAPSHOT")
```

## Table of contents
1. [Introduction](#introduction)
2. [Usage](#usage)

## Introduction

Cucumber Dressing is a simple java helper library to use in combination with data tables in cucumber stepdefs.

## Usage
Cucumber Dressing will map your data table from your feature file easily to Java objects. See below for an example use case:

### Feature file
```gherkin
Feature: Some basic grocery validations

  Scenario:
    Given I have a budget of 100 euro's
    And I have the following grocery list
      | name    | amount | price | expirationDate            |
      | apple   | 5      | 0.4   | (today+15)T00:00:00.000Z  |
      | onion   | 10     | 0.25  | (today+20)T00:00:00.000Z  |
      | potato  | 10     | 0.20  | (today+22)T00:00:00.000Z  |
      | ketchup | 1      | 2.49  | (today+90)T00:00:00.000Z  |
      | rice    | 2      | 4.95  | (today+180)T00:00:00.000Z |
    When I go out and buy everything
    Then I expect to have some money left over
    And I validate the products will not expire at least for the next 14 days
```

### Mapping of data table
```java
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.vijane.cucumber.dressing.DataElement;
import com.vijane.cucumber.dressing.DataElementTransformer;

public class GroceryDataTableMapper
    implements DataElement
{

    @DataElementTransformer
    private String name;

    @DataElementTransformer
    private BigDecimal amount;

    @DataElementTransformer( defaultValue = "0.0" )
    private BigDecimal price;

    @DataElementTransformer( value = "expirationDate", converter = DataElement.class, method = "getZonedDateTimeFromString(java.lang.String)", mandatory = false )
    private ZonedDateTime expirationDate;

    public static class Builder
    {
        public Product from( GroceryDataTableMapper mapper )
        {
            return new Product( mapper.name, mapper.amount, mapper.price, mapper.expirationDate );
        }
    }

}
```

### Usage within your stepdefs
```java
import com.vijane.cucumber.dressing.DataCollection;
import com.vijane.cucumber.dressing.model.GroceryDataTableMapper;
import com.vijane.cucumber.dressing.model.Product;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;

import java.util.List;
import java.util.stream.Collectors;

public class GroceryStepDefs {

    @And("I have the following grocery list")
    public void iHaveTheFollowingGroceryList(DataTable dataTable) {
        List<Product> products = new DataCollection<>(dataTable, GroceryDataTableMapper.class).stream()
                .map(row -> new GroceryDataTableMapper.Builder().from(row))
                .collect(Collectors.toList());
    }
    
}
```