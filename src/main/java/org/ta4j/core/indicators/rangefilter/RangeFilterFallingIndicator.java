package org.ta4j.core.indicators.rangefilter;

import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Range Filter indicator by guikroth.
 * <a href="https://www.tradingview.com/script/J8GzFGfD-Range-Filter-Buy-and-Sell-5min-guikroth-version/">TradingView</a>
 */
public class RangeFilterFallingIndicator extends CachedIndicator<Num> {
	private final RangeFilterIndicator rangeFilterIndicator;

	public RangeFilterFallingIndicator(RangeFilterIndicator rangeFilterIndicator) {
		super(rangeFilterIndicator);

		this.rangeFilterIndicator = rangeFilterIndicator;
	}

	@Override
	protected Num calculate(int index) {
		Num prevValue = numOf(0);

		if (index > 0) {
			prevValue = getValue(index - 1);
		}

		Num rangeFilterValue = rangeFilterIndicator.getValue(index);
		Num rangeFilterPrevValue = rangeFilterIndicator.getValue(index - 1);

		if (rangeFilterValue.isLessThan(rangeFilterPrevValue)) {
			return prevValue.plus(numOf(1));
		} else if (rangeFilterValue.isGreaterThan(rangeFilterPrevValue)) {
			return numOf(0);
		} else {
			return prevValue;
		}
	}
}
