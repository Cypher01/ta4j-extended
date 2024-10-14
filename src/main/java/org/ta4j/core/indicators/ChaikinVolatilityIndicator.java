package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.Num;

/**
 * Chaikin volatility indicator by HPotter.
 * <a href="https://www.tradingview.com/script/QAjxTT0J-Chaikin-Volatility/">TradingView</a>
 */
public class ChaikinVolatilityIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> chaikinVolatilityIndicator;
	private final int barCount;

	public ChaikinVolatilityIndicator(BarSeries series, int barCount, int rocBarCount) {
		super(series);

		CombineIndicator priceRange = CombineIndicator.minus(new HighPriceIndicator(series), new LowPriceIndicator(series));
		EMAIndicator priceRangeEma = new EMAIndicator(priceRange, barCount);
		this.chaikinVolatilityIndicator = new ROCIndicator(priceRangeEma, rocBarCount);
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		return chaikinVolatilityIndicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
