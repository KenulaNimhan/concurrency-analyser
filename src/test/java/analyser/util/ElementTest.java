package analyser.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ElementTest {

    Element underTest = new Element(100);

    @Test
    void shouldPerformNoComputation() {
        // act
        underTest.compute(0);

        // assert
        for (int i: underTest.getArray()) {
            assertThat(i).isEqualTo(0);
        }
    }

    @Test
    void shouldCompute50Percent() {
        // act
        underTest.compute(5);

        int count=0;
        for (int i=0; i<underTest.getArray().length; i++) {
            if (underTest.getArray()[i] == 1) count++;
        }

        // assert
        assertThat(count).isEqualTo(50);
    }

    @Test
    void shouldCompute100Percent() {
        // act
        underTest.compute(10);

        int count=0;
        for (int i=0; i<underTest.getArray().length; i++) {
            if (underTest.getArray()[i] == 1) count++;
        }

        // assert
        assertThat(count).isEqualTo(100);
    }
}