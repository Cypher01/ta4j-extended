package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.num.Num;

/**
 * Trend Trigger Factor (TTF) indicator by everget. <a
 * href="https://www.tradingview.com/script/WA9Z4CV2-Trend-Trigger-Factor/">TradingView</a>
 */
public class TTFIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> ttfIndicator;
    private final int barCount;

    public TTFIndicator(BarSeries series, int barCount) {
        this(new ClosePriceIndicator(series), barCount);
    }

    public TTFIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());

        Indicator<Num> highestValue = new HighestValueIndicator(new HighPriceIndicator(indicator.getBarSeries()),
                barCount);
        Indicator<Num> lowestValue = new LowestValueIndicator(new LowPriceIndicator(indicator.getBarSeries()),
                barCount);
        Indicator<Num> previousHighestValue = new NzIndicator(new PreviousValueIndicator(highestValue, barCount));
        Indicator<Num> previousLowestValue = new NzIndicator(new PreviousValueIndicator(lowestValue, barCount));
        Indicator<Num> buyPower = BinaryOperationIndicator.difference(highestValue, previousLowestValue);
        Indicator<Num> sellPower = BinaryOperationIndicator.difference(previousHighestValue, lowestValue);
        Indicator<Num> difference = BinaryOperationIndicator.difference(buyPower, sellPower);
        Indicator<Num> differenceScaled = BinaryOperationIndicator.product(difference, 200);
        Indicator<Num> sum = BinaryOperationIndicator.sum(buyPower, sellPower);
        this.ttfIndicator = new NzIndicator(BinaryOperationIndicator.quotient(differenceScaled, sum));
        this.barCount = barCount;
    }

    @Override
    public Num getValue(int index) {
        return ttfIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
