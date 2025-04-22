import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Exports the menu in JSON, now serializing allergies as an array.
 */
public class JsonExportStrategy implements ExportStrategy {
    @Override
    public void export(MenuComponent menu, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(componentToJson(menu, 0));
        }
    }

    private String componentToJson(MenuComponent comp, int indent) {
        StringBuilder sb = new StringBuilder();
        String ind = " ".repeat(indent);

        if (comp instanceof MenuItem) {
            MenuItem item = (MenuItem) comp;
            sb.append(ind).append("{\n")
                    .append(ind).append("  \"name\": \"").append(item.getName()).append("\",\n")
                    .append(ind).append("  \"price\": ").append(item.getPrice()).append(",\n")
                    .append(ind).append("  \"allergies\": [");
            List<String> codes = item.getAllergies().stream().map(Enum::name).toList();
            if (!codes.isEmpty()) {
                sb.append("\"").append(String.join("\", \"", codes)).append("\"");
            }
            sb.append("]\n")
                    .append(ind).append("}");
        } else {
            MenuCategory cat = (MenuCategory) comp;
            sb.append(ind).append("{\n")
                    .append(ind).append("  \"category\": \"").append(cat.getName()).append("\",\n")
                    .append(ind).append("  \"items\": [\n");
            List<MenuComponent> kids = cat.getChildren();
            for (int i = 0; i < kids.size(); i++) {
                sb.append(componentToJson(kids.get(i), indent + 4));
                if (i < kids.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(ind).append("  ]\n")
                    .append(ind).append("}");
        }
        return sb.toString();
    }
}