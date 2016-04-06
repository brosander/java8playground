package com.github.java8playground;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by bryan on 4/6/16.
 */
@RepeatableAnnotation("Pentaho")
@RepeatableAnnotation("Demo")
public class RepeatableAnnotationTest {
    @Test
    public void testRepeatableAnnotations() {
        boolean sawPentaho = false;
        boolean sawDemo = false;

        for (RepeatableAnnotation repeatableAnnotation : RepeatableAnnotationTest.class.getAnnotation(RepeatableAnnotations.class).value()) {
            System.out.println(repeatableAnnotation);
            if (repeatableAnnotation.value().equals("Pentaho")) {
                sawPentaho = true;
            } else if (repeatableAnnotation.value().equals("Demo")) {
                sawDemo = true;
            }
        }
        assertTrue(sawPentaho);
        assertTrue(sawDemo);
    }
}
