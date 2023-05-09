package org.ta4j.core.indicators.rangefilter;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Range Filter indicator by guikroth.
 * <a href="https://www.tradingview.com/script/J8GzFGfD-Range-Filter-Buy-and-Sell-5min-guikroth-version/">TradingView</a>
 */
public class SmoothRangeIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> smoothRange;

	public SmoothRangeIndicator(BarSeries series, int barCount, double multiplier) {
		this(new ClosePriceIndicator(series), barCount, multiplier);
	}

	public SmoothRangeIndicator(Indicator<Num> indicator, int barCount, double multiplier) {
		super(indicator.getBarSeries());

		PreviousValueIndicator prev = new PreviousValueIndicator(indicator);
		CombineIndicator diff = CombineIndicator.minus(indicator, prev);
		TransformIndicator abs = TransformIndicator.abs(diff);
		EMAIndicator avrng = new EMAIndicator(abs, barCount);
		EMAIndicator smoothrng = new EMAIndicator(avrng, barCount * 2 - 1);
		this.smoothRange = TransformIndicator.multiply(smoothrng, multiplier);
	}

	@Override
	public Num getValue(int index) {
		return smoothRange.getValue(index);
	}
}
