package whisk

import org.junit.runner.RunWith
import org.junit.Test
import cucumber.junit.Cucumber

@Test
@RunWith(classOf[Cucumber])
//@Cucumber.Options(format = Array("junit:target/junit"))
@Cucumber.Options(format = Array("pretty", "html:target/cucumber"))
class RunCukesTest {

}


