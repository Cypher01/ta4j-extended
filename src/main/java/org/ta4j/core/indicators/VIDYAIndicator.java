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
	private final int barCount;
	private final Num alpha;

	public VIDYAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);

		this.indicator = indicator;
		this.cmoIndicator = new CMOIndicator(indicator, barCount);
		this.barCount = barCount;
		this.alpha = getBarSeries().numFactory().numOf(2d / (barCount + 1d));
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return getBarSeries().numFactory().zero();
		}

		Num cmoAbsNormValue = cmoIndicator.getValue(index).abs().dividedBy(getBarSeries().numFactory().hundred());
		Num prevValue = getValue(index - 1);

		return indicator.getValue(index).multipliedBy(alpha).multipliedBy(cmoAbsNormValue).plus(prevValue.multipliedBy(getBarSeries().numFactory().one().minus(alpha.multipliedBy(cmoAbsNormValue))));
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
