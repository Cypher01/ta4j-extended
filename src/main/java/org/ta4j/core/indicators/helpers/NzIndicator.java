package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * nz indicator, inspired by PineScript nz function.
 * <a href="https://www.tradingview.com/pine-script-reference/v5/#fun_nz">TradingView</a>
 */
public class NzIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final Indicator<Num> replacement;

	public NzIndicator(Indicator<Num> indicator) {
		this(indicator, indicator.zero());
	}

	public NzIndicator(Indicator<Num> indicator, Num replacement) {
		this(indicator, new ConstantIndicator<>(indicator.getBarSeries(), replacement));
	}

	public NzIndicator(Indicator<Num> indicator, Indicator<Num> replacement) {
		super(indicator);

		this.indicator = indicator;
		this.replacement = replacement;
	}

	@Override
	protected Num calculate(int index) {
		Num value = indicator.getValue(index);
		return value.isNaN() ? replacement.getValue(index) : value;
	}

	@Override
	public int getUnstableBars() {
		return indicator.getUnstableBars();
	}
}
