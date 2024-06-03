import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverInstance {
    WebDriver driver;
    public DriverInstance(){
        System.setProperty("webdriver.chrome.driver", "/chromedriver-win64/chromedriver.exe");
        driver = new ChromeDriver();
    }
}
