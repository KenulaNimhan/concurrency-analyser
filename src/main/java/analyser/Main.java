package analyser;

import analyser.config.Configuration;
import analyser.config.Configurator;
import analyser.use.UseQueue;
import analyser.use.UseStack;
import analyser.use.User;
import analyser.util.constant.StructureType;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scan = new Scanner(System.in);
    private static final Configurator configurator = new Configurator();
    private static StructureType selectedStructure;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("""
                ---------------------------------------------------
                    WELCOME TO CONCURRENCY PERFORMANCE ANALYSER
                ---------------------------------------------------
                enter -1 at any time to exit.
                set configuration settings;
                
                Select the data structure you want to use:
                """);

        selectStructure();

        // configuring test data
        Configuration testData = configurator.configure();

        User user;
        if (selectedStructure == StructureType.STACK) {
            user = new UseStack(testData);
        } else {
            user = new UseQueue(testData);
        }
        user.use();
    }

    private static void selectStructure() {
        int userInput=0;
        while (true) {
            try {
                System.out.print("1 - Stack | 2 - Queue: ");
                userInput = scan.nextInt();
                if (userInput == -1) {
                    System.out.println("""
                            -----------------------------------
                            THANK YOU FOR USING THE APPLICATION
                            -----------------------------------
                            """);
                    System.exit(0);
                } else if (userInput ==1 || userInput ==2) break;
                else {
                    System.out.println("invalid entry");
                }

            } catch (InputMismatchException e) {
                System.out.println("invalid input");
                scan.nextLine();
            }
        }
        if (userInput == 1)
            selectedStructure = StructureType.STACK;
        else {
            selectedStructure = StructureType.QUEUE;
        }
    }
}
