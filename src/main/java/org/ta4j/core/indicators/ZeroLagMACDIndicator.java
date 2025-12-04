package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

/**
 * Zero Lag MACD (ZLMACD) indicator by @yassotreyo
 * <a href="https://www.tradingview.com/v/GcbLcwG7/">TradingView</a>
 */
public class ZeroLagMACDIndicator extends CachedIndicator<Num> {

    private final NumericIndicator zeroLagMACD;
    private final NumericIndicator signalLine;

    public ZeroLagMACDIndicator(Indicator<Num> indicator, int fastLength, int slowLength, int signalLength) {
        super(indicator);

        // FAST LINE
        EMAIndicator ema1 = new EMAIndicator(indicator, fastLength);
        EMAIndicator ema2 = new EMAIndicator(ema1, fastLength);
        NumericIndicator demaFast = NumericIndicator.of(ema1).multipliedBy(2).minus(ema2);

        // SLOW LINE
        EMAIndicator emas1 = new EMAIndicator(indicator, slowLength);
        EMAIndicator emas2 = new EMAIndicator(emas1, slowLength);
        NumericIndicator demaSlow = NumericIndicator.of(emas1).multipliedBy(2).minus(emas2);

        // MACD LINE
        zeroLagMACD = demaFast.minus(demaSlow);

        // SIGNAL LINE
        EMAIndicator emasig1 = new EMAIndicator(zeroLagMACD, signalLength);
        EMAIndicator emasig2 = new EMAIndicator(emasig1, signalLength);
        signalLine = NumericIndicator.of(emasig1).multipliedBy(2).minus(emasig2);
    }

    @Override
    protected Num calculate(int index) {
        return zeroLagMACD.getValue(index);
    }

    public NumericIndicator getSignalLine() {
        return signalLine;
    }

    public NumericIndicator getHistogram() {
        return zeroLagMACD.minus(signalLine);
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}