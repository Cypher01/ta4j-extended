package org.ta4j.core.indicators.rangefilter;

import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Range Filter indicator by guikroth.
 * <a href="https://www.tradingview.com/script/J8GzFGfD-Range-Filter-Buy-and-Sell-5min-guikroth-version/">TradingView</a>
 */
public class RangeFilterRisingIndicator extends CachedIndicator<Num> {
	private final RangeFilterIndicator rangeFilterIndicator;

	public RangeFilterRisingIndicator(RangeFilterIndicator rangeFilterIndicator) {
		super(rangeFilterIndicator);

		this.rangeFilterIndicator = rangeFilterIndicator;
	}

	@Override
	protected Num calculate(int index) {
		Num prevValue = zero();

		if (index > 0) {
			prevValue = getValue(index - 1);
		}

		Num rangeFilterValue = rangeFilterIndicator.getValue(index);
		Num rangeFilterPrevValue = rangeFilterIndicator.getValue(index - 1);

		if (rangeFilterValue.isGreaterThan(rangeFilterPrevValue)) {
			return prevValue.plus(one());
		} else if (rangeFilterValue.isLessThan(rangeFilterPrevValue)) {
			return zero();
		} else {
			return prevValue;
		}
	}

	@Override
	public int getUnstableBars() {
		return rangeFilterIndicator.getUnstableBars();
	}
}
