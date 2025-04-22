/**
 * Base component in the Composite pattern.
 */
public abstract class MenuComponent {
    public void add(MenuComponent component) {
        throw new UnsupportedOperationException();
    }
    public void remove(MenuComponent component) {
        throw new UnsupportedOperationException();
    }
    public MenuComponent getChild(int i) {
        throw new UnsupportedOperationException();
    }
    public String getName() {
        throw new UnsupportedOperationException();
    }
    public double getPrice() {
        throw new UnsupportedOperationException();
    }
    public void print() {
        throw new UnsupportedOperationException();
    }
}
