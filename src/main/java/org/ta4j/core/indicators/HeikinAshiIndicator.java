package org.ta4j.core.indicators;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

/**
 * Heikin Ashi indicator.
 */
public class HeikinAshiIndicator extends RecursiveCachedIndicator<Bar> {
	public HeikinAshiIndicator(BarSeries series) {
		super(series);
	}

	public BarSeries getHeikinAshiBarSeries() {
		return getHeikinAshiBarSeries(getBarSeries().getName() + "_HeikinAshi");
	}

	public BarSeries getHeikinAshiBarSeries(String name) {
		List<Bar> bars = new ArrayList<>();

		for (int i = getBarSeries().getBeginIndex(); i <= getBarSeries().getEndIndex(); i++) {
			bars.add(getValue(i));
		}

		return new BaseBarSeries(name, bars);
	}

	@Override
	protected Bar calculate(int index) {
		if (index == 0) {
			return getBarSeries().getBar(index);
		}

		Bar prevHaBar = getValue(index - 1);
		Bar bar = getBarSeries().getBar(index);

		Num openPrice = bar.getOpenPrice();
		Num highPrice = bar.getHighPrice();
		Num lowPrice = bar.getLowPrice();
		Num closePrice = bar.getClosePrice();

		Num haOpenPrice = prevHaBar.getOpenPrice().plus(prevHaBar.getClosePrice()).dividedBy(numOf(2));
		Num haClosePrice = openPrice.plus(highPrice).plus(lowPrice).plus(closePrice).dividedBy(numOf(4));
		Num haHighPrice = highPrice.max(haClosePrice).max(haOpenPrice);
		Num haLowPrice = lowPrice.min(haClosePrice).min(haOpenPrice);

		return new BaseBar(bar.getTimePeriod(), bar.getEndTime(),
				haOpenPrice, haHighPrice, haLowPrice, haClosePrice,
				bar.getVolume(), bar.getAmount(), bar.getTrades());
	}
}
