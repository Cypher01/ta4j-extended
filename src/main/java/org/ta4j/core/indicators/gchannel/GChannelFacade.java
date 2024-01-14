package org.ta4j.core.indicators.gchannel;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

/**
 * G-Channel Trend Detection indicator by jaggedsoft.
 * <a href="https://www.tradingview.com/script/smADlDdP-G-Channel-Trend-Detection/">TradingView</a>
 */
public class GChannelFacade {
	private final GChannelUpperBandIndicator upperBandIndicator;
	private final GChannelLowerBandIndicator lowerBandIndicator;
	private final GChannelAverageIndicator averageIndicator;

	public GChannelFacade(BarSeries series, int barCount) {
		this(new ClosePriceIndicator(series), barCount);
	}

	public GChannelFacade(Indicator<Num> indicator, int barCount) {
		this.upperBandIndicator = new GChannelUpperBandIndicator(indicator, barCount);
		this.lowerBandIndicator = new GChannelLowerBandIndicator(indicator, barCount);
		this.upperBandIndicator.setLowerBandIndicator(this.lowerBandIndicator);
		this.lowerBandIndicator.setUpperBandIndicator(this.upperBandIndicator);
		this.averageIndicator = new GChannelAverageIndicator(this.upperBandIndicator, this.lowerBandIndicator);
	}

	public GChannelUpperBandIndicator upper() {
		return upperBandIndicator;
	}

	public GChannelLowerBandIndicator lower() {
		return lowerBandIndicator;
	}

	public GChannelAverageIndicator average() {
		return averageIndicator;
	}
}
