package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.NzIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.num.Num;

/**
 * Jurik Moving Average (JMA) indicator by everget.
 * <a https://www.tradingview.com/script/nZuBWW9j-Jurik-Moving-Average/">TradingView</a>
 */
public class JMAIndicator extends CachedIndicator<Num> {
	private final int barCount;
	private final E2Indicator e2Indicator;

	public JMAIndicator(BarSeries series, int barCount, int phase, int power) {
		this(new ClosePriceIndicator(series), barCount, phase, power);
	}

	public JMAIndicator(Indicator<Num> indicator, int barCount, int phase, int power) {
		super(indicator.getBarSeries());

		this.barCount = barCount;
		double phaseRatio;

		if (phase < -100) {
			phaseRatio = 0.5;
		} else if (phase > 100) {
			phaseRatio = 2.5;
		} else {
			phaseRatio = phase / 100d + 1.5;
		}

		double beta = 0.45 * (barCount - 1) / (0.45 * (barCount - 1) + 2);
		double alpha = Math.pow(beta, power);

		E0Indicator e0Indicator = new E0Indicator(indicator, alpha);
		E1Indicator e1Indicator = new E1Indicator(indicator, e0Indicator, beta);
		this.e2Indicator = new E2Indicator(e0Indicator, e1Indicator, this, phaseRatio, alpha);
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			return e2Indicator.getValue(index);
		}

		return e2Indicator.getValue(index).plus(getValue(index - 1));
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}

	private static class E0Indicator extends CachedIndicator<Num> {
		private final Indicator<Num> indicator;
		private final double alpha;

		public E0Indicator(Indicator<Num> indicator, double alpha) {
			super(indicator);

			this.indicator = indicator;
			this.alpha = alpha;
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return indicator.getValue(index).multipliedBy(numOf(1d - alpha));
			}

			return indicator.getValue(index).multipliedBy(numOf(1d - alpha)).plus(numOf(alpha).multipliedBy(getValue(index - 1)));
		}

		@Override
		public int getUnstableBars() {
			return 0;
		}
	}

	private static class E1Indicator extends CachedIndicator<Num> {
		private final Indicator<Num> indicator;
		private final E0Indicator e0Indicator;
		private final double beta;

		public E1Indicator(Indicator<Num> indicator, E0Indicator e0Indicator, double beta) {
			super(indicator);

			this.indicator = indicator;
			this.e0Indicator = e0Indicator;
			this.beta = beta;
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return indicator.getValue(index).minus(e0Indicator.getValue(index)).multipliedBy(numOf(1d - beta));
			}

			return indicator.getValue(index).minus(e0Indicator.getValue(index)).multipliedBy(numOf(1d - beta)).plus(numOf(beta).multipliedBy(getValue(index - 1)));
		}

		@Override
		public int getUnstableBars() {
			return 0;
		}
	}

	private static class E2Indicator extends CachedIndicator<Num> {
		private final E0Indicator e0Indicator;
		private final E1Indicator e1Indicator;
		private final NzIndicator nzPrevJmaIndicator;
		private final double phaseRatio;
		private final double alpha;

		public E2Indicator(E0Indicator e0Indicator, E1Indicator e1Indicator, JMAIndicator jmaIndicator, double phaseRatio, double alpha) {
			super(e0Indicator.getBarSeries());

			this.e0Indicator = e0Indicator;
			this.e1Indicator = e1Indicator;
			this.nzPrevJmaIndicator = new NzIndicator(new PreviousValueIndicator(jmaIndicator));
			this.phaseRatio = phaseRatio;
			this.alpha = alpha;
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return e0Indicator.getValue(index).plus(e1Indicator.getValue(index).multipliedBy(numOf(phaseRatio))).minus(nzPrevJmaIndicator.getValue(index)).multipliedBy(numOf(1 - alpha).pow(2));
			}

			return e0Indicator.getValue(index).plus(e1Indicator.getValue(index).multipliedBy(numOf(phaseRatio))).minus(nzPrevJmaIndicator.getValue(index)).multipliedBy(numOf(1 - alpha).pow(2)).plus(numOf(alpha).pow(2).multipliedBy(getValue(index - 1)));
		}

		@Override
		public int getUnstableBars() {
			return 0;
		}
	}
}
