package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Bar;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.num.Num;

import java.util.function.Function;

/**
 * Indicator to get any part value series of type Num of an indicator of type Bar, like open/high/low/close/volume.
 * This is a combined version of OpenPriceIndicator/HighPriceIndicator/LowPriceIndicator/ClosePriceIndicator/VolumeIndicator,
 * but for indicator of type Bar instead of BarSeries.
 */
public class BarValueIndicator extends AbstractIndicator<Num> {
	private final Indicator<Bar> indicator;
	private final Function<Bar, Num> priceFunction;

	public static BarValueIndicator openPrice(Indicator<Bar> indicator) {
		return new BarValueIndicator(indicator, Bar::getOpenPrice);
	}

	public static BarValueIndicator highPrice(Indicator<Bar> indicator) {
		return new BarValueIndicator(indicator, Bar::getHighPrice);
	}

	public static BarValueIndicator lowPrice(Indicator<Bar> indicator) {
		return new BarValueIndicator(indicator, Bar::getLowPrice);
	}

	public static BarValueIndicator closePrice(Indicator<Bar> indicator) {
		return new BarValueIndicator(indicator, Bar::getClosePrice);
	}

	public static BarValueIndicator volume(Indicator<Bar> indicator) {
		return new BarValueIndicator(indicator, Bar::getVolume);
	}

	public BarValueIndicator(Indicator<Bar> indicator, Function<Bar, Num> priceFunction) {
		super(indicator.getBarSeries());
		this.indicator = indicator;
		this.priceFunction = priceFunction;
	}

	@Override
	public Num getValue(int index) {
		final Bar bar = indicator.getValue(index);
		return priceFunction.apply(bar);
	}
}
