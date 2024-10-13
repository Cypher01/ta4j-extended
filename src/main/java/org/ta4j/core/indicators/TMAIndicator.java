package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * Triangular moving average (TMA) indicator by everget.
 * <a href="https://www.tradingview.com/script/Y3mprx4L-Triangular-Moving-Average/">TradingView</a>
 */
public class TMAIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> tmaIndicator;
	private final int barCount;

	public TMAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator.getBarSeries());
		this.tmaIndicator = new SMAIndicator(new SMAIndicator(indicator, (int) Math.ceil(barCount / 2d)), (int) Math.floor(barCount / 2d) + 1);
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		return tmaIndicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
