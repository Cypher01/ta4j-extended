package org.ta4j.core.indicators;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

/**
 * Bar Height Percentage Indicator.
 * Get the difference in percentage between open and close or high and low
 * in relation to a given indicator, by default the close price.
 */
public class BarHeightPercentageIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> indicator;
	private final BarHeightType type;

	public BarHeightPercentageIndicator(BarSeries series, BarHeightType type) {
		this(series, new ClosePriceIndicator(series), type);
	}

	public BarHeightPercentageIndicator(BarSeries series, Indicator<Num> indicator, BarHeightType type) {
		super(series);

		this.indicator = indicator;
		this.type = type;
	}

	@Override
	public Num getValue(int index) {
		Bar bar = getBarSeries().getBar(index);
		Num range;

		switch (type) {
			case openClose:
				range = bar.getOpenPrice().minus(bar.getClosePrice()).abs();
				break;
			case highLow:
				range = bar.getHighPrice().minus(bar.getLowPrice());
				break;
			default:
				throw new IllegalArgumentException("Type " + type + " not supported");
		}

		if (range.isZero()) {
			return getBarSeries().numFactory().zero();
		} else {
			return range.dividedBy(indicator.getValue(index)).multipliedBy(getBarSeries().numFactory().hundred());
		}
	}

	@Override
	public int getUnstableBars() {
		return indicator.getUnstableBars();
	}

	public enum BarHeightType {
		openClose,
		highLow
	}
}
