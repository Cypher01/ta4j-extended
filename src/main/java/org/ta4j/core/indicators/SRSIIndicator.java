package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Apirine Slow RSI (SRSI) indicator by LazyBear.
 * <a href="https://www.tradingview.com/script/3VDdQZei-Apirine-Slow-RSI-LazyBear/">TradingView</a>
 */
public class SRSIIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> r4;
	private final Indicator<Num> r5;

	public SRSIIndicator(Indicator<Num> indicator, int barCount, int smoothing) {
		super(indicator);

		Indicator<Num> r1 = new EMAIndicator(indicator, barCount);
		Indicator<Num> r2 = TransformIndicator.max(CombineIndicator.minus(indicator, r1), 0);
		Indicator<Num> r3 = TransformIndicator.max(CombineIndicator.minus(r1, indicator), 0);
		r4 = new SMMAIndicator(r2, smoothing);
		r5 = new SMMAIndicator(r3, smoothing);
	}

	@Override
	protected Num calculate(int index) {
		Num hundred = numOf(100);

		if (r5.getValue(index).isEqual(numOf(0))) {
			return hundred;
		}

		return hundred.minus(hundred.dividedBy(numOf(1).plus(r4.getValue(index).dividedBy(r5.getValue(index)))));
	}
}
