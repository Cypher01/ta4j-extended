package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Indicator to add up the last n values of another indicator.
 */
public class SumValuesIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final int barCount;

	public SumValuesIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);

		this.indicator = indicator;
		this.barCount = barCount;
	}

	@Override
	protected Num calculate(int index) {
		Num sum = zero();

		for (int i = index; i > index - barCount; i--) {
			sum = sum.plus(indicator.getValue(i));
		}

		return sum;
	}

	@Override
	public int getUnstableBars() {
		return indicator.getUnstableBars();
	}
}
