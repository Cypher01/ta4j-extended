package org.ta4j.core.indicators.gchannel;

import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * G-Channel Trend Detection indicator by jaggedsoft.
 * <a href="https://www.tradingview.com/script/smADlDdP-G-Channel-Trend-Detection/">TradingView</a>
 */
public class GChannelAverageIndicator extends AbstractIndicator<Num> {
	private final GChannelUpperBandIndicator upperBandIndicator;
	private final GChannelLowerBandIndicator lowerBandIndicator;
	private final TransformIndicator averageIndicator;

	GChannelAverageIndicator(GChannelUpperBandIndicator upperBandIndicator, GChannelLowerBandIndicator lowerBandIndicator) {
		super(upperBandIndicator.getBarSeries());
		this.upperBandIndicator = upperBandIndicator;
		this.lowerBandIndicator = lowerBandIndicator;
		this.averageIndicator = TransformIndicator.divide(CombineIndicator.plus(upperBandIndicator, lowerBandIndicator), 2);
	}

	public boolean bullish(int index) {
		int barSinceLastUpperBreakout = 0;
		int barSinceLastLowerBreakout = 0;

		for (int i = index; i > 0; i--) {
			if (upperBandIndicator.breakout(i)) {
				barSinceLastUpperBreakout = index - i;
				break;
			}
		}

		for (int i = index; i > 0; i--) {
			if (lowerBandIndicator.breakout(i)) {
				barSinceLastLowerBreakout = index - i;
				break;
			}
		}

		return barSinceLastUpperBreakout <= barSinceLastLowerBreakout;
	}

	@Override
	public Num getValue(int index) {
		return averageIndicator.getValue(index);
	}
}
