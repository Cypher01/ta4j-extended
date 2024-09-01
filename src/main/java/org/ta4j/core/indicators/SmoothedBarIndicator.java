package org.ta4j.core.indicators;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.BarValueIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

/**
 * Smoothed bar indicator.
 */
public class SmoothedBarIndicator extends CachedIndicator<Bar> {
	private final Indicator<Num> smoothedOpen;
	private final Indicator<Num> smoothedHigh;
	private final Indicator<Num> smoothedLow;
	private final Indicator<Num> smoothedClose;
	private final int barCount;

	public SmoothedBarIndicator(BarSeries series, int barCount) {
		this(new OpenPriceIndicator(series), new HighPriceIndicator(series), new LowPriceIndicator(series), new ClosePriceIndicator(series), barCount);
	}

	public SmoothedBarIndicator(Indicator<Bar> indicator, int barCount) {
		this(BarValueIndicator.openPrice(indicator), BarValueIndicator.highPrice(indicator), BarValueIndicator.lowPrice(indicator), BarValueIndicator.closePrice(indicator), barCount);
	}

	public SmoothedBarIndicator(Indicator<Num> openPriceIndicator, Indicator<Num> highPriceIndicator, Indicator<Num> lowPriceIndicator, Indicator<Num> closePriceIndicator, int barCount) {
		super(openPriceIndicator);

		this.smoothedOpen = new EMAIndicator(openPriceIndicator, barCount);
		this.smoothedHigh = new EMAIndicator(highPriceIndicator, barCount);
		this.smoothedLow = new EMAIndicator(lowPriceIndicator, barCount);
		this.smoothedClose = new EMAIndicator(closePriceIndicator, barCount);
		this.barCount = barCount;
	}

	public BarSeries getSmoothedBarSeries() {
		return getSmoothedBarSeries(getBarSeries().getName() + "_Smoothed");
	}

	public BarSeries getSmoothedBarSeries(String name) {
		List<Bar> bars = new ArrayList<>();

		for (int i = getBarSeries().getBeginIndex(); i <= getBarSeries().getEndIndex(); i++) {
			bars.add(getValue(i));
		}

		return new BaseBarSeries(name, bars);
	}

	@Override
	protected Bar calculate(int index) {
		Bar bar = getBarSeries().getBar(index);
		return new BaseBar(bar.getTimePeriod(), bar.getEndTime(), smoothedOpen.getValue(index), smoothedHigh.getValue(index), smoothedLow.getValue(index), smoothedClose.getValue(index), bar.getVolume(), bar.getAmount());
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
