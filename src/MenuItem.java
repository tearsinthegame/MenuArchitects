import java.util.Collections;
import java.util.Set;
/**
 * Leaf in Composite now supporting multiple allergies.
 */
public class MenuItem extends MenuComponent {
    private final String name;
    private final double price;
    private final Set<Allergy> allergies;

    public MenuItem(String name, double price, Set<Allergy> allergies) {
        this.name = name;
        this.price = price;
        // never null
        this.allergies = allergies.isEmpty()
                ? Collections.emptySet()
                : Collections.unmodifiableSet(allergies);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public Set<Allergy> getAllergies() {
        return allergies;
    }

    @Override
    public void print() {
        System.out.printf("  %s : $%.2f [Allergies: %s]%n",
                name,
                price,
                allergies.isEmpty() ? "None" : String.join(
                        ", ",
                        allergies.stream().map(Enum::name).toArray(String[]::new)
                )
        );
    }
}