package analyser.structure.queue.impl;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SyncQueueTest {

    SyncQueue<Integer> underTest = new SyncQueue<>(5);

    @Test
    void shouldGiveCorrectCapacityEvenWhenEmpty() {
        // assert
        assertThat(underTest.cap()).isEqualTo(5);
    }

    @Test
    void shouldGiveCorrectCapacityWhenContainsElements() throws InterruptedException {
        // arrange
        underTest.enqueue(2);

        // assert
        assertThat(underTest.cap()).isEqualTo(5);
    }

    @Test
    void shouldGiveCorrectSizeOfQueue() throws InterruptedException {
        // arrange
        underTest.enqueue(2);
        underTest.enqueue(3);

        // assert
        assertThat(underTest.size()).isEqualTo(2);
    }


    @Test
    void shouldIndicateWhenEmpty() {
        // assert
        assertThat(underTest.isEmpty()).isTrue();
    }

    @Test
    void shouldIndicateWhenNotEmpty() throws InterruptedException {
        // arrange
        underTest.enqueue(5);
        underTest.enqueue(4);
        underTest.enqueue(2);

        // assert
        assertThat(underTest.isEmpty()).isFalse();
    }

    @Test
    void accountingMethodIsFIFO() throws InterruptedException {
        // arrange
        int[] orderToProduce = new int[] {1, 2, 3, 4, 5};

        // populate the stack
        for (int val: orderToProduce) {
            underTest.enqueue(val);
        }

        // de-populate the stack
        ArrayList<Integer> consumedOrder = new ArrayList<>(5);
        while (!underTest.isEmpty()) {
            consumedOrder.addLast(underTest.dequeue());
        }

        // assert
        for (int i=0; i<orderToProduce.length; i++) {
            assertThat(orderToProduce[i]).isEqualTo(consumedOrder.get(i));
        }

    }
}