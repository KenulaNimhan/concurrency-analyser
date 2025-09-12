package site.analyser.model.stack;
import org.junit.jupiter.api.Test;
import site.analyser.structure.stack.BasicStack;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicStackTest {

    BasicStack<Integer> underTest = new BasicStack<>(5);

    @Test
    void shouldReturnCorrectCap() {
        // act & assert
        assertThat(underTest.cap()).isEqualTo(5);
    }

    @Test
    void shouldReturnCorrectSize() {
        // act
        underTest.push(10);
        underTest.push(20);

        // assert
        assertThat(underTest.size()).isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionWhenPushingBeyondCap() {
        // act
        for(int i=0; i<5; i++) {
            underTest.push(i);
        }

        // assert
        assertThatThrownBy(() -> underTest.push(40))
                .hasMessageContaining("stack is full")
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldThrowExceptionWhenPoppingEmptyStack() {
        // act & assert
        assertThatThrownBy(underTest::pop)
                .hasMessageContaining("stack is empty")
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldPopTopElement() {
        // act
        underTest.push(10);
        underTest.push(23);

        // assert
        assertThat(underTest.pop()).isEqualTo(23);
    }

    @Test
    void shouldPeekTheTopElement() {
        // act
        underTest.push(10);
        underTest.push(20);

        // assert
        assertThat(underTest.peek()).isEqualTo(20);
    }

    @Test
    void shouldThrowExceptionWhenPeekingEmptyStack() {
        // act & assert
        assertThatThrownBy(underTest::peek)
                .hasMessageContaining("stack is empty")
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldIndicateStackIsEmptyOrNot() {
        // assert
        assertThat(underTest.isEmpty()).isTrue();
    }
}