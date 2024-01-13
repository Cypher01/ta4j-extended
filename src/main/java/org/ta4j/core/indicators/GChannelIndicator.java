package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * G-Channel Trend Detection indicator by jaggedsoft.
 * <a href="https://www.tradingview.com/script/smADlDdP-G-Channel-Trend-Detection/">TradingView</a>
 */
public class GChannelIndicator extends AbstractIndicator<Num> {
	private final GChannelUpperBandIndicator upperBandIndicator;
	private final GChannelLowerBandIndicator lowerBandIndicator;
	private final Num two;

	public GChannelIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator.getBarSeries());
		this.upperBandIndicator = new GChannelUpperBandIndicator(indicator, barCount);
		this.lowerBandIndicator = new GChannelLowerBandIndicator(indicator, barCount);
		this.upperBandIndicator.setLowerBandIndicator(lowerBandIndicator);
		this.lowerBandIndicator.setUpperBandIndicator(upperBandIndicator);
		this.two = numOf(2);
	}

	public boolean bullish(int index) {
		int barSinceLastUpperBreakout = 0;
		int barSinceLastLowerBreakout = 0;

		for (int i = index; i > 0; i--) {
			if (upperBandIndicator.breakout(i)) {
				barSinceLastUpperBreakout = index - i;
				break;
			}
		}

		for (int i = index; i > 0; i--) {
			if (lowerBandIndicator.breakout(i)) {
				barSinceLastLowerBreakout = index - i;
				break;
			}
		}

		return barSinceLastUpperBreakout <= barSinceLastLowerBreakout;
	}

	@Override
	public Num getValue(int index) {
		return upperBandIndicator.getValue(index).plus(lowerBandIndicator.getValue(index)).dividedBy(two);
	}

	private static class GChannelUpperBandIndicator extends CachedIndicator<Num> {
		private final Indicator<Num> indicator;
		private final Num barCount;
		private GChannelLowerBandIndicator lowerBandIndicator;

		public GChannelUpperBandIndicator(Indicator<Num> indicator, int barCount) {
			super(indicator);
			this.indicator = indicator;
			this.barCount = numOf(barCount);
		}

		public void setLowerBandIndicator(final GChannelLowerBandIndicator lowerBandIndicator) {
			this.lowerBandIndicator = lowerBandIndicator;
		}

		public boolean breakout(int index) {
			if (index == 0) {
				return false;
			}

			Num prevClosePrice = getBarSeries().getBar(index - 1).getClosePrice();
			Num closePrice = getBarSeries().getBar(index).getClosePrice();
			Num prevValue = getValue(index - 1);
			Num value = getValue(index);

			return prevValue.isLessThan(prevClosePrice) && value.isGreaterThan(closePrice);
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return numOf(0);
			}

			Num prevValueUpper = getValue(index - 1);
			Num prevValueLower = lowerBandIndicator.getValue(index - 1);

			return indicator.getValue(index).max(prevValueUpper).minus(prevValueUpper.minus(prevValueLower).dividedBy(barCount));
		}
	}

	private static class GChannelLowerBandIndicator extends CachedIndicator<Num> {
		private final Indicator<Num> indicator;
		private final Num barCount;
		private GChannelUpperBandIndicator upperBandIndicator;

		public GChannelLowerBandIndicator(Indicator<Num> indicator, int barCount) {
			super(indicator);
			this.barCount = numOf(barCount);
			this.indicator = indicator;
		}

		public void setUpperBandIndicator(final GChannelUpperBandIndicator upperBandIndicator) {
			this.upperBandIndicator = upperBandIndicator;
		}

		public boolean breakout(int index) {
			if (index == 0) {
				return false;
			}

			Num prevClosePrice = getBarSeries().getBar(index - 1).getClosePrice();
			Num closePrice = getBarSeries().getBar(index).getClosePrice();
			Num prevValue = getValue(index - 1);
			Num value = getValue(index);

			return prevValue.isGreaterThan(prevClosePrice) && value.isLessThan(closePrice);
		}

		@Override
		protected Num calculate(int index) {
			if (index == 0) {
				return numOf(0);
			}

			Num prevValueLower = getValue(index - 1);
			Num prevValueUpper = upperBandIndicator.getValue(index - 1);

			return indicator.getValue(index).min(prevValueLower).plus(prevValueUpper.minus(prevValueLower).dividedBy(barCount));
		}
	}
}
