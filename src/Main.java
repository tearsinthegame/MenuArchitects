import java.util.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Map letter → Allergy
        Map<String,Allergy> allergyMap = Map.of(
                "A", Allergy.GLUTEN,
                "B", Allergy.DAIRY,
                "C", Allergy.NUTS,
                "D", Allergy.SHELLFISH,
                "E", Allergy.NONE
        );

        // 1) Populate fixed categories
        List<MenuCategory> categories = List.of(
                new MenuCategory("Starters"),
                new MenuCategory("Main Courses"),
                new MenuCategory("Desserts"),
                new MenuCategory("Drinks")
        );

        for (MenuCategory cat : categories) {
            System.out.println("Enter items for category: " + cat.getName());
            while (true) {
                System.out.print("  Item name (or 'done'): ");
                String name = scanner.nextLine();
                if ("done".equalsIgnoreCase(name)) break;

                System.out.print("  Price: ");
                double price = Double.parseDouble(scanner.nextLine());

                System.out.println("  Allergy codes (comma‑separated):");
                System.out.println("    A) Gluten   B) Dairy   C) Nuts   D) Shellfish   E) None");
                Set<Allergy> allergies = new HashSet<>();
                while (true) {
                    System.out.print("  Enter codes [A–E]: ");
                    String line = scanner.nextLine().toUpperCase();
                    String[] codes = line.split("\\s*,\\s*");
                    boolean hasNone = Arrays.asList(codes).contains("E");
                    for (String code : codes) {
                        Allergy a = allergyMap.get(code);
                        if (a != null && a != Allergy.NONE) {
                            allergies.add(a);
                        }
                    }
                    if (hasNone) {
                        allergies.clear();
                    }
                    if (!codes[0].isBlank()) break;
                }

                cat.add(new MenuItem(name, price, allergies));
            }
        }

        // 2) Build & show full menu
        MenuCategory fullMenu = new MenuCategory("All Items");
        categories.forEach(fullMenu::add);
        System.out.println("\n=== Full Menu ===");
        fullMenu.print();

        // 3) Build custom menu
        MenuCategory customMenu = new MenuCategory("Your Custom Menu");
        while (true) {
            System.out.print("\nEnter category to browse (or 'done'): ");
            String c = scanner.nextLine();
            if ("done".equalsIgnoreCase(c)) break;

            Optional<MenuComponent> oc = fullMenu.getChildren().stream()
                    .filter(m -> m.getName().equalsIgnoreCase(c))
                    .findFirst();
            if (oc.isEmpty() || !(oc.get() instanceof MenuCategory chosenCat)) {
                System.out.println("  → Unknown category.");
                continue;
            }
            System.out.println("\n  Items in " + chosenCat.getName() + ":");
            chosenCat.print();

            System.out.print("  Enter item name to add (or 'back'): ");
            String itemName = scanner.nextLine();
            if ("back".equalsIgnoreCase(itemName)) continue;

            boolean added = chosenCat.getChildren().stream()
                    .filter(i -> i instanceof MenuItem)
                    .filter(i -> i.getName().equalsIgnoreCase(itemName))
                    .peek(customMenu::add)
                    .findFirst()
                    .isPresent();
            System.out.println(added
                    ? "    + Added " + itemName
                    : "    → Unknown item.");
        }

        // 4) Show custom menu
        System.out.println("\n=== Your Final Menu ===");
        customMenu.print();

        // 5) Export
        System.out.print("\nChoose export format [1] PlainText  [2] JSON: ");
        ExportStrategy strat = "2".equals(scanner.nextLine())
                ? new JsonExportStrategy()
                : new PlainTextExportStrategy();
        System.out.print("Enter output file path: ");
        String path = scanner.nextLine();

        new MenuExporter(strat).exportMenu(customMenu, path);
        scanner.close();
    }
}