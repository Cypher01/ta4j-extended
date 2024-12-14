package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Kairi Relative Index (KRI).
 * <a href="https://www.tradingview.com/script/xzRPAboO-Indicator-Kairi-Relative-Index-KRI/">TradingView</a>
 */
public class KRIIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> kriIndicator;
    private final int barCount;

    public KRIIndicator(BarSeries series, int barCount) {
        this(new ClosePriceIndicator(series), barCount);
    }

    public KRIIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());

        Indicator<Num> smaIndicator = new SMAIndicator(indicator, barCount);
        Indicator<Num> difference = CombineIndicator.minus(indicator, smaIndicator);
        Indicator<Num> quotient = CombineIndicator.divide(difference, smaIndicator);
        this.kriIndicator = TransformIndicator.multiply(quotient, 100);
        this.barCount = barCount;
    }

    @Override public Num getValue(int index) {
        return kriIndicator.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return barCount;
    }
}
