package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.num.Num;

/**
 * Distance from indicator (close - indicator) / indicator.
 * This is based on DistanceFromMAIndicator, but with simplified calculation and without the limitation to moving averages only.
 */
public class DistanceFromIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> distance;

	public DistanceFromIndicator(BarSeries series, Indicator<Num> indicator) {
		this(new ClosePriceIndicator(series), indicator);
	}

	public DistanceFromIndicator(ClosePriceIndicator closePriceIndicator, Indicator<Num> indicator) {
		super(closePriceIndicator.getBarSeries());

		this.distance = CombineIndicator.divide(CombineIndicator.minus(closePriceIndicator, indicator), indicator);
	}

	@Override
	public Num getValue(int index) {
		return distance.getValue(index);
	}
}
