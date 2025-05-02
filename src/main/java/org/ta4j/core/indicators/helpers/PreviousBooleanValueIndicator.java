package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;

/**
 * Previous boolean value indicator.
 * This is based on {@link PreviousValueIndicator}, but of type Boolean instead of Num.
 */
public class PreviousBooleanValueIndicator extends CachedIndicator<Boolean> {
	private final int n;
	private final Indicator<Boolean> indicator;

	public PreviousBooleanValueIndicator(Indicator<Boolean> indicator) {
		this(indicator, 1);
	}

	public PreviousBooleanValueIndicator(Indicator<Boolean> indicator, int n) {
		super(indicator);

		if (n < 1) {
			throw new IllegalArgumentException("n must be positive number, but was: " + n);
		}

		this.n = n;
		this.indicator = indicator;
	}

	@Override
	protected Boolean calculate(int index) {
		int previousIndex = index - n;
		return previousIndex < 0 ? false : indicator.getValue(previousIndex);
	}

	@Override
	public int getUnstableBars() {
		return n;
	}

	@Override
	public String toString() {
		final String nInfo = n == 1 ? "" : "(" + n + ")";
		return getClass().getSimpleName() + nInfo + "[" + this.indicator + "]";
	}
}
