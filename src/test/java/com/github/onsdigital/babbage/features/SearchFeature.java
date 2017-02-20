package com.github.onsdigital.babbage.features;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Search Feature for Cucumber
 * Created by fawkej on 16/12/2016.
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"json:target/cucumber-json-report.json"})
public class SearchFeature {

}
