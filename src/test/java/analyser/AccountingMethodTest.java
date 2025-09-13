package analyser;

import org.junit.jupiter.api.Test;
import analyser.structure.stack.SyncStack;
import analyser.util.Element;
import analyser.util.StackPerformanceMetrics;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountingMethodTest {

    StackPerformanceMetrics underTest = new StackPerformanceMetrics("underTest");
    SyncStack<Element> stack = new SyncStack<>(10);

    @Test
    void shouldBeLifo() throws Exception {

        for(int i=0; i<5; i++) {
            var element = new Element();
            stack.push(element);
            underTest.addToProducedData(String.valueOf(element.getUniqueID()));
        }

        for (int i=0; i<5; i++) {
            underTest.addToConsumedData(String.valueOf(stack.pop().getUniqueID()));
        }

        assertThat(underTest.isLifo()).isTrue();

    }

    @Test
    void shouldNotBeLifo() throws Exception {
        for(int i=0; i<5; i++) {
            var element = new Element();
            stack.push(element);
            underTest.addToProducedData(String.valueOf(element.getUniqueID()));
        }

        for (int i=0; i<4; i++) {
            underTest.addToConsumedData(String.valueOf(stack.pop().getUniqueID()));
        }

        // adding a different element to the consumed list.
        underTest.addToConsumedData(String.valueOf(UUID.randomUUID()));

        assertThat(underTest.isLifo()).isFalse();
    }
}
