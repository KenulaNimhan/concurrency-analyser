package analyser.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceMetricsTest {

    PerformanceMetrics underTest = new PerformanceMetrics("underTest");

    @Test
    void testThroughputCalculation() {
        // arrange
        underTest.setTotalTime(500);
        for (int i=0; i<250; i++) underTest.incrementProducedCount();
        for (int i=0; i<250; i++) underTest.incrementConsumedCount();

        // assert
        assertThat(underTest.calculateThroughput()).isEqualTo(1);
    }

    @Test
    void testAvgLatencyCalculation() {
        // arrange
        double expectedVal = (double) 1 / 1000;

        for (int i=0; i<1000; i++) {
            underTest.addLatency(1000);
        }

        // assert
        assertThat(underTest.getAvgLatency()).isEqualTo(expectedVal);
    }

    @Test
    void shouldIndicateIfContainsDuplicates() {
        // arrange
        List<String> values = List.of("1", "2", "2");

        // assert
        assertThat(underTest.hasDuplicates(values)).isTrue();
    }

    @Test
    void shouldIndicateIfNoDuplicates() {
        // arrange
        List<String> values = List.of("1", "2", "3");

        // assert
        assertThat(underTest.hasDuplicates(values)).isFalse();
    }
}