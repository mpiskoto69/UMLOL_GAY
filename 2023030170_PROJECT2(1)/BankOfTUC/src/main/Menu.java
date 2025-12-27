package main;

import java.util.List;
import java.util.Scanner;

public class Menu   { 
	
/** Throw this to break out of the menu loop */
public static class ExitMenuException extends RuntimeException {}

private final String title;
private final List<Option> options;
private final Scanner in = new Scanner(System.in);

public Menu(String title, List<Option> options) {
    this.title   = title;
    this.options = options;
}

/** Display, read choice, invoke execute(); loop until ExitMenuException */
public void run() {
    while (true) {
        System.out.println("\n--- " + title + " ---");
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%2d) %s%n", i + 1, options.get(i).getLabel());
        }
        System.out.print("> ");
        String line = in.nextLine().trim();
        try {
            int idx = Integer.parseInt(line) - 1;
            options.get(idx).execute();
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid choice");
        } catch (ExitMenuException eme) { //save 
            break;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
}