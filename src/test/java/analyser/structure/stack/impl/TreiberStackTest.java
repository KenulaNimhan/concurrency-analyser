package analyser.structure.stack.impl;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class TreiberStackTest {

    TreiberStack<Integer> underTest = new TreiberStack<>(5);

    @Test
    void shouldGiveCorrectSizeOfStack() throws InterruptedException {
        // arrange
        underTest.push(2);
        underTest.push(3);

        // assert
        assertThat(underTest.size()).isEqualTo(2);
    }

    @Test
    void shouldGiveNull_WhenTryToPop_WhenEmpty() {
        // assert
        assertThat(underTest.pop()).isNull();
    }

    @Test
    void shouldIndicateWhenEmpty() {
        // assert
        assertThat(underTest.isEmpty()).isTrue();
    }

    @Test
    void shouldIndicateWhenNotEmpty() throws InterruptedException {
        // arrange
        underTest.push(5);
        underTest.push(4);
        underTest.push(2);

        // assert
        assertThat(underTest.isEmpty()).isFalse();
    }

    @Test
    void accountingMethodIsLIFO() throws InterruptedException {
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