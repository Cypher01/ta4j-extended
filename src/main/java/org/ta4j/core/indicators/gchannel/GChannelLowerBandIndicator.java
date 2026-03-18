package org.ta4j.core.indicators.gchannel;

import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.num.Num;

/**
 * G-Channel Trend Detection indicator by jaggedsoft.
 * <a href="https://www.tradingview.com/script/smADlDdP-G-Channel-Trend-Detection/">TradingView</a>
 */
public class GChannelLowerBandIndicator extends AbstractIndicator<Num> {

    private final GChannelBaseIndicator baseIndicator;

    GChannelLowerBandIndicator(GChannelBaseIndicator baseIndicator) {
        super(baseIndicator.getBarSeries());
        this.baseIndicator = baseIndicator;
    }

    @Override
    public Num getValue(int index) {
        return baseIndicator.getValue(index)[1];
    }

    @Override
    public int getCountOfUnstableBars() {
        return baseIndicator.getCountOfUnstableBars();
    }
}
