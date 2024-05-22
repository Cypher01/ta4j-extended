package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Indicator to replace a given value in another indicator. All other values are unchanged.
 */
public class SubstituteIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final Num value;
	private final Num substitute;

	public SubstituteIndicator(Indicator<Num> indicator, Number value, Number substitute) {
		this(indicator, indicator.numOf(value), indicator.numOf(substitute));
	}

	public SubstituteIndicator(Indicator<Num> indicator, Num value, Num substitute) {
		super(indicator);
		this.indicator = indicator;
		this.value = value;
		this.substitute = substitute;
	}

	@Override
	protected Num calculate(int index) {
		Num value = indicator.getValue(index);

		if (value.equals(this.value)) {
			return substitute;
		} else {
			return value;
		}
	}

	@Override
	public int getUnstableBars() {
		return indicator.getUnstableBars();
	}
}
