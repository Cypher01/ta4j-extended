package org.ta4j.core.indicators.helpers;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.num.Num;

/**
 * Change indicator aka. momentum indicator.
 */
public class ChangeIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> indicator;
	private final int barCount;

	public ChangeIndicator(BarSeries series) {
		this(series, 1);
	}

	public ChangeIndicator(Indicator<Num> indicator) {
		this(indicator.getBarSeries(), 1);
	}

	public ChangeIndicator(BarSeries series, int barCount) {
		this(new ClosePriceIndicator(series), barCount);
	}

	public ChangeIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator.getBarSeries());

		this.indicator = indicator;
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		if (index < barCount) {
			return zero();
		}

		return indicator.getValue(index).minus(indicator.getValue(index - barCount));
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
