package site.visualizer.model;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import site.visualizer.StackPerformanceMetrics;

import java.util.ArrayList;
import java.util.Collections;


public class Main {
    public static void main(String[] args) {

        var testMetrics = new StackPerformanceMetrics<Integer>("Synchronised Stack");

        testMetrics.incrementProducedCount();
        testMetrics.incrementProducedCount();

        testMetrics.incrementConsumedCount();
        testMetrics.incrementConsumedCount();



        System.out.println(testMetrics);
    }

}
