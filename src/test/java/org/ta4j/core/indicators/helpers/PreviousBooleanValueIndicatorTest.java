package org.ta4j.core.indicators.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.mocks.MockBarSeriesBuilder;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

public class PreviousBooleanValueIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {

    private BarSeries series;

    public PreviousBooleanValueIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() {
        // 5 bars – close-price values are irrelevant for Boolean logic
        series = new MockBarSeriesBuilder().withNumFactory(numFactory).withData(1.0, 2.0, 3.0, 4.0, 5.0).build();
    }

    // ---------------------------------------------------------------
    // helper: create a fixed Boolean indicator backed by our series
    // ---------------------------------------------------------------

    private Indicator<Boolean> fixed(boolean... values) {
        return new CachedIndicator<>(series) {
            @Override
            protected Boolean calculate(int index) {
                return values[index];
            }

            @Override
            public int getCountOfUnstableBars() {
                return 0;
            }
        };
    }

    // ---------------------------------------------------------------
    // Default constructor (n = 1)
    // ---------------------------------------------------------------

    @Test
    public void defaultConstructorReturnsFalseAtIndexZero() {
        // no bar before index 0 → always false
        var prev = new PreviousBooleanValueIndicator(fixed(true, true, true, true, true));
        assertFalse("index 0 has no previous bar – must return false", prev.getValue(0));
    }

    @Test
    public void defaultConstructorReturnsValueAtPreviousIndex() {
        // source: [T, F, T, F, T]
        // prev(1): [false, T, F, T, F]
        var prev = new PreviousBooleanValueIndicator(fixed(true, false, true, false, true));
        assertFalse(prev.getValue(0)); // no previous
        assertTrue(prev.getValue(1));  // source[0] = T
        assertFalse(prev.getValue(2)); // source[1] = F
        assertTrue(prev.getValue(3));  // source[2] = T
        assertFalse(prev.getValue(4)); // source[3] = F
    }

    @Test
    public void defaultConstructorAllFalseSource() {
        // source: all-false → prev(1): [false, F, F, F, F] (still all false)
        var prev = new PreviousBooleanValueIndicator(fixed(false, false, false, false, false));
        for (int i = 0; i < 5; i++) {
            assertFalse("prev(1) of all-false must be false at index " + i, prev.getValue(i));
        }
    }

    // ---------------------------------------------------------------
    // Explicit n = 1 matches default constructor exactly
    // ---------------------------------------------------------------

    @Test
    public void explicitN1BehavesIdenticalToDefaultConstructor() {
        var source = fixed(false, true, false, true, false);
        var withDefault = new PreviousBooleanValueIndicator(source);
        var withExplicit = new PreviousBooleanValueIndicator(source, 1);
        for (int i = 0; i < 5; i++) {
            assertEquals("explicit n=1 must match default constructor at index " + i, withDefault.getValue(i),
                    withExplicit.getValue(i));
        }
    }

    // ---------------------------------------------------------------
    // n = 2
    // ---------------------------------------------------------------

    @Test
    public void n2ReturnsFalseForFirstTwoBars() {
        var prev = new PreviousBooleanValueIndicator(fixed(true, true, true, true, true), 2);
        assertFalse("index 0 → previousIndex -2 → false", prev.getValue(0));
        assertFalse("index 1 → previousIndex -1 → false", prev.getValue(1));
    }

    @Test
    public void n2ReturnsCorrectPreviousValues() {
        // source: [T, F, T, F, T]
        // prev(2): [false, false, T, F, T]
        var prev = new PreviousBooleanValueIndicator(fixed(true, false, true, false, true), 2);
        assertFalse(prev.getValue(0)); // -2 → false
        assertFalse(prev.getValue(1)); // -1 → false
        assertTrue(prev.getValue(2));  // source[0] = T
        assertFalse(prev.getValue(3)); // source[1] = F
        assertTrue(prev.getValue(4));  // source[2] = T
    }

    // ---------------------------------------------------------------
    // n = 3
    // ---------------------------------------------------------------

    @Test
    public void n3ReturnsFalseForFirstThreeBars() {
        var prev = new PreviousBooleanValueIndicator(fixed(true, true, true, true, true), 3);
        assertFalse(prev.getValue(0));
        assertFalse(prev.getValue(1));
        assertFalse(prev.getValue(2));
    }

    @Test
    public void n3ReturnsCorrectPreviousValues() {
        // source: [T, F, F, T, F]
        // prev(3): [false, false, false, T, F]
        var prev = new PreviousBooleanValueIndicator(fixed(true, false, false, true, false), 3);
        assertFalse(prev.getValue(0));
        assertFalse(prev.getValue(1));
        assertFalse(prev.getValue(2));
        assertTrue(prev.getValue(3));  // source[0] = T
        assertFalse(prev.getValue(4)); // source[1] = F
    }

    // ---------------------------------------------------------------
    // getCountOfUnstableBars()
    // ---------------------------------------------------------------

    @Test
    public void getCountOfUnstableBarsIsOneForDefaultConstructor() {
        var prev = new PreviousBooleanValueIndicator(fixed(true, true, true, true, true));
        assertEquals(1, prev.getCountOfUnstableBars());
    }

    @Test
    public void getCountOfUnstableBarsEqualsN() {
        var source = fixed(true, true, true, true, true);
        assertEquals(1, new PreviousBooleanValueIndicator(source, 1).getCountOfUnstableBars());
        assertEquals(2, new PreviousBooleanValueIndicator(source, 2).getCountOfUnstableBars());
        assertEquals(3, new PreviousBooleanValueIndicator(source, 3).getCountOfUnstableBars());
        assertEquals(4, new PreviousBooleanValueIndicator(source, 4).getCountOfUnstableBars());
    }

    // ---------------------------------------------------------------
    // Invalid n → IllegalArgumentException
    // ---------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenNIsZero() {
        new PreviousBooleanValueIndicator(fixed(true, true, true, true, true), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenNIsNegative() {
        new PreviousBooleanValueIndicator(fixed(true, true, true, true, true), -3);
    }

    // ---------------------------------------------------------------
    // toString()
    // ---------------------------------------------------------------

    @Test
    public void toStringWithN1OmitsParentheses() {
        var prev = new PreviousBooleanValueIndicator(fixed(true, true, true, true, true));
        String str = prev.toString();
        assertTrue("must start with class name", str.startsWith("PreviousBooleanValueIndicator["));
        assertFalse("n=1 must NOT include (1) in toString", str.contains("(1)"));
    }

    @Test
    public void toStringWithN2IncludesParentheses() {
        var prev = new PreviousBooleanValueIndicator(fixed(true, true, true, true, true), 2);
        assertTrue("n=2 must include (2) in toString", prev.toString().contains("(2)"));
    }

    @Test
    public void toStringWithN3IncludesParentheses() {
        var prev = new PreviousBooleanValueIndicator(fixed(true, true, true, true, true), 3);
        assertTrue("n=3 must include (3) in toString", prev.toString().contains("(3)"));
    }
}
