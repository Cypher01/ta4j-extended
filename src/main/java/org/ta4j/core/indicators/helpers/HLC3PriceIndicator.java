package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * HLC3 (high/low/close average) price indicator.
 */
public class HLC3PriceIndicator extends CachedIndicator<Num> {
	public HLC3PriceIndicator(BarSeries series) {
		super(series);
	}

	@Override
	protected Num calculate(int index) {
		Bar bar = getBarSeries().getBar(index);

		Num highPrice = bar.getHighPrice();
		Num lowPrice = bar.getLowPrice();
		Num closePrice = bar.getClosePrice();

		return highPrice.plus(lowPrice).plus(closePrice).dividedBy(numOf(3));
	}
}
