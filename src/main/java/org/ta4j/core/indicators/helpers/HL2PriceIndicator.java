package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * HL2 (high/low average) price indicator.
 */
public class HL2PriceIndicator extends CachedIndicator<Num> {
	public HL2PriceIndicator(BarSeries series) {
		super(series);
	}

	@Override
	protected Num calculate(int index) {
		Bar bar = getBarSeries().getBar(index);

		Num highPrice = bar.getHighPrice();
		Num lowPrice = bar.getLowPrice();

		return highPrice.plus(lowPrice).dividedBy(numOf(2));
	}
}
