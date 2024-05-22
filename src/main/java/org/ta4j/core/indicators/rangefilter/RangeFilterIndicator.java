package org.ta4j.core.indicators.rangefilter;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

/**
 * Range Filter indicator by guikroth.
 * <a href="https://www.tradingview.com/script/J8GzFGfD-Range-Filter-Buy-and-Sell-5min-guikroth-version/">TradingView</a>
 */
public class RangeFilterIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final SmoothRangeIndicator smoothRangeIndicator;

	public RangeFilterIndicator(BarSeries series, int barCount, double multiplier) {
		this(new ClosePriceIndicator(series), barCount, multiplier);
	}

	public RangeFilterIndicator(Indicator<Num> indicator, int barCount, double multiplier) {
		this(indicator, new SmoothRangeIndicator(indicator, barCount, multiplier));
	}

	public RangeFilterIndicator(Indicator<Num> indicator, SmoothRangeIndicator smoothRangeIndicator) {
		super(indicator);

		this.indicator = indicator;
		this.smoothRangeIndicator = smoothRangeIndicator;
	}

	@Override
	protected Num calculate(int index) {
		Num prevValue = zero();

		if (index > 0) {
			prevValue = getValue(index - 1);
		}

		Num x = indicator.getValue(index);
		Num r = smoothRangeIndicator.getValue(index);
		Num rngfilt;

		if (x.isGreaterThan(prevValue)) {
			if (x.minus(r).isLessThan(prevValue)) {
				rngfilt = prevValue;
			} else {
				rngfilt = x.minus(r);
			}
		} else {
			if (x.plus(r).isGreaterThan(prevValue)) {
				rngfilt = prevValue;
			} else {
				rngfilt = x.plus(r);
			}
		}

		return rngfilt;
	}

	@Override
	public int getUnstableBars() {
		return smoothRangeIndicator.getUnstableBars();
	}
}
