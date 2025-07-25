package org.ta4j.core.indicators.helpers;

import java.util.Arrays;
import java.util.List;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Indicator to calculate the average of a list of other indicators.
 */
public class AverageIndicator extends CachedIndicator<Num> {
    private final List<Indicator<Num>> indicators;

    public AverageIndicator(Indicator<Num>... indicators) {
        super(indicators[0]);

        this.indicators = Arrays.asList(indicators);
    }

    @Override protected Num calculate(int index) {
        Num value = getBarSeries().numFactory().zero();

        for (Indicator<Num> indicator : indicators) {
            value = value.plus(indicator.getValue(index));
        }

        return value.dividedBy(getBarSeries().numFactory().numOf(indicators.size()));
    }

    @Override public int getCountOfUnstableBars() {
        return indicators.stream().mapToInt(Indicator::getCountOfUnstableBars).max().getAsInt();
    }
}
