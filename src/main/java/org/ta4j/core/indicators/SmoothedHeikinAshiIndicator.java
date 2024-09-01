package org.ta4j.core.indicators;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Smoothed Heikin Ashi indicator by jackvmk.
 * <a href="https://de.tradingview.com/script/ROokknI2-Smoothed-Heiken-Ashi-Candles-v1/">TradingView</a>
 */
public class SmoothedHeikinAshiIndicator extends AbstractIndicator<Bar> {
	private final SmoothedBarIndicator smoothedHeikinAshiIndicator;
	private final int barCount;

	public SmoothedHeikinAshiIndicator(BarSeries series, int barCount) {
		super(series);

		this.smoothedHeikinAshiIndicator = new SmoothedBarIndicator(new HeikinAshiIndicator(new SmoothedBarIndicator(series, barCount)), barCount);
		this.barCount = barCount;
	}

	public BarSeries getHeikinAshiBarSeries() {
		return getHeikinAshiBarSeries(getBarSeries().getName() + "_SmoothedHeikinAshi");
	}

	public BarSeries getHeikinAshiBarSeries(String name) {
		List<Bar> bars = new ArrayList<>();

		for (int i = getBarSeries().getBeginIndex(); i <= getBarSeries().getEndIndex(); i++) {
			bars.add(getValue(i));
		}

		return new BaseBarSeries(name, bars);
	}

	@Override
	public Bar getValue(int index) {
		return smoothedHeikinAshiIndicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
