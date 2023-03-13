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
package com.vijane.cucumber.dressing.stepdefs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.vijane.cucumber.dressing.DataCollection;
import com.vijane.cucumber.dressing.model.GroceryDataTableMapper;
import com.vijane.cucumber.dressing.model.Product;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GroceryStepDefs
{

    @Given( "I have a budget of {int} euro's" )
    public void iHaveABudgetOfEuroS( int budget )
    {
        TestScenario.getInstance().setBudget( new BigDecimal( budget ) );
    }

    @And( "I have the following grocery list" )
    public void iHaveTheFollowingGroceryList( DataTable dataTable )
    {
        List<Product> products = DataCollection
            .create( dataTable, GroceryDataTableMapper.class ).stream()
            .map( row -> new GroceryDataTableMapper.Builder().from( row ) )
            .collect( Collectors.toList() );

        TestScenario.getInstance().setProducts( products );
    }

    @When( "I go out and buy everything" )
    public void iGoOutAndBuyEverything()
    {
        // assuming you are now at Albert Heijn
    }

    @Then( "I expect to have some money left over" )
    public void iExpectToHaveSomeMoneyLeftOver()
    {
        TestScenario testScenario = TestScenario.getInstance();
        BigDecimal budget = testScenario.getBudget();
        List<Product> products = testScenario.getProducts();

        BigDecimal totalCost = products
            .stream()
            .map( product -> product.getPrice().multiply( product.getAmount() ) )
            .reduce( BigDecimal.ZERO, BigDecimal::add );

        BigDecimal leftOver = budget.subtract( totalCost );
        assertThat( leftOver, greaterThan( BigDecimal.ZERO ) );
    }

    @And( "I validate the products will not expire at least for the next {int} days" )
    public void iValidateTheProductsWillNotExpireAtLeastForTheNextDays( int minDaysUntilExpired )
    {
        ZonedDateTime minExpirationDate = ZonedDateTime.now().plusDays( minDaysUntilExpired );
        TestScenario
            .getInstance()
            .getProducts()
            .forEach(
                product -> assertThat(
                    String
                        .format(
                            "Product [%s] is going to expire earlier then the requested minimal of [%d] days until it should expire",
                            product.getName(),
                            minDaysUntilExpired ),
                    product.getExpirationDate().isAfter( minExpirationDate ),
                    equalTo( true ) ) );
    }

}
