package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.Num;

/**
 * Volume weighted moving average (VWMA) indicator, based on PineScript v5 Reference Manual.
 * <a href="https://www.tradingview.com/pine-script-reference/v5/#fun_ta{dot}vwma">TradingView</a>
 */
public class VWMAIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> vwmaIndicator;

	public VWMAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator.getBarSeries());
		VolumeIndicator volumeIndicator = new VolumeIndicator(indicator.getBarSeries());
		CombineIndicator inputTimesVolume = CombineIndicator.multiply(indicator, volumeIndicator);
		SMAIndicator smaIndicator1 = new SMAIndicator(inputTimesVolume, barCount);
		SMAIndicator smaIndicator2 = new SMAIndicator(volumeIndicator, barCount);
		this.vwmaIndicator = CombineIndicator.divide(smaIndicator1, smaIndicator2);
	}

	@Override
	public Num getValue(int index) {
		return vwmaIndicator.getValue(index);
	}
}
