package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.indicators.helpers.NzIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Trend Trigger Factor (TTF) indicator by everget. <a
 * https://www.tradingview.com/script/WA9Z4CV2-Trend-Trigger-Factor/">TradingView</a>
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
        Indicator<Num> buyPower = CombineIndicator.minus(highestValue, previousLowestValue);
        Indicator<Num> sellPower = CombineIndicator.minus(previousHighestValue, lowestValue);
        Indicator<Num> difference = CombineIndicator.minus(buyPower, sellPower);
        Indicator<Num> differenceScaled = TransformIndicator.multiply(difference, 200);
        Indicator<Num> sum = CombineIndicator.plus(buyPower, sellPower);
        this.ttfIndicator = new NzIndicator(CombineIndicator.divide(differenceScaled, sum));
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
