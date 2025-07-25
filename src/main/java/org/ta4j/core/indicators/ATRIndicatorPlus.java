package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.MMAIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.averages.WMAIndicator;
import org.ta4j.core.indicators.helpers.TRIndicator;
import org.ta4j.core.num.Num;

/**
 * Average true range (ATR) indicator with more flexible configuration.
 */
public class ATRIndicatorPlus extends AbstractIndicator<Num> {
    private final Indicator<Num> atrIndicator;

    public ATRIndicatorPlus(BarSeries series, int barCount) {
        this(series, barCount, Input.MMA);
    }

    public ATRIndicatorPlus(BarSeries series, int barCount, Input input) {
        super(series);

        TRIndicator trIndicator = new TRIndicator(series);

        switch (input) {
        case SMA:
            this.atrIndicator = new SMAIndicator(trIndicator, barCount);
            break;
        case EMA:
            this.atrIndicator = new EMAIndicator(trIndicator, barCount);
            break;
        case MMA:
            this.atrIndicator = new MMAIndicator(trIndicator, barCount);
            break;
        case WMA:
            this.atrIndicator = new WMAIndicator(trIndicator, barCount);
            break;
        case VWMA:
            this.atrIndicator = new VWMAIndicator(trIndicator, barCount);
            break;
        case VAWMA:
            this.atrIndicator = new VAWMAIndicator(trIndicator, barCount);
            break;
        default:
            throw new IllegalArgumentException("Input " + input + " not supported");
        }
    }

    @Override public Num getValue(int index) {
        return atrIndicator.getValue(index);
    }

    @Override public int getCountOfUnstableBars() {
        return atrIndicator.getCountOfUnstableBars();
    }

    public enum Input {
        SMA, EMA, MMA, WMA, VWMA, VAWMA
    }
}
