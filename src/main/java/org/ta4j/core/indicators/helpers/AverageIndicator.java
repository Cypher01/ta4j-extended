package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import java.util.Arrays;
import java.util.List;

/**
 * Indicator to calculate the average of a list of other indicators.
 */
public class AverageIndicator extends CachedIndicator<Num> {
	private final List<Indicator<Num>> indicators;

	public AverageIndicator(Indicator<Num>... indicators) {
		super(indicators[0]);

		this.indicators = Arrays.asList(indicators);
	}

	@Override
	protected Num calculate(int index) {
		Num value = numOf(0);

		for (Indicator<Num> indicator : indicators) {
			value = value.plus(indicator.getValue(index));
		}

		return value.dividedBy(numOf(indicators.size()));
	}
}
