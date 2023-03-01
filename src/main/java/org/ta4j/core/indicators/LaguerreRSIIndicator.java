package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * Laguerre RSI indicator by everget.
 * <a href="https://de.tradingview.com/script/w0hoT58L-Laguerre-RSI/">TradingView</a>
 */
public class LaguerreRSIIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> cuIndicator;
	private final Indicator<Num> cdIndicator;

	public LaguerreRSIIndicator(Indicator<Num> indicator, double alpha) {
		super(indicator);

		Indicator<Num> l0Indicator = new L0Indicator(indicator, alpha);
		Indicator<Num> l1Indicator = new L123Indicator(l0Indicator, alpha);
		Indicator<Num> l2Indicator = new L123Indicator(l1Indicator, alpha);
		Indicator<Num> l3Indicator = new L123Indicator(l2Indicator, alpha);
		this.cuIndicator = new CUIndicator(l0Indicator, l1Indicator, l2Indicator, l3Indicator);
		this.cdIndicator = new CDIndicator(l0Indicator, l1Indicator, l2Indicator, l3Indicator);
	}

	@Override
	protected Num calculate(int index) {
		Num cu = cuIndicator.getValue(index);
		Num cd = cdIndicator.getValue(index);

		return nz(cu.dividedBy(cu.plus(cd))); // division by zero returns NaN, which is resolved to zero by nz method
	}

	private Num nz(Num num) {
		return num.isNaN() ? numOf(0) : num;
	}

	private static class L0Indicator extends CachedIndicator<Num> {
		private final Indicator<Num> indicator;
		private final Num alpha;
		private final Num gamma;

		private L0Indicator(Indicator<Num> indicator, double alpha) {
			super(indicator);

			this.indicator = indicator;
			this.alpha = numOf(alpha);
			this.gamma = numOf(1 - alpha);
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return numOf(0);
			}

			return alpha.multipliedBy(indicator.getValue(index)).plus(gamma.multipliedBy(getValue(index - 1)));
		}
	}

	private static class L123Indicator extends CachedIndicator<Num> {
		private final Indicator<Num> indicator;
		private final Num gamma;
		private final Num negGamma;

		private L123Indicator(Indicator<Num> indicator, double alpha) {
			super(indicator);

			this.indicator = indicator;
			this.gamma = numOf(1 - alpha);
			this.negGamma = numOf((-1) * (1 - alpha));
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return numOf(0);
			}

			return negGamma.multipliedBy(indicator.getValue(index)).plus(indicator.getValue(index - 1)).plus(gamma.multipliedBy(getValue(index - 1)));
		}
	}

	private static class CUIndicator extends CachedIndicator<Num> {
		private final Indicator<Num> l0Indicator;
		private final Indicator<Num> l1Indicator;
		private final Indicator<Num> l2Indicator;
		private final Indicator<Num> l3Indicator;

		private CUIndicator(Indicator<Num> l0Indicator, Indicator<Num> l1Indicator, Indicator<Num> l2Indicator, Indicator<Num> l3Indicator) {
			super(l0Indicator);

			this.l0Indicator = l0Indicator;
			this.l1Indicator = l1Indicator;
			this.l2Indicator = l2Indicator;
			this.l3Indicator = l3Indicator;
		}

		@Override
		protected Num calculate(int index) {
			Num addend1 = l0Indicator.getValue(index).minus(l1Indicator.getValue(index)).max(numOf(0));
			Num addend2 = l1Indicator.getValue(index).minus(l2Indicator.getValue(index)).max(numOf(0));
			Num addend3 = l2Indicator.getValue(index).minus(l3Indicator.getValue(index)).max(numOf(0));

			return addend1.plus(addend2).plus(addend3);
		}
	}

	private static class CDIndicator extends CachedIndicator<Num> {
		private final Indicator<Num> l0Indicator;
		private final Indicator<Num> l1Indicator;
		private final Indicator<Num> l2Indicator;
		private final Indicator<Num> l3Indicator;

		private CDIndicator(Indicator<Num> l0Indicator, Indicator<Num> l1Indicator, Indicator<Num> l2Indicator, Indicator<Num> l3Indicator) {
			super(l0Indicator);

			this.l0Indicator = l0Indicator;
			this.l1Indicator = l1Indicator;
			this.l2Indicator = l2Indicator;
			this.l3Indicator = l3Indicator;
		}

		@Override
		protected Num calculate(int index) {
			Num addend1 = l1Indicator.getValue(index).minus(l0Indicator.getValue(index)).max(numOf(0));
			Num addend2 = l2Indicator.getValue(index).minus(l1Indicator.getValue(index)).max(numOf(0));
			Num addend3 = l3Indicator.getValue(index).minus(l2Indicator.getValue(index)).max(numOf(0));

			return addend1.plus(addend2).plus(addend3);
		}
	}
}
