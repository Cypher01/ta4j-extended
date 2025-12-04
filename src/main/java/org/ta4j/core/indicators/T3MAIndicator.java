package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.num.Num;

/**
 * T3 moving average (T3MA) indicator by HPotter.
 * <a href="https://www.tradingview.com/script/qzoC9H1I-T3-Average/">TradingView</a>
 */
public class T3MAIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> t3maIndicator;
    private final int barCount;

    public T3MAIndicator(BarSeries series, int barCount) {
        this(new ClosePriceIndicator(series), barCount);
    }

    public T3MAIndicator(Indicator<Num> indicator, int barCount) {
        super(indicator.getBarSeries());

        EMAIndicator xe1 = new EMAIndicator(indicator, barCount);
        EMAIndicator xe2 = new EMAIndicator(xe1, barCount);
        EMAIndicator xe3 = new EMAIndicator(xe2, barCount);
        EMAIndicator xe4 = new EMAIndicator(xe3, barCount);
        EMAIndicator xe5 = new EMAIndicator(xe4, barCount);
        EMAIndicator xe6 = new EMAIndicator(xe5, barCount);

        double b = 0.7;
        Num c1 = getBarSeries().numFactory().numOf(-b * b * b);
        Num c2 = getBarSeries().numFactory().numOf(3 * b * b + 3 * b * b * b);
        Num c3 = getBarSeries().numFactory().numOf(-6 * b * b - 3 * b - 3 * b * b * b);
        Num c4 = getBarSeries().numFactory().numOf(1 + 3 * b + b * b * b + 3 * b * b);

        BinaryOperationIndicator addend1 = BinaryOperationIndicator.product(xe6, c1.doubleValue());
        BinaryOperationIndicator addend2 = BinaryOperationIndicator.product(xe5, c2.doubleValue());
        BinaryOperationIndicator addend3 = BinaryOperationIndicator.product(xe4, c3.doubleValue());
        BinaryOperationIndicator addend4 = BinaryOperationIndicator.product(xe3, c4.doubleValue());

        this.t3maIndicator = BinaryOperationIndicator.sum(
                BinaryOperationIndicator.sum(BinaryOperationIndicator.sum(addend1, addend2), addend3), addend4);
        this.barCount = barCount;
    }

    @Override
    public Num getValue(int index) {
        return t3maIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return barCount;
    }
}
