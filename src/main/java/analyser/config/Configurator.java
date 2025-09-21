package analyser.config;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Configurator {
    private static final Configuration testData = new Configuration();
    private static final Scanner scan = new Scanner(System.in);

    private static final Map<ConfigKey, Consumer<Integer>> configurableActions = Map.of(
            ConfigKey.TO_PRODUCE, testData::setToProduce,
            ConfigKey.PRODUCER_COUNT, testData::setProducerCount,
            ConfigKey.CONSUMER_COUNT, testData::setConsumerCount,
            ConfigKey.CAP, testData::setCap,
            ConfigKey.ELEMENT_SIZE, testData::setElementSize,
            ConfigKey.OPERATIONAL_SCALE, testData::setOperationalScale
    );

    public Configuration configure() {

        for (ConfigKey configurable: ConfigKey.values()) {
            var action = configurableActions.get(configurable);
            int userInput = askFor(configurable.getPrompt(), configurable.getRange()[0], configurable.getRange()[1]);
            if (userInput == -1) {
                System.out.println("""
                --------------------------------------------------------
                ___________THANK YOU FOR USING THE APPLICATION__________
                """);
                System.exit(0);
            }
            action.accept(userInput);
        }
        return testData;
    }

    private int askFor(String prompt, int rangeStart, int rangeEnd) {
        int userInput;
        while (true) {
            try {
                System.out.println(prompt);
                System.out.print(rangeStart+" - "+rangeEnd+": ");
                userInput = scan.nextInt();
                if (userInput ==-1) break;
                if (userInput < rangeStart || userInput > rangeEnd) {
                    System.out.println("value out of range");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("invalid entry");
                scan.nextLine();
            }
        }
        return userInput;
    }
}
