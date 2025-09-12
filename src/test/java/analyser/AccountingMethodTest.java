package analyser;

import org.junit.jupiter.api.Test;
import analyser.structure.stack.AdvSyncStack;
import analyser.util.Element;
import analyser.util.StackPerformanceMetrics;


import static org.assertj.core.api.Assertions.assertThat;

public class AccountingMethodTest {

    StackPerformanceMetrics underTest = new StackPerformanceMetrics("underTest");
    AdvSyncStack<Element> stack = new AdvSyncStack<>(10);

    @Test
    void shouldBeLifo() throws Exception {

        for(int i=0; i<5; i++) {
            var element = new Element();
            stack.push(element);
            underTest.addToProducedData(element);
        }

        for (int i=0; i<5; i++) {
            underTest.addToConsumedData(stack.pop());
        }

        assertThat(underTest.isLifo()).isTrue();

    }

    @Test
    void shouldNotBeLifo() throws Exception {
        for(int i=0; i<5; i++) {
            var element = new Element();
            stack.push(element);
            underTest.addToProducedData(element);
        }

        for (int i=0; i<4; i++) {
            underTest.addToConsumedData(stack.pop());
        }

        // adding a different element to the consumed list.
        underTest.addToConsumedData(new Element());

        assertThat(underTest.isLifo()).isFalse();
    }
}
