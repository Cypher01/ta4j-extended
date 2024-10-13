package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.statistics.SimpleLinearRegressionIndicator;
import org.ta4j.core.num.Num;

/**
 * Zero Lag LSMA (ZLSMA) indicator by veryfid.
 * <a href="https://www.tradingview.com/script/3LGnSrQN-ZLSMA-Zero-Lag-LSMA/">TradingView</a>
 * The offset is not implemented, because the SimpleLinearRegressionIndicator of ta4j-core does not support it.
 * The default value on TradingView is anyway zero.
 */
public class ZLSMAIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> zlsmaIndicator;
	private final int barCount;

	public ZLSMAIndicator(BarSeries series, int barCount) {
		this(new ClosePriceIndicator(series), barCount);
	}

	public ZLSMAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator.getBarSeries());

		SimpleLinearRegressionIndicator lsma = new SimpleLinearRegressionIndicator(indicator, barCount);
		SimpleLinearRegressionIndicator lsma2 = new SimpleLinearRegressionIndicator(lsma, barCount);
		CombineIndicator eq = CombineIndicator.minus(lsma, lsma2);

		this.zlsmaIndicator = CombineIndicator.plus(lsma, eq);
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		return zlsmaIndicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
