package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.ATRIndicatorPlus.Input;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Supertrend indicator, based on PineScript v5 Reference Manual.
 * <a href="https://www.tradingview.com/pine-script-reference/v5/#fun_ta{dot}supertrend">TradingView</a>
 */
public class SupertrendIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> closePriceIndicator;
	private final Indicator<Num> upperAtrBandIndicator;
	private final Indicator<Num> lowerAtrBandIndicator;

	public SupertrendIndicator(BarSeries series, int barCount, double factor) {
		this(series, barCount, factor, Input.MMA);
	}

	public SupertrendIndicator(Indicator<Num> indicator, int barCount, double factor) {
		this(indicator, barCount, factor, Input.MMA);
	}

	public SupertrendIndicator(BarSeries series, int barCount, double factor, Input atrInput) {
		this(new MedianPriceIndicator(series), barCount, factor, atrInput);
	}

	public SupertrendIndicator(Indicator<Num> indicator, int barCount, double factor, Input atrInput) {
		super(indicator);

		if (indicator instanceof ClosePriceIndicator) {
			this.closePriceIndicator = indicator;
		} else {
			this.closePriceIndicator = new ClosePriceIndicator(indicator.getBarSeries());
		}

		TransformIndicator atrIndicatorMultiplied = TransformIndicator.multiply(new ATRIndicatorPlus(indicator.getBarSeries(), barCount, atrInput), factor);
		this.upperAtrBandIndicator = CombineIndicator.plus(indicator, atrIndicatorMultiplied);
		this.lowerAtrBandIndicator = CombineIndicator.minus(indicator, atrIndicatorMultiplied);
	}

	@Override
	protected Num calculate(int index) {
		if (index == 0) {
			// Assume the bar series starts in an uptrend
			// The value is anyway not valid before the first trend reversal
			return lowerAtrBandIndicator.getValue(index);
		}

		Num prevValue = getValue(index - 1);
		Num prevClosePrice = closePriceIndicator.getValue(index - 1);
		Num closePrice = closePriceIndicator.getValue(index);
		Num upperAtrBandValue = upperAtrBandIndicator.getValue(index);
		Num lowerAtrBandValue = lowerAtrBandIndicator.getValue(index);

		if (prevValue.isLessThan(prevClosePrice)) { // green, band below price
			Num value = prevValue.max(lowerAtrBandValue);
			return value.isLessThan(closePrice) ? value : upperAtrBandValue;
		} else if (prevValue.isGreaterThan(prevClosePrice)) { // red, band above price
			Num value = prevValue.min(upperAtrBandValue);
			return value.isGreaterThan(closePrice) ? value : lowerAtrBandValue;
		} else {
			throw new IllegalArgumentException("Previous value equals previous close price, this shouldn't happen");
		}
	}
}
