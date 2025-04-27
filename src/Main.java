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
        List<MenuCategory> fullCategories = List.of(
                new MenuCategory("Starters"),
                new MenuCategory("Main Courses"),
                new MenuCategory("Desserts"),
                new MenuCategory("Drinks")
        );
        for (MenuCategory cat : fullCategories) {
            System.out.println("Enter items for category: " + cat.getName());
            while (true) {
                System.out.print("  Item name (or 'done'): ");
                String name = scanner.nextLine().trim();
                if ("done".equalsIgnoreCase(name)) break;

                double price = readPrice(scanner, "  Price: ");

                System.out.println("  Allergy codes (comma-separated):");
                System.out.println("    A) Gluten   B) Dairy   C) Nuts   D) Shellfish   E) None");
                Set<Allergy> allergies = readAllergies(scanner, allergyMap);

                cat.add(new MenuItem(name, price, allergies));
            }
        }

        // Build full menu composite
        MenuCategory fullMenu = new MenuCategory("All Items");
        fullCategories.forEach(fullMenu::add);

        // 2) Show full menu
        System.out.println("\n=== Full Menu ===");
        fullMenu.print();

        // 3) Prepare structured custom menu
        MenuCategory customMenu = new MenuCategory("Your Final Menu");
        Map<String,MenuCategory> customMap = new LinkedHashMap<>();
        for (MenuCategory fc : fullCategories) {
            MenuCategory cc = new MenuCategory(fc.getName());
            // seed with initial items
            for (MenuComponent item : fc.getChildren()) {
                cc.add(item);
            }
            customMap.put(fc.getName(), cc);
            customMenu.add(cc);
        }

        // 4) Browse & manage
        outer:
        while (true) {
            System.out.print("\nEnter category to browse (or 'done'): ");
            String c = scanner.nextLine().trim();
            if ("done".equalsIgnoreCase(c)) break;

            // find full & custom categories
            MenuCategory fullCat = fullCategories.stream()
                    .filter(cat -> cat.getName().equalsIgnoreCase(c))
                    .findFirst().orElse(null);
            if (fullCat == null) {
                System.out.println("  → Unknown category.");
                continue;
            }
            MenuCategory customCat = customMap.get(fullCat.getName());

            // show the single, up-to-date menu for this category
            System.out.println("\n== " + customCat.getName() + " ==");
            customCat.print();

            // choose action
            System.out.println("Actions: [A]dd  [E]dit  [R]emove  [B]ack");
            System.out.print("Choose action: ");
            String act = scanner.nextLine().trim().toUpperCase();

            switch (act) {
                case "A" -> {
                    System.out.print("  Item name to add: ");
                    String newName = scanner.nextLine().trim();
                    MenuItem existing = fullCat.getChildren().stream()
                            .filter(i -> i instanceof MenuItem)
                            .map(i -> (MenuItem)i)
                            .filter(i -> i.getName().equalsIgnoreCase(newName))
                            .findFirst().orElse(null);
                    if (existing != null) {
                        customCat.add(existing);
                        System.out.println("  + Added existing \"" + newName + "\"");
                    } else {
                        System.out.println("  \"" + newName + "\" is new; creating it.");
                        double newPrice = readPrice(scanner, "    Price: ");
                        System.out.println("    Allergy codes (comma-separated):");
                        System.out.println("      A) Gluten   B) Dairy   C) Nuts   D) Shellfish   E) None");
                        Set<Allergy> newAll = readAllergies(scanner, allergyMap);

                        MenuItem mi = new MenuItem(newName, newPrice, newAll);
                        fullCat.add(mi);
                        customCat.add(mi);
                        System.out.println("    + Created & added \"" + newName + "\"");
                    }
                }
                case "E" -> {
                    System.out.print("  Item name to edit: ");
                    String eName = scanner.nextLine().trim();
                    MenuItem toEdit = customCat.getChildren().stream()
                            .filter(i -> i instanceof MenuItem)
                            .map(i -> (MenuItem)i)
                            .filter(i -> i.getName().equalsIgnoreCase(eName))
                            .findFirst().orElse(null);
                    if (toEdit == null) {
                        System.out.println("  → No such item in your selection.");
                    } else {
                        double newPrice = readPrice(scanner, "    New price: ");
                        System.out.println("    Allergy codes (comma-separated):");
                        System.out.println("      A) Gluten   B) Dairy   C) Nuts   D) Shellfish   E) None");
                        Set<Allergy> newAll = readAllergies(scanner, allergyMap);

                        customCat.remove(toEdit);
                        MenuItem updated = new MenuItem(toEdit.getName(), newPrice, newAll);
                        customCat.add(updated);
                        System.out.println("    * Edited \"" + eName + "\"");
                    }
                }
                case "R" -> {
                    System.out.print("  Item name to remove: ");
                    String rName = scanner.nextLine().trim();
                    MenuItem toRemove = customCat.getChildren().stream()
                            .filter(i -> i instanceof MenuItem)
                            .map(i -> (MenuItem)i)
                            .filter(i -> i.getName().equalsIgnoreCase(rName))
                            .findFirst().orElse(null);
                    if (toRemove == null) {
                        System.out.println("  → No such item in your selection.");
                    } else {
                        customCat.remove(toRemove);
                        System.out.println("  - Removed \"" + rName + "\"");
                    }
                }
                case "B" -> {
                    continue outer;
                }
                default -> {
                    System.out.println("  → Invalid action.");
                }
            }
        }

        // 5) Show final structured menu
        customMenu.print();

        // 6) Export
        System.out.print("\nChoose export format [1] PlainText  [2] JSON: ");
        ExportStrategy strat = "2".equals(scanner.nextLine().trim())
                ? new JsonExportStrategy()
                : new PlainTextExportStrategy();

        System.out.print("Enter output file path: ");
        String path = scanner.nextLine().trim();
        new MenuExporter(strat).exportMenu(customMenu, path);

        scanner.close();
    }

    // Repeated utility for robust price input
    private static double readPrice(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("    → Invalid number. Try again.");
            }
        }
    }

    // Robust allergy input, warns on invalid codes
    private static Set<Allergy> readAllergies(Scanner sc,
                                              Map<String,Allergy> map) {
        while (true) {
            System.out.print("      Enter codes [A–E]: ");
            String line = sc.nextLine().trim().toUpperCase();
            if (line.isEmpty()) {
                System.out.println("      → Please enter at least one code.");
                continue;
            }
            String[] codes = line.split("\\s*,\\s*");
            Set<Allergy> set = new HashSet<>();
            boolean invalid = false;
            boolean hasNone = false;

            for (String c : codes) {
                Allergy a = map.get(c);
                if (a == null) {
                    System.out.println("      → Invalid code: " + c);
                    invalid = true;
                } else if (a == Allergy.NONE) {
                    hasNone = true;
                } else {
                    set.add(a);
                }
            }
            if (invalid) {
                // some code was not A–E
                continue;
            }
            if (hasNone && !set.isEmpty()) {
                // None cannot be combined
                System.out.println("      → 'None' (E) cannot be combined with other codes.");
                continue;
            }
            if (hasNone) {
                return Collections.emptySet();
            }
            return set;
        }
    }
}