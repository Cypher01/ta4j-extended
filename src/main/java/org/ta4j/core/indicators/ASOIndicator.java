package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.AverageIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.SubstituteIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Average Sentiment Oscillator (ASO) indicator by KivancOzbilgic.
 * <a href="https://www.tradingview.com/script/hz1PKu3G/">TradingView</a>
 *
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

        CombineIndicator intrarange = CombineIndicator.minus(new HighPriceIndicator(series), low);
        LowestValueIndicator grouplow = new LowestValueIndicator(low, barCount);
        HighestValueIndicator grouphigh = new HighestValueIndicator(high, barCount);
        PreviousValueIndicator groupopen = new PreviousValueIndicator(open, barCount - 1);
        CombineIndicator grouprange = CombineIndicator.minus(grouphigh, grouplow);
        SubstituteIndicator k1 = new SubstituteIndicator(intrarange, 0, 1);
        SubstituteIndicator k2 = new SubstituteIndicator(grouprange, 0, 1);

        AverageIndicator intrabaravg = new AverageIndicator(CombineIndicator.minus(close, low),
                CombineIndicator.minus(high, open));
        CombineIndicator intrabarbulls = CombineIndicator.divide(TransformIndicator.multiply(intrabaravg, 100), k1);
        AverageIndicator groupavg = new AverageIndicator(CombineIndicator.minus(close, grouplow),
                CombineIndicator.minus(grouphigh, groupopen));
        CombineIndicator groupbulls = CombineIndicator.divide(TransformIndicator.multiply(groupavg, 100), k2);

        this.asoIndicator = new SMAIndicator(new AverageIndicator(intrabarbulls, groupbulls), barCount);
    }

    @Override public Num getValue(int index) {
        return asoIndicator.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return asoIndicator.getCountOfUnstableBars();
    }
}
