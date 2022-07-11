import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kong.unirest.Unirest;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class Script {

    public ArrayList<ItemData> run(String url, String option) {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        WebDriver driver = new ChromeDriver(chromeOptions);

        // Get price data from steam market render
        driver.get(url + "/render/?query=&start=0&count=100&country=US&language=english&currency=1");
        WebElement data = driver.findElement(By.xpath("/html/body/pre"));
        String datas = data.getAttribute("innerHTML");
        if (datas.substring(datas.indexOf("{\"success\":") + 11, datas.indexOf(",\"start\"")).contains("false"))
            return null;
        String datas2 = data.getAttribute("innerHTML");
        String name = datas.substring(datas.indexOf("\"Inspect in Game...\"}],\"name\":\"") + 31, datas.indexOf("\",\"name_color\":\""));
        int checkPageLength = parseInt(datas.substring(datas.indexOf("\"total_count\":") + 14, datas.indexOf(",\"results_html\":")));
        if (checkPageLength > 100)
            checkPageLength = 100;

        ArrayList<String> prices = new ArrayList<>(checkPageLength);
        ArrayList<String> floats = new ArrayList<>(checkPageLength);

        datas = datas.substring(datas.indexOf(",\"listinginfo\":{\"") + 5);

        for (int i = 0; i < checkPageLength; i++) {
            int begin = datas.indexOf("\"listingid\":\"");
            int end = datas.indexOf("\",\"pric");
            String listingId = datas.substring(begin + 13, end);
            begin = datas.indexOf(",\"id\":\"");
            end = datas.indexOf("\",\"amount\"");
            String assetId = datas.substring(begin + 7, end);
            begin = datas.indexOf("[{\"link\":\"");
            end = datas.indexOf("\",\"name\":\"Ins");
            String link = datas.substring(begin + 10, end);
            link = link.replace("\\/","/");
            link = link.replace("%listingid%", listingId);
            link = link.replace("%assetid%", assetId);
            datas = datas.substring(end + 5);
            String body = Unirest.get("https://api.csgofloat.com/?url={link}")
                    .routeParam("link", link)
                    .asString()
                    .getBody();
            if(body.contains("Valve's servers didn't reply in time")) {
                body = "Unable to determine float";
            }
            floats.add(body);
            begin = datas2.indexOf("market_listing_price_with_fee");
            end = datas2.indexOf("market_listing_price_with_publisher_fee_only");
            prices.add(datas2.substring(begin + 51, end - 76));
            datas2 = datas2.substring(end + 20);
        }

        for (int i = 0; i < floats.size(); i++) {
            if (!floats.get(i).contains("Unable to determine float")) {
                String temp = floats.get(i).substring(floats.get(i).indexOf("\"floatvalue\":"));
                floats.set(i, temp.substring(temp.indexOf("\"floatvalue\":") + 13, temp.indexOf(",\"")));
            }
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
        if(((option.contains("sort_float")) && (option.contains("sort_price"))) || ((option.contains("asc")) && (option.contains("sc"))))
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
            if(options.contains("+")) {
                int end = options.indexOf("+");
                declaredOptions.add(options.substring(0, end));
                options = options.substring(end + 1);
                declaredOptions.set(i, declaredOptions.get(i).toLowerCase());
            }
        }

        // Apply options
        for(int i = 0; i < declaredOptions.size(); i++) {
            String declaredOption = declaredOptions.get(i);
            String sign = null;
            double parameter = 0;
            boolean filter = false;
            if (declaredOption.contains("filter")) {
                switch(Character.toString(declaredOption.charAt(12))) {
                    case "<":
                        parameter = parseDouble(declaredOption.substring(13));
                        sign = new String("<");
                        declaredOption = declaredOption.substring(0, 12);
                        filter = true;
                        break;
                    case ">":
                        parameter = parseDouble(declaredOption.substring(13));
                        sign = new String(">");
                        declaredOption = declaredOption.substring(0, 12);
                        filter = true;
                        break;
                    default:
                        return null;
                }
            }

            switch(declaredOption) {
                case "sort_float_asc":
                    sort(items, "float");
                    break;

                case "sort_float_dsc":
                    sort(items, "float");
                    Collections.reverse(items);
                    break;

                case "sort_price_asc":
                    sort(items, "price");
                    break;

                case "sort_price_dsc":
                    sort(items, "price");
                    Collections.reverse(items);
                    break;

                case "filter_float":
                    filter(items, filter, sign, "float", parameter);
                    break;

                case "filter_price":
                    filter(items, filter, sign, "price", parameter);
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
        driver.get(url + "/render/?query=&start=0&count=100&country=US&language=english&currency=1");
        WebElement render = driver.findElement(By.xpath("/html/body/pre"));
    }

    public static void sort(ArrayList<ItemData> list, String type) {
        switch(type) {
            case "float":
                Collections.sort(list, new Comparator<ItemData>() {
                    @Override
                    public int compare(ItemData a, ItemData b) {
                        return Double.compare(a.getItemFloat(), b.getItemFloat());
                    }
                });
                break;
            case "price":
                Collections.sort(list, new Comparator<ItemData>() {
                    @Override
                    public int compare(ItemData a, ItemData b) {
                        return Double.compare(a.getPrice(), b.getPrice());
                    }
                });
                break;
        }
    }

    public static void filter(ArrayList<ItemData> list, boolean filter, String sign, String type, double parameter) {
        if (filter) {
            switch(type) {
                case "float":
                    switch(sign) {
                        case ">":
                            for (int i = list.size() - 1; i >= 0; i--) {
                                if (list.get(i).getItemFloat() < parameter) {
                                    list.remove(i);
                                }
                            }
                            break;
                        case "<":
                            for (int i = list.size() - 1; i >= 0; i--) {
                                if (list.get(i).getItemFloat() > parameter) {
                                    list.remove(i);
                                }
                            }
                            break;
                    }
                    break;
                case "price":
                    switch(sign) {
                        case ">":
                            for (int i = list.size() - 1; i >= 0; i--) {
                                if(list.get(i).getPrice() < parameter) {
                                    list.remove(i);
                                }
                            }
                            break;
                        case "<":
                            for (int i = list.size() - 1; i >= 0; i--) {
                                if(list.get(i).getPrice() > parameter) {
                                    list.remove(i);
                                }
                            }
                            break;
                    }
                    break;

            }
        }
    }
}
