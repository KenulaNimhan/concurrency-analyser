package analyser.structure.stack;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NaiveSyncStackTest {

    NaiveSyncStack<Integer> underTest = new NaiveSyncStack<>(5);

    @Test
    void shouldGiveCorrectCapacityEvenWhenEmpty() {
        // assert
        assertThat(underTest.cap()).isEqualTo(5);
    }

    @Test
    void shouldGiveCorrectCapacityWhenContainsElements() {
        // arrange
        underTest.push(2);

        // assert
        assertThat(underTest.cap()).isEqualTo(5);
    }

    @Test
    void shouldGiveCorrectSizeOfStack() {
        // arrange
        underTest.push(2);
        underTest.push(3);

        // assert
        assertThat(underTest.size()).isEqualTo(2);
    }

    @Test
    void shouldThrowException_TryToPush_WhenFull() {
        // arrange
        for (int i=0; i<5; i++) {
            underTest.push(i);
        }

        // assert
        assertThatThrownBy(() -> underTest.push(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("stack is full");

    }

    @Test
    void shouldThrowException_TryToPop_WhenEmpty() {
        // assert
        assertThatThrownBy(() -> underTest.pop())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("stack is empty");
    }

    @Test
    void shouldIndicateWhenEmpty() {
        // assert
        assertThat(underTest.isEmpty()).isTrue();
    }

    @Test
    void shouldIndicateWhenNotEmpty() {
        // arrange
        underTest.push(5);
        underTest.push(4);
        underTest.push(2);

        // assert
        assertThat(underTest.isEmpty()).isFalse();
    }

    @Test
    void accountingMethodIsLIFO() {
        // arrange
        int[] orderToProduce = new int[] {1, 2, 3, 4, 5};

        // populate the stack
        for (int val: orderToProduce) {
            underTest.push(val);
        }

        // de-populate the stack
        ArrayList<Integer> consumedOrder = new ArrayList<>(5);
        while (!underTest.isEmpty()) {
            consumedOrder.addLast(underTest.pop());
        }

        // assert
        for (int i=0; i<orderToProduce.length; i++) {
            assertThat(orderToProduce[i]).isEqualTo(consumedOrder.reversed().get(i));
        }

    }
}