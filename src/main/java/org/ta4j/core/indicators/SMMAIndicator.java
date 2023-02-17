package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * Smoothed moving average (SMMA) indicator, based on TradingView built-in indicator.
 */
public class SMMAIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final int barCount;

	public SMMAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);
		this.indicator = indicator;
		this.barCount = barCount;
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return indicator.getValue(0);
		}

		return getValue(index - 1).multipliedBy(numOf(barCount - 1)).plus(indicator.getValue(index)).dividedBy(numOf(barCount));
	}
}
