package org.ta4j.core.indicators.volume;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

/**
 * Time Segmented Volume (TSV) indicator by vitelot.
 * <a href="https://www.tradingview.com/script/6GR4ht9X-Time-Segmented-Volume/">TradingView</a>
 */
public class TSVIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final int barCount;

	public TSVIndicator(BarSeries series) {
		this(series, 13);
	}

	public TSVIndicator(BarSeries series, int barCount) {
		super(series);

		this.indicator = new ClosePriceIndicator(series);
		this.barCount = barCount;
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return numOf(0);
		}

		Num value = numOf(0);
		int loopLength = index - Math.min(index, barCount);

		for (int i = index; i > loopLength; i--) {
			Num closePrice = indicator.getValue(i);
			Num prevClosePrice = indicator.getValue(i - 1);
			Num volume = getBarSeries().getBar(i).getVolume();
			value = value.plus(volume.multipliedBy(closePrice.minus(prevClosePrice)));
		}

		return value;
	}
}
