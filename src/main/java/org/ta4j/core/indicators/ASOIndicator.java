package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.num.Num;

/**
 * Average Sentiment Oscillator (ASO) indicator by KivancOzbilgic.
 * <a href="https://www.tradingview.com/script/hz1PKu3G/">TradingView</a>
 * <p>
 * The implementation is simplified. Only the bull value is calculated. As the oscillator is a percentage, the bear
 * value can be calculated by 100 minus bull value.
 */
public class ASOIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> asoIndicator;

    public ASOIndicator(BarSeries series, int barCount) {
        super(series);

        OpenPriceIndicator open = new OpenPriceIndicator(series);
        HighPriceIndicator high = new HighPriceIndicator(series);
        LowPriceIndicator low = new LowPriceIndicator(series);
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        BinaryOperationIndicator intrarange = BinaryOperationIndicator.difference(new HighPriceIndicator(series), low);
        LowestValueIndicator grouplow = new LowestValueIndicator(low, barCount);
        HighestValueIndicator grouphigh = new HighestValueIndicator(high, barCount);
        PreviousValueIndicator groupopen = new PreviousValueIndicator(open, barCount - 1);
        BinaryOperationIndicator grouprange = BinaryOperationIndicator.difference(grouphigh, grouplow);
        SubstituteIndicator k1 = new SubstituteIndicator(intrarange, 0, 1);
        SubstituteIndicator k2 = new SubstituteIndicator(grouprange, 0, 1);

        AverageIndicator intrabaravg = new AverageIndicator(BinaryOperationIndicator.difference(close, low),
                BinaryOperationIndicator.difference(high, open));
        BinaryOperationIndicator intrabarbulls = BinaryOperationIndicator.quotient(BinaryOperationIndicator.product(intrabaravg, 100), k1);
        AverageIndicator groupavg = new AverageIndicator(BinaryOperationIndicator.difference(close, grouplow),
                BinaryOperationIndicator.difference(grouphigh, groupopen));
        BinaryOperationIndicator groupbulls = BinaryOperationIndicator.difference(BinaryOperationIndicator.product(groupavg, 100), k2);

        this.asoIndicator = new SMAIndicator(new AverageIndicator(intrabarbulls, groupbulls), barCount);
    }

    @Override
    public Num getValue(int index) {
        return asoIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return asoIndicator.getCountOfUnstableBars();
    }
}
