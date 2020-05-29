import com.yilnz.surfing.core.SurfSpider;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class GoogleTest {

	@Test
	public void test(){
		System.setProperty("webdriver.chrome.driver", "/Users/zyl/Documents/chromedriver/chromedriver");
		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			driver.get("https://google.com/ncr");
			driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
			WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3>div")));
			System.out.println(firstResult.getAttribute("textContent"));
		} finally {
			driver.quit();
		}
	}
}
