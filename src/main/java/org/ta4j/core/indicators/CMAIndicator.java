package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.VarianceIndicator;
import org.ta4j.core.num.Num;

/**
 * Corrected Moving Average indicator by everget.
 * <a href="https://www.tradingview.com/script/0nfmHG29-Corrected-Moving-Average/">TradingView</a>
 */
public class CMAIndicator extends CachedIndicator<Num> {
	private final Num tolerance;
	private final Indicator<Num> indicator;
	private final SMAIndicator sma;
	private final VarianceIndicator varianceIndicator;

	public CMAIndicator(BarSeries series, int barCount) {
		this(new ClosePriceIndicator(series), barCount);
	}

	public CMAIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator.getBarSeries());

		this.tolerance = numOf(10).pow(-5);
		this.indicator = indicator;
		this.sma = new SMAIndicator(indicator, barCount);
		this.varianceIndicator = new VarianceIndicator(indicator, barCount);
	}

	@Override
	protected Num calculate(int index) {
		Num prevValueOrSma;
		Num prevValueOrSrc;

		if (index == 0) {
			prevValueOrSma = sma.getValue(index);
			prevValueOrSrc = indicator.getValue(index);
		} else {
			prevValueOrSma = getValue(index - 1);
			prevValueOrSrc = getValue(index - 1);
		}

		Num v1 = varianceIndicator.getValue(index);
		Num v2 = prevValueOrSma.minus(sma.getValue(index)).pow(2);
		Num v3 = v1.isEqual(numOf(0)) || v2.isEqual(numOf(0)) ? numOf(1) : v2.dividedBy(v1.plus(v2));

		Num err = numOf(1);
		Num kPrev = numOf(1);
		Num k = numOf(1);

		for (int i = 0; i < 5000; i++) {
			if (err.isGreaterThan(tolerance)) {
				k = v3.multipliedBy(kPrev).multipliedBy(numOf(2).minus(kPrev));
				err = kPrev.minus(k);
				kPrev = k;
			}
		}

		return prevValueOrSrc.plus(k.multipliedBy(sma.getValue(index).minus(prevValueOrSrc)));
	}
}
