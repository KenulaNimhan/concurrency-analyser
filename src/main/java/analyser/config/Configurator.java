package analyser.config;

import java.util.Scanner;

public class Configurator {
    private static final Configurable testData = new Configurable();
    private static final Scanner scan = new Scanner(System.in);

    public Configurable configure() {
        // gathering input from user to configure
        System.out.print("how many items needed to be produced & consumed: ");
        var toProduce = scan.nextInt();
        System.out.print("how many producer threads: ");
        var producerCount = scan.nextInt();
        System.out.print("how many consumer threads: ");
        var consumerCount = scan.nextInt();
        System.out.print("max no. of elements the data structure can hold: ");
        var stackHeight = scan.nextInt();
        System.out.println("estimated size of the object that is being stacked or queued (in bytes): ");
        var elementSize = scan.nextInt();
        System.out.println("estimated scale of operations that would be performed in each iteration: ");
        var operationalScale = scan.nextInt();

        testData.setCap(stackHeight)
                .setToProduce(toProduce)
                .setToConsume(toProduce)
                .setProducerCount(producerCount)
                .setConsumerCount(consumerCount)
                .setElementSize(elementSize)
                .setOperationalScale(operationalScale);

        return testData;
    }

}
