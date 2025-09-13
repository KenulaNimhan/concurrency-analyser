package analyser.config;

import analyser.util.ConfigKey;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Configurator {
    private static final Configuration testData = new Configuration();
    private static final Scanner scan = new Scanner(System.in);

    private static final Consumer<Integer> actionOfSettingToProduce = testData::setToProduce;
    private static final Consumer<Integer> actionOfSettingProducerCount = testData::setProducerCount;
    private static final Consumer<Integer> actionOfSettingConsumerCount = testData::setConsumerCount;
    private static final Consumer<Integer> actionOfSettingStackCap = testData::setCap;
    private static final Consumer<Integer> actionOfSettingElementSize = testData::setElementSize;
    private static final Consumer<Integer> actionOfSettingOperationalScale = testData::setOperationalScale;

    private static final Map<ConfigKey, Consumer<Integer>> configurableActions = Map.of(
            ConfigKey.TO_PRODUCE, actionOfSettingToProduce,
            ConfigKey.PRODUCER_COUNT, actionOfSettingProducerCount,
            ConfigKey.CONSUMER_COUNT, actionOfSettingConsumerCount,
            ConfigKey.CAP, actionOfSettingStackCap,
            ConfigKey.ELEMENT_SIZE, actionOfSettingElementSize,
            ConfigKey.OPERATIONAL_SCALE, actionOfSettingOperationalScale
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
