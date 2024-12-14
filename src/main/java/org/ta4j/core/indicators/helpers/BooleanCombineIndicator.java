package org.ta4j.core.indicators.helpers;

import java.util.function.BiPredicate;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Boolean combine indicator. This is based on BooleanTransformIndicator with the same logical operators, but instead of
 * comparing one indicator to a constant, it compares two indicators. Additionally, the indicator supports converting
 * true and false to 1 and 0.
 */
public class BooleanCombineIndicator extends CachedIndicator<Boolean> {
    private final Indicator<Num> indicator1;
    private final Indicator<Num> indicator2;
    private final BiPredicate<Num, Num> transform;

    public static BooleanCombineIndicator isEqual(Indicator<Num> indicator1, Indicator<Num> indicator2) {
        return new BooleanCombineIndicator(indicator1, indicator2, Num::isEqual);
    }

    public static BooleanCombineIndicator isGreaterThan(Indicator<Num> indicator1, Indicator<Num> indicator2) {
        return new BooleanCombineIndicator(indicator1, indicator2, Num::isGreaterThan);
    }

    public static BooleanCombineIndicator isGreaterThanOrEqual(Indicator<Num> indicator1, Indicator<Num> indicator2) {
        return new BooleanCombineIndicator(indicator1, indicator2, Num::isGreaterThanOrEqual);
    }

    public static BooleanCombineIndicator isLessThan(Indicator<Num> indicator1, Indicator<Num> indicator2) {
        return new BooleanCombineIndicator(indicator1, indicator2, Num::isLessThan);
    }

    public static BooleanCombineIndicator isLessThanOrEqual(Indicator<Num> indicator1, Indicator<Num> indicator2) {
        return new BooleanCombineIndicator(indicator1, indicator2, Num::isLessThanOrEqual);
    }

    public BooleanCombineIndicator(Indicator<Num> indicator1, Indicator<Num> indicator2,
            BiPredicate<Num, Num> transform) {
        super(indicator1);

        this.indicator1 = indicator1;
        this.indicator2 = indicator2;
        this.transform = transform;
    }

    @Override protected Boolean calculate(int index) {
        return transform.test(indicator1.getValue(index), indicator2.getValue(index));
    }

    @Override public int getCountOfUnstableBars() {
        return Math.max(indicator1.getCountOfUnstableBars(), indicator2.getCountOfUnstableBars());
    }

    public BooleanToNumIndicator asNum() {
        return new BooleanToNumIndicator(this);
    }

    public static class BooleanToNumIndicator extends CachedIndicator<Num> {
        private final BooleanCombineIndicator indicator;

        public BooleanToNumIndicator(BooleanCombineIndicator indicator) {
            super(indicator);

            this.indicator = indicator;
        }

        @Override protected Num calculate(int index) {
            return indicator.getValue(index) ? getBarSeries().numFactory().one() : getBarSeries().numFactory().zero();
        }

        @Override public int getCountOfUnstableBars() {
            return indicator.getCountOfUnstableBars();
        }
    }
}
