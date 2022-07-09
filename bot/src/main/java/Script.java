import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kong.unirest.Unirest;

import static java.lang.Integer.parseInt;

public class Script {

    public ArrayList<ItemData> run(String url, String option) {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        WebDriver driver = new ChromeDriver(chromeOptions);

        // Get data from steam page
        driver.get(url + "?query=&start=0&count=100");
        WebElement data = driver.findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/script[2]"));
        String name = driver.findElement(By.className("hover_item_name")).getText();
        int pageLength = parseInt(driver.findElement(By.xpath("//*[@id=\"searchResults_total\"]")).getText());
        String datas = data.getAttribute("innerHTML");
        String datas2 = data.getAttribute("innerHTML");

        int checkPageLength = 100;
        if (pageLength < 100) {
           checkPageLength = pageLength;
        }

        ArrayList<String> floats = new ArrayList<>(checkPageLength);

        datas = datas.substring(datas.indexOf(",\"name\":\"Ins") + 5);

        // Filter out inspect links and send to CSGOFloat API
        for (int i = 0; i < checkPageLength; i++) {
            int begin = datas.indexOf("[{\"link\":\"");
            int end = datas.indexOf("\",\"name\":\"Ins");
            int begin2 = datas2.indexOf(",\"id\":\"");
            int end2 = datas2.indexOf("\",\"classid\":");
            String id = datas2.substring(begin2 + 7, end2);
            String link = datas.substring(begin + 10, end);
            link = link.replace("\\/","/");
            link = link.replace("%assetid%", id);
            datas = datas.substring(end + 5);
            datas2 = datas2.substring(end2 + 5);
            String body = Unirest.get("https://api.csgofloat.com/?url={link}")
                    .routeParam("link", link)
                    .asString()
                    .getBody();
            if(body.indexOf("Valve's servers didn't reply in time") != -1) {
                body = "Unable to determine float";
            }
            floats.add(body);
        }


        for (int i = 0; i < floats.size(); i++) {
            if (floats.get(i).indexOf("Unable to determine float") == -1) {
                String temp = floats.get(i).substring(floats.get(i).indexOf("\"floatvalue\":"));
                floats.set(i, temp.substring(temp.indexOf("\"floatvalue\":") + 13, temp.indexOf(",\"")));
            }
        }

        // Get price data from steam market render
        driver.get(url + "/render/?query=&start=0&count=100&country=US&language=english&currency=1");
        WebElement render = driver.findElement(By.xpath("/html/body/pre"));
        String fullRender = render.getAttribute("innerHTML");
        ArrayList<String> prices = new ArrayList<>(checkPageLength);

        for(int i = 0; i < checkPageLength; i++) {
            int begin = fullRender.indexOf("market_listing_price_with_fee");
            int end = fullRender.indexOf("market_listing_price_with_publisher_fee_only");
            prices.add(fullRender.substring(begin + 51, end - 76));
            fullRender = fullRender.substring(end + 20);
        }

        // Build list of ItemData objects
        ArrayList<ItemData> items = new ArrayList<>(checkPageLength);
        for (int i = 0; i < checkPageLength; i++) {
            items.add(new ItemData(name, floats.get(i), prices.get(i)));
        }

        Collections.sort(items, new Comparator<ItemData>() {
            @Override
            public int compare(ItemData a, ItemData b) {
                return Double.compare(a.getPrice(), b.getPrice());
            }
        });

        // Determine options to use
        if((option.indexOf("sort_float") != -1 ) && (option.indexOf("sort_price") != -1))
            return null;

        String options = option;
        ArrayList<String> declaredOptions = new ArrayList<>();
        int count = 1;
        int temp;
        for(int i = 0; i < options.length(); i++) {
            if(options.charAt(i) == "+".charAt(0)) {
                count++;
            }
        }

        for(int i = 0; i < count; i++) {
            if(options.indexOf("+") != -1) {
                int end = options.indexOf("+");
                declaredOptions.add(options.substring(0, end));
                options = options.substring(end + 1);
                declaredOptions.set(i, declaredOptions.get(i).toLowerCase());
            }
        }

        // Apply options
        for(int i = 0; i < declaredOptions.size(); i++) {
            switch(declaredOptions.get(i)) {
                case "sort_float":
                    Collections.sort(items, new Comparator<ItemData>() {
                        @Override
                        public int compare(ItemData a, ItemData b) {
                            return Double.compare(a.getItemFloat(), b.getItemFloat());
                        }
                    });
                    break;

                case "sort_price":
                    Collections.sort(items, new Comparator<ItemData>() {
                        @Override
                        public int compare(ItemData a, ItemData b) {
                            return Double.compare(a.getPrice(), b.getPrice());
                        }
                    });
                    break;

                default:
                    return null;

            }
        }

        return items;
    }

    public void check(String url, String option) {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        WebDriver driver = new ChromeDriver(chromeOptions);

        driver.get(url + "?query=&start=0&count=100");
        WebElement data = driver.findElement(By.xpath("/html/body/div[1]/div[7]/div[2]/script[2]"));
        WebElement pageLength = driver.findElement(By.xpath("//*[@id=\"searchResults_total\"]"));
        driver.get(url + "/render/?query=&start=0&count=100&country=US&language=english&currency=1");
        WebElement render = driver.findElement(By.xpath("/html/body/pre"));
    }
}
