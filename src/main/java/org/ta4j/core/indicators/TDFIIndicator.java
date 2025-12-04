package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.AverageIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.indicators.numeric.UnaryOperationIndicator;
import org.ta4j.core.num.Num;

/**
 * Trend Direction Force Index (TDFI) by causecelebre.
 * <a href="https://www.tradingview.com/script/HUpIful1-Trend-Direction-Force-Index-v2-TDFI-wm/">TradingView</a>
 * The MA mode configuration was not added, this implementation only supports EMA.
 */
public class TDFIIndicator extends AbstractIndicator<Num> {
    private final Indicator<Num> tdfiIndicator;
    private final int unstableBars;

    public TDFIIndicator(BarSeries series, int lookback, int mmaLength, int smmaLength, int nLength) {
        this(new ClosePriceIndicator(series), lookback, mmaLength, smmaLength, nLength);
    }

    public TDFIIndicator(Indicator<Num> indicator, int lookback, int mmaLength, int smmaLength, int nLength) {
        super(indicator.getBarSeries());

        Indicator<Num> mmaIndicator = new EMAIndicator(BinaryOperationIndicator.product(indicator, 1000), mmaLength);
        Indicator<Num> smmaIndicator = new EMAIndicator(mmaIndicator, smmaLength);
        BinaryOperationIndicator impetMmaIndicator = BinaryOperationIndicator.difference(mmaIndicator, new PreviousValueIndicator(mmaIndicator));
        BinaryOperationIndicator impetSmmaIndicator = BinaryOperationIndicator.difference(smmaIndicator, new PreviousValueIndicator(smmaIndicator));
        UnaryOperationIndicator divMa = UnaryOperationIndicator.abs(BinaryOperationIndicator.difference(mmaIndicator, smmaIndicator));
        AverageIndicator averimpet = new AverageIndicator(impetMmaIndicator, impetSmmaIndicator);
        BinaryOperationIndicator tdf = BinaryOperationIndicator.product(divMa, UnaryOperationIndicator.pow(averimpet, nLength));
        this.tdfiIndicator = BinaryOperationIndicator.quotient(tdf, new HighestValueIndicator(UnaryOperationIndicator.abs(tdf), lookback * nLength));
        this.unstableBars = Math.max(Math.max(mmaLength, smmaLength), lookback * nLength);
    }

    @Override
    public Num getValue(int index) {
        return tdfiIndicator.getValue(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return unstableBars;
    }
}
