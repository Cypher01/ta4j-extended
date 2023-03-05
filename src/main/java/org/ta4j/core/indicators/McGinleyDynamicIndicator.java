package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * McGinley dynamic indicator, based on TradingView built-in indicator.
 */
public class McGinleyDynamicIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final Indicator<Num> emaIndicator;
	private final int barCount;
	private final Num barCountNum;
	private final Num four;

	public McGinleyDynamicIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);

		this.indicator = indicator;
		this.emaIndicator = new EMAIndicator(indicator, barCount);
		this.barCount = barCount;
		this.barCountNum = numOf(barCount);
		this.four = numOf(4);
	}

	@Override
	protected Num calculate(int index) {
		if (index < barCount) {
			return emaIndicator.getValue(index);
		}

		Num source = indicator.getValue(index);
		Num prevValue = getValue(index - 1);

		return prevValue.plus(source.minus(prevValue).dividedBy(barCountNum.multipliedBy(source.dividedBy(prevValue).pow(four))));
	}
}
