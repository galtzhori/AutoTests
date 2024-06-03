import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LinkedinConnections {
    WebDriver driver;
    WebDriverWait wait;
    WebElement connectionsHolder;
    String name;
    String username;
    String password;

    public LinkedinConnections(String username, String password) {

        this.username = username;
        this.password = password;
        DriverInstance driverInstance = new DriverInstance();
        driver = driverInstance.driver;
        wait = new WebDriverWait(driver, Duration.ofMillis(1000));
        try {

            this.SignInToLinkedin();
            this.ShowConnectionPage();
            this.LoadAllConnections();
            this.WriteConnections();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }


    }

    void SignInToLinkedin() throws Exception {
        // 1.2 Navigate to LinkedIn page
        driver.get("https://www.linkedin.com/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        // 1.3 Click “Sign in” to navigate to the login page

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sign in")));
        signInButton.click();
        // 1.4 Perform a login
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        String newUrl = driver.getCurrentUrl();

        if (newUrl.startsWith("https://www.linkedin.com/checkpoint/challenge")) {
            System.out.println("access to account denied");
            throw new Exception();
        }
        if (newUrl.startsWith("https://www.linkedin.com/checkpoint")) {
            System.out.println("login info is incorrect!");
            throw new Exception();
        }


    }

    void ShowConnectionPage() throws Exception {
        // 1.5 Click “Me” to open the profile menu
        WebElement meButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("" +
                ".global-nav__primary-link.global-nav__primary-link-me-menu-trigger.artdeco-dropdown__trigger." +
                "artdeco-dropdown__trigger--placement-bottom.ember-view")));

        meButton.click();

        name = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".artdeco-entity-lockup__title.ember-view"))).getText();

        // 1.6 Navigate to “your profile”

        WebElement viewProfileButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("View Profile")));
        viewProfileButton.click();

        // 1.7 Navigate to “your connections”
        try {
            WebElement connectionsTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/mynetwork/invite-connect/connections/']")));
            connectionsTab.click();
            // Additional wait to ensure that the connections page loads
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("mn-connections__header")));
            connectionsHolder = driver.findElement(By.xpath("//div[@class='scaffold-finite-scroll__content']"));
        } catch (Exception e) {
            System.out.println("no connections");
            throw new Exception();
        }

    }

    void LoadAllConnections() {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        boolean showMore = true;
        while (showMore) {
            long oldPageHeight = (long) jse.executeScript("return document.body.scrollHeight");
            jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            try {
                WebElement showMoreButton = driver.findElement(By.cssSelector(".artdeco-button.artdeco-button--muted." +
                        "artdeco-button--1.artdeco-button--full.artdeco-button--secondary.ember-view.scaffold-finite-scroll__load-button"));
                showMoreButton.click();
            } catch (Exception e) {
                long newPageHeight = (long) jse.executeScript("return document.body.scrollHeight");
                if (oldPageHeight == newPageHeight)
                    showMore = false;
            }
        }
    }

    void WriteConnections() throws Exception {
        // Find the child element
        WebElement li = this.connectionsHolder.findElement(By.xpath("./child::*"));
        List<WebElement> list = li.findElements(By.xpath("./child::*"));
        List<String> connectionsData = new ArrayList<>();
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter("connections.json", false));

        for (WebElement i : list) {


            // Print the child elements
            connectionsData.add("\n\"" + i.findElement(By.className("mn-connection-card__name")).getText().replaceAll("\"", "'") + "\"");
            connectionsData.add("\n\"" + i.findElement(By.className("mn-connection-card__occupation")).getText().replaceAll("\"", "'") + "\"");
            connectionsData.add("\n\"" + i.findElement(By.xpath("//time")).getText() + "\"");
        }
        fileWriter.write("{\"myName\": \"" + name + "\",\n\"myWorkplace\": \"Intel\",\n\"city\": \"Tel-Aviv\",\n\"connections\":\n");
        String connectionsDataString = connectionsData.toString();
        fileWriter.write(connectionsDataString + "\n}");
        fileWriter.close();
    }

    public static void main(String[] args) {
        String username = "gilnada007@gmail.com";
        String password = "gtgtgt";
        new LinkedinConnections(username, password);
    }
}
