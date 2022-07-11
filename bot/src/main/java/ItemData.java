import java.text.NumberFormat;
import static java.lang.Double.parseDouble;

public class ItemData {
    private final String name;
    public double itemFloat;
    private final double price;
    private final String itemWear;
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance();

    public ItemData(String name, String itemFloat, String price) {
        this.formatter.setMinimumIntegerDigits(1);
        this.name = name;
        if (itemFloat.contains("Unable to determine float")) {
            this.itemFloat = 1;
        } else {
            this.itemFloat = parseDouble(itemFloat);
        }
        this.price = parseDouble(price.substring(1));
        if (this.itemFloat < 0.07) {
            this.itemWear = "(Factory New)";
        } else if (this.itemFloat < 0.15) {
            this.itemWear = "(Minimal Wear)";
        } else if (this.itemFloat < 0.38) {
            this.itemWear = "(Field-Tested)";
        } else if (this.itemFloat < 0.45) {
            this.itemWear = "(Well-Worn)";
        } else {
            this.itemWear = "(Battle-Scarred)";
        }
    }

    public String getName() {
        return name;
    }

    public double getItemFloat() {
        return itemFloat;
    }

    public double getPrice() {
        return price;
    }

    public String getItemWear() {
        return itemWear;
    }

    public String toString() {
        return "" + name + " " + itemWear + ", " + itemFloat + ", " + formatter.format(price);
    }

}
