import java.io.IOException;

/**
 * Strategy interface for exporting a menu.
 */
public interface ExportStrategy {
    void export(MenuComponent menu, String filePath) throws IOException;
}

