package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * Variable Index Dynamic Average (VIDYA) indicator by everget.
 * <a href="https://www.tradingview.com/script/hdrf0fXV-Variable-Index-Dynamic-Average-VIDYA/">TradingView</a>
 */
public class VIDYAIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final Indicator<Num> cmoIndicator;
	private final Num alpha;

	public VIDYAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);

		this.indicator = indicator;
		this.cmoIndicator = new CMOIndicator(indicator, barCount);
		this.alpha = numOf(2d / (barCount + 1d));
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return numOf(0);
		}

		Num cmoAbsNormValue = cmoIndicator.getValue(index).abs().dividedBy(numOf(100));
		Num prevValue = getValue(index - 1);

		return indicator.getValue(index).multipliedBy(alpha).multipliedBy(cmoAbsNormValue).plus(prevValue.multipliedBy(numOf(1).minus(alpha.multipliedBy(cmoAbsNormValue))));
	}
}
