package org.ta4j.core.indicators.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicatorTest;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.mocks.MockBarSeriesBuilder;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

@SuppressWarnings("unchecked")
public class LogicIndicatorTest extends AbstractIndicatorTest<Indicator<Num>, Num> {

    private BarSeries series;

    public LogicIndicatorTest(NumFactory numFactory) {
        super(numFactory);
    }

    @Before
    public void setUp() {
        // 4 bars – close-price values are irrelevant for Boolean logic
        series = new MockBarSeriesBuilder()
                .withNumFactory(numFactory)
                .withData(1.0, 2.0, 3.0, 4.0)
                .build();
    }

    // ---------------------------------------------------------------
    // helper: create a fixed Boolean indicator backed by our series
    // ---------------------------------------------------------------

    /** Returns an indicator that emits the given values per index, unstableBars = 0. */
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

    /** Returns an indicator with a configurable unstableBars count. */
    private Indicator<Boolean> fixedWithUnstable(int unstable, boolean... values) {
        return new CachedIndicator<>(series) {
            @Override
            protected Boolean calculate(int index) {
                return values[index];
            }

            @Override
            public int getCountOfUnstableBars() {
                return unstable;
            }
        };
    }

    // ---------------------------------------------------------------
    // not()
    // ---------------------------------------------------------------

    @Test
    public void notInvertsTrueToFalse() {
        LogicIndicator not = LogicIndicator.not(fixed(true, true, true, true));
        for (int i = 0; i < 4; i++) {
            assertFalse("not(true) must be false at index " + i, not.getValue(i));
        }
    }

    @Test
    public void notInvertsFalseToTrue() {
        LogicIndicator not = LogicIndicator.not(fixed(false, false, false, false));
        for (int i = 0; i < 4; i++) {
            assertTrue("not(false) must be true at index " + i, not.getValue(i));
        }
    }

    @Test
    public void notAlternatingValues() {
        // [T, F, T, F] → [F, T, F, T]
        LogicIndicator not = LogicIndicator.not(fixed(true, false, true, false));
        assertFalse(not.getValue(0));
        assertTrue(not.getValue(1));
        assertFalse(not.getValue(2));
        assertTrue(not.getValue(3));
    }

    // ---------------------------------------------------------------
    // and() – varargs
    // ---------------------------------------------------------------

    @Test
    public void andVarargsAllTrueReturnsTrue() {
        LogicIndicator and = LogicIndicator.and(
                fixed(true, true, true, true),
                fixed(true, true, true, true));
        for (int i = 0; i < 4; i++) {
            assertTrue("and(T,T) must be true at index " + i, and.getValue(i));
        }
    }

    @Test
    public void andVarargsOneFalseReturnsFalse() {
        // ind1: [T, T, F, F]  ind2: [T, F, T, F] → [T, F, F, F]
        LogicIndicator and = LogicIndicator.and(
                fixed(true, true, false, false),
                fixed(true, false, true, false));
        assertTrue(and.getValue(0));
        assertFalse(and.getValue(1));
        assertFalse(and.getValue(2));
        assertFalse(and.getValue(3));
    }

    @Test
    public void andVarargsAllFalseReturnsFalse() {
        LogicIndicator and = LogicIndicator.and(
                fixed(false, false, false, false),
                fixed(false, false, false, false));
        for (int i = 0; i < 4; i++) {
            assertFalse("and(F,F) must be false at index " + i, and.getValue(i));
        }
    }

    // ---------------------------------------------------------------
    // and() – List overload (3 indicators)
    // ---------------------------------------------------------------

    @Test
    public void andListAllTrueReturnsTrue() {
        LogicIndicator and = LogicIndicator.and(List.of(
                fixed(true, true, true, true),
                fixed(true, true, true, true),
                fixed(true, true, true, true)));
        for (int i = 0; i < 4; i++) {
            assertTrue("and(T,T,T) must be true at index " + i, and.getValue(i));
        }
    }

    @Test
    public void andListOneFalseReturnsFalse() {
        // ind1: [T,T,T,F]  ind2: [T,T,F,F]  ind3: [T,F,F,F] → [T,F,F,F]
        LogicIndicator and = LogicIndicator.and(List.of(
                fixed(true, true, true, false),
                fixed(true, true, false, false),
                fixed(true, false, false, false)));
        assertTrue(and.getValue(0));
        assertFalse(and.getValue(1));
        assertFalse(and.getValue(2));
        assertFalse(and.getValue(3));
    }

    // ---------------------------------------------------------------
    // or() – varargs
    // ---------------------------------------------------------------

    @Test
    public void orVarargsAllFalseReturnsFalse() {
        LogicIndicator or = LogicIndicator.or(
                fixed(false, false, false, false),
                fixed(false, false, false, false));
        for (int i = 0; i < 4; i++) {
            assertFalse("or(F,F) must be false at index " + i, or.getValue(i));
        }
    }

    @Test
    public void orVarargsOneTrueReturnsTrue() {
        // ind1: [T,T,F,F]  ind2: [T,F,T,F] → [T,T,T,F]
        LogicIndicator or = LogicIndicator.or(
                fixed(true, true, false, false),
                fixed(true, false, true, false));
        assertTrue(or.getValue(0));
        assertTrue(or.getValue(1));
        assertTrue(or.getValue(2));
        assertFalse(or.getValue(3));
    }

    @Test
    public void orVarargsAllTrueReturnsTrue() {
        LogicIndicator or = LogicIndicator.or(
                fixed(true, true, true, true),
                fixed(true, true, true, true));
        for (int i = 0; i < 4; i++) {
            assertTrue("or(T,T) must be true at index " + i, or.getValue(i));
        }
    }

    // ---------------------------------------------------------------
    // or() – List overload (3 indicators)
    // ---------------------------------------------------------------

    @Test
    public void orListAllFalseReturnsFalse() {
        LogicIndicator or = LogicIndicator.or(List.of(
                fixed(false, false, false, false),
                fixed(false, false, false, false),
                fixed(false, false, false, false)));
        for (int i = 0; i < 4; i++) {
            assertFalse("or(F,F,F) must be false at index " + i, or.getValue(i));
        }
    }

    @Test
    public void orListOneTrueReturnsTrue() {
        // ind1: [F,T,F,F]  ind2: [F,F,T,F]  ind3: [F,F,F,F] → [F,T,T,F]
        LogicIndicator or = LogicIndicator.or(List.of(
                fixed(false, true, false, false),
                fixed(false, false, true, false),
                fixed(false, false, false, false)));
        assertFalse(or.getValue(0));
        assertTrue(or.getValue(1));
        assertTrue(or.getValue(2));
        assertFalse(or.getValue(3));
    }

    // ---------------------------------------------------------------
    // getCountOfUnstableBars()
    // ---------------------------------------------------------------

    @Test
    public void unstableBarsIsZeroWhenAllWrappedAreZero() {
        LogicIndicator and = LogicIndicator.and(
                fixed(true, true, true, true),
                fixed(true, true, true, true));
        assertEquals(0, and.getCountOfUnstableBars());
    }

    @Test
    public void unstableBarsReturnsMaxForAnd() {
        LogicIndicator and = LogicIndicator.and(
                fixedWithUnstable(3, true, true, true, true),
                fixedWithUnstable(7, true, true, true, true));
        assertEquals(7, and.getCountOfUnstableBars());
    }

    @Test
    public void unstableBarsReturnsMaxForOr() {
        LogicIndicator or = LogicIndicator.or(
                fixedWithUnstable(5, true, true, true, true),
                fixedWithUnstable(2, true, true, true, true));
        assertEquals(5, or.getCountOfUnstableBars());
    }

    @Test
    public void unstableBarsForNotDelegates() {
        LogicIndicator not = LogicIndicator.not(
                fixedWithUnstable(4, true, true, true, true));
        assertEquals(4, not.getCountOfUnstableBars());
    }

    @Test
    public void unstableBarsReturnsMaxAcrossThreeIndicators() {
        LogicIndicator and = LogicIndicator.and(List.of(
                fixedWithUnstable(1, true, true, true, true),
                fixedWithUnstable(9, true, true, true, true),
                fixedWithUnstable(4, true, true, true, true)));
        assertEquals(9, and.getCountOfUnstableBars());
    }

    // ---------------------------------------------------------------
    // Custom predicate (public constructor)
    // ---------------------------------------------------------------

    @Test
    public void customNoneMatchPredicateOnAllFalse() {
        // noneMatch on all-false → every entry is true (none are true)
        LogicIndicator none = new LogicIndicator(
                List.of(fixed(false, false, false, false),
                        fixed(false, false, false, false)),
                list -> list.stream().noneMatch(v -> v));
        for (int i = 0; i < 4; i++) {
            assertTrue("noneMatch(F,F) must be true at index " + i, none.getValue(i));
        }
    }

    @Test
    public void customNoneMatchPredicateOnAllTrue() {
        // noneMatch on all-true → every entry is false (some are true)
        LogicIndicator none = new LogicIndicator(
                List.of(fixed(true, true, true, true)),
                list -> list.stream().noneMatch(v -> v));
        for (int i = 0; i < 4; i++) {
            assertFalse("noneMatch(T) must be false at index " + i, none.getValue(i));
        }
    }

    @Test
    public void customExactlyOneTruePredicate() {
        // ind1: [T,T,F,F]  ind2: [F,T,T,F] → exactly-one: [T,F,T,F]
        LogicIndicator exactlyOne = new LogicIndicator(
                List.of(fixed(true, true, false, false),
                        fixed(false, true, true, false)),
                list -> list.stream().filter(v -> v).count() == 1);
        assertTrue(exactlyOne.getValue(0));   // T,F → 1
        assertFalse(exactlyOne.getValue(1));  // T,T → 2
        assertTrue(exactlyOne.getValue(2));   // F,T → 1
        assertFalse(exactlyOne.getValue(3));  // F,F → 0
    }
}

