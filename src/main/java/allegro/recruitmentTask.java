package allegro;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class recruitmentTask
{
    private WebDriver wd;

    @BeforeTest(alwaysRun = true)
    public void beforeTest()
    {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        wd = new ChromeDriver();
    }


    @Test(enabled = true)
    public void recruitmentTaskTest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(wd, 10);

        wd.get("https://allegro.pl/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@aria-labelledby='dialog-title']")));
        wd.findElement(By.xpath("(//div[@aria-labelledby='dialog-title']//button)[3]")).click();

        String searchKey = "Iphone 11";
        wd.findElement(By.xpath("//form/input")).sendKeys(searchKey);

        String firstSuggestion = "(//div[@role='listbox']/a)[1]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(firstSuggestion)));
        wd.findElement(By.xpath(firstSuggestion)).click();

        String colorFilterOptionLocator = "((//div[@class='opbox-listing-filters']//fieldset)[11]//span)[2]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(colorFilterOptionLocator)));
        WebElement colorFilterOptionElement = wd.findElement(By.xpath(colorFilterOptionLocator));

        Actions builder = new Actions(wd);
        builder.moveToElement(colorFilterOptionElement).click(colorFilterOptionElement);
        builder.perform();
        
        wait.until(ExpectedConditions.urlContains("kolor=czarny"));
        wd.get(wd.getCurrentUrl());

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-box-name='items container']")));
        System.out.println("Number of products: " +
                wd.findElements(By.xpath("//div[@data-box-name='items container']//article")).size());


        List<String> pricesRaw = new ArrayList<String>();
        for(WebElement element :
                wd.findElements(By.xpath("//div[@data-box-name='items container']//article/div/div[2]/div[2]/div/div/span")))
        {
            pricesRaw.add(element.getText());
        }

        List<Float> pricesClean = new ArrayList<Float>();
        for(int i=0; i<pricesRaw.size(); i++)
        {
            String priceClean;
            priceClean = StringUtils.deleteWhitespace(pricesRaw.get(i));
            priceClean = StringUtils.removeEnd(priceClean, "zł");
            priceClean = StringUtils.replace(priceClean, ",", ".");

            pricesClean.add(Float.parseFloat(priceClean));
        }

        Collections.sort(pricesClean);

        Float highestPrice = pricesClean.get(pricesClean.size()-1);
        System.out.println("highestPrice: " + highestPrice);

        Float highestPriceVAT = ((highestPrice * 23)/100) + highestPrice;
        System.out.println("highestPriceVAT: " + highestPriceVAT);
    }


    @AfterTest(alwaysRun = true)
    public void afterTest()
    {
        wd.quit();
    }
}
