import java.io.IOException;

/**
 * Context for the Strategy pattern.
 */
public class MenuExporter {
    private ExportStrategy strategy;

    public MenuExporter(ExportStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(ExportStrategy strategy) {
        this.strategy = strategy;
    }

    public void exportMenu(MenuComponent menu, String filePath) {
        try {
            strategy.export(menu, filePath);
            System.out.println("Menu successfully exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }
}
