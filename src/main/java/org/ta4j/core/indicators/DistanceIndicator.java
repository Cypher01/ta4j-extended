package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.num.Num;

/**
 * Distance between two indicators (indicator1 - indicator2) / indicator2. This is based on DistanceFromMAIndicator, but
 * more generic, with a simplified calculation, and without the limitation to moving averages only.
 */
public class DistanceIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> distance;
    private final int unstableBars;

    public DistanceIndicator(Indicator<Num> indicator1, Indicator<Num> indicator2) {
        super(indicator1.getBarSeries());

        this.distance = CombineIndicator.divide(CombineIndicator.minus(indicator1, indicator2), indicator2);
        this.unstableBars = Math.max(indicator1.getCountOfUnstableBars(), indicator2.getCountOfUnstableBars());
    }

    @Override public Num getValue(int index) {
        return distance.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return unstableBars;
    }
}
