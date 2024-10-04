package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * HLCC4 (high/low/close/close average) price indicator.
 */
public class HLCC4PriceIndicator extends CachedIndicator<Num> {
	public HLCC4PriceIndicator(BarSeries series) {
		super(series);
	}

	@Override
	protected Num calculate(int index) {
		Bar bar = getBarSeries().getBar(index);

		Num highPrice = bar.getHighPrice();
		Num lowPrice = bar.getLowPrice();
		Num closePrice = bar.getClosePrice();

		return highPrice.plus(lowPrice).plus(closePrice).plus(closePrice).dividedBy(getBarSeries().numFactory().numOf(4));
	}

	@Override
	public int getUnstableBars() {
		return 0;
	}
}
