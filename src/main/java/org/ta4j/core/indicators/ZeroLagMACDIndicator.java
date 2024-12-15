package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.ZLEMAIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

/**
 * Based on the MACD indicator, the ZeroLagMACD indicator is a trend indicator that is used to identify the direction of
 * the market. It is calculated by subtracting the long-term ZLEMA from the short-term ZLEMA.
 */
public class ZeroLagMACDIndicator extends CachedIndicator<Num> {

    private final ZLEMAIndicator shortTermZLEma;
    private final ZLEMAIndicator longTermZLEma;

    /**
     * Constructor with:
     *
     * <ul>
     * <li>{@code shortBarCount} = 12
     * <li>{@code longBarCount} = 26
     * </ul>
     *
     * @param indicator the {@link Indicator}
     */
    public ZeroLagMACDIndicator(Indicator<Num> indicator) {
        this(indicator, 12, 26);
    }

    /**
     * Constructor.
     *
     * @param indicator     the {@link Indicator}
     * @param shortBarCount the short time frame (normally 12)
     * @param longBarCount  the long time frame (normally 26)
     */
    public ZeroLagMACDIndicator(Indicator<Num> indicator, int shortBarCount, int longBarCount) {
        super(indicator);
        if (shortBarCount > longBarCount) {
            throw new IllegalArgumentException("Long term period count must be greater than short term period count");
        }
        this.shortTermZLEma = new ZLEMAIndicator(indicator, shortBarCount);
        this.longTermZLEma = new ZLEMAIndicator(indicator, longBarCount);
    }

    /**
     * @return the Short term EMA indicator
     */
    public ZLEMAIndicator getShortTermZLEma() {
        return shortTermZLEma;
    }

    /**
     * @return the Long term EMA indicator
     */
    public ZLEMAIndicator getLongTermZLEma() {
        return longTermZLEma;
    }

    /**
     * @param barCount of signal line
     *
     * @return signal line for this MACD indicator
     */
    public ZLEMAIndicator getSignalLine(int barCount) {
        return new ZLEMAIndicator(this, barCount);
    }

    /**
     * @param barCount of signal line
     *
     * @return histogram of this MACD indicator
     */
    public NumericIndicator getHistogram(int barCount) {
        return NumericIndicator.of(this).minus(getSignalLine(barCount));
    }

    @Override
    protected Num calculate(int index) {
        return shortTermZLEma.getValue(index).minus(longTermZLEma.getValue(index));
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}
