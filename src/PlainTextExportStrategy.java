import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
/**
 * Exports the menu as a plain‑text outline,
 * now appending “.txt” if needed.
 */
public class PlainTextExportStrategy implements ExportStrategy {
    @Override
    public void export(MenuComponent menu, String filePath) throws IOException {
        if (!filePath.toLowerCase().endsWith(".txt")) {
            filePath += ".txt";
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            exportComponent(menu, writer, "");
        }
    }

    private void exportComponent(MenuComponent component,
                                 BufferedWriter writer,
                                 String indent) throws IOException {
        // Name + price
        writer.write(indent + component.getName());
        try {
            double price = component.getPrice();
            writer.write(String.format(" : $%.2f", price));
        } catch (UnsupportedOperationException ignored) { }
        // Allergies if leaf
        if (component instanceof MenuItem) {
            Set<Allergy> allergies = ((MenuItem) component).getAllergies();
            writer.write(" [Allergies: "
                    + (allergies.isEmpty() ? "None"
                    : String.join(", ", allergies.stream().map(Enum::name).toArray(String[]::new)))
                    + "]");
        }
        writer.newLine();

        // Recurse composites
        if (component instanceof MenuCategory) {
            for (MenuComponent child : ((MenuCategory) component).getChildren()) {
                exportComponent(child, writer, indent + "  ");
            }
        }
    }
}
