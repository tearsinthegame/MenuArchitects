import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite in the Composite pattern: a category that can contain items or other categories.
 */
public class MenuCategory extends MenuComponent {
    private final String name;
    private final List<MenuComponent> children = new ArrayList<>();

    public MenuCategory(String name) {
        this.name = name;
    }

    @Override
    public void add(MenuComponent component) {
        children.add(component);
    }

    @Override
    public void remove(MenuComponent component) {
        children.remove(component);
    }

    @Override
    public MenuComponent getChild(int i) {
        return children.get(i);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void print() {
        System.out.println("\n== " + name + " ==");
        for (MenuComponent c : children) {
            c.print();
        }
    }

    public List<MenuComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }
}