package org.ta4j.core.indicators.wae;

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
 * Waddah Attar Explosion Trend indicator by ShayanKM.
 * <a href="https://www.tradingview.com/script/d9IjcYyS-Waddah-Attar-Explosion-V2-SHK/">TradingView</a>
 */
public class WAETrendIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> trendIndicator;
	private final int unstableBars;

	public WAETrendIndicator(BarSeries series) {
		this(series, 150, 20, 40);
	}

	public WAETrendIndicator(BarSeries series, int sensitivity, int fastLength, int slowLength) {
		this(new ClosePriceIndicator(series), sensitivity, fastLength, slowLength);
	}

	public WAETrendIndicator(Indicator<Num> indicator) {
		this(indicator, 150, 20, 40);
	}

	public WAETrendIndicator(Indicator<Num> indicator, int sensitivity, int fastLength, int slowLength) {
		super(indicator.getBarSeries());

		EMAIndicator fastMA = new EMAIndicator(indicator, fastLength);
		EMAIndicator slowMA = new EMAIndicator(indicator, slowLength);
		CombineIndicator macd = CombineIndicator.minus(fastMA, slowMA);
		PreviousValueIndicator macdPrev = new PreviousValueIndicator(macd);
		this.trendIndicator = TransformIndicator.multiply(CombineIndicator.minus(macd, macdPrev), sensitivity);
		this.unstableBars = Math.max(fastLength, slowLength);
	}

	@Override
	public Num getValue(int index) {
		return trendIndicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return unstableBars;
	}
}
