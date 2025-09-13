package analyser.config;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Configurator {
    private static final Configurable testData = new Configurable();
    private static final Scanner scan = new Scanner(System.in);

    public Configurable configure() {
        // gathering input from user to configure

        askForAmountToProduce();
        askForProducerCount();
        askForConsumerCount();
        askForCapacityOfStructure();
        askForElementSize();
        askForComputeScale();

        return testData;
    }

    private void askForAmountToProduce() {
        while (true) {
            try {
                System.out.print("how many items needed to be produced & consumed: ");
                var amt = scan.nextInt();
                testData.setToProduce(amt);
                testData.setToConsume(amt);
                break;
            } catch (InputMismatchException e) {
                System.out.println("invalid entry!");
                scan.nextLine();
            }
        }
    }

    private void askForProducerCount() {
        while (true) {
            try {
                System.out.print("how many producer threads: ");
                testData.setProducerCount(scan.nextInt());
                break;
            } catch (InputMismatchException e) {
                System.out.println("invalid entry!");
                scan.nextLine();
            }
        }
    }

    private void askForConsumerCount() {
        while (true) {
            try {
                System.out.print("how many consumer threads: ");
                testData.setConsumerCount(scan.nextInt());
                break;
            } catch (InputMismatchException e) {
                System.out.println("invalid entry!");
                scan.nextLine();
            }
        }
    }

    private void askForCapacityOfStructure() {
        while (true) {
            try {
                System.out.print("max no. of elements the data structure can hold: ");
                testData.setCap(scan.nextInt());
                break;
            } catch (InputMismatchException e) {
                System.out.println("invalid entry!");
                scan.nextLine();
            }
        }
    }

    private void askForElementSize() {
        while (true) {
            try {
                System.out.println("size per element stacked (in bytes)");
                System.out.print(" 1 - 2048: ");
                testData.setElementSize(scan.nextInt());
                break;
            } catch (InputMismatchException e) {
                System.out.println("invalid entry!");
                scan.nextLine();
            }
        }
    }

    private void askForComputeScale() {
        while (true) {
            try {
                System.out.println("estimated CPU usage for each operation");
                System.out.print("choose on a scale from 1 - 10: ");
                testData.setOperationalScale(scan.nextInt());
                break;
            } catch (InputMismatchException e) {
                System.out.println("invalid entry!");
                scan.nextLine();
            }
        }
    }

}
