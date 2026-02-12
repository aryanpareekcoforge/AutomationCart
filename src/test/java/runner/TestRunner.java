package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepsDefinations","hooks"},
        tags = "not @delete",
        plugin = {"pretty",
        		    "html:target/cucumber-report.html",
                "json:target/cucumber.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:" 

        		
        }
)

public class TestRunner extends AbstractTestNGCucumberTests 
{ 
	
	
}