package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

import static org.ta4j.core.num.NaN.NaN;

/**
 * Indicator to trail another indicator up or down.
 * In case of up, if the indicator rises, the trailing indicator follows, if the indicator falls, the trailing indicator will stay the same.
 * In case of down, if the indicator falls, the trailing indicator follows, if the indicator rises, the trailing indicator will stay the same.
 */
public class TrailingIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> indicator;
	private final int startIndex;
	private final Direction direction;

	public static TrailingIndicator atr(BarSeries series, int barCount, int multiplier, int startIndex, Direction direction) {
		return atr(new ClosePriceIndicator(series), new ATRIndicatorPlus(series, barCount), multiplier, startIndex, direction);
	}

	public static TrailingIndicator atr(Indicator<Num> indicator, ATRIndicatorPlus atrIndicator, int multiplier, int startIndex, Direction direction) {
		switch (direction) {
			case UP:
				return trail(CombineIndicator.minus(indicator, TransformIndicator.multiply(atrIndicator, multiplier)), startIndex, direction);
			case DOWN:
				return trail(CombineIndicator.plus(indicator, TransformIndicator.multiply(atrIndicator, multiplier)), startIndex, direction);
			default:
				throw new IllegalArgumentException("Direction " + direction + " not supported");
		}
	}

	public static TrailingIndicator doubleEma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new DoubleEMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator ema(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new EMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator hma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new HMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator lwma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new LWMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator mcGinley(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new McGinleyDynamicIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator mma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new MMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator sma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new SMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator smma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new SMMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator t3ma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new T3MAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator tripleEma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new TripleEMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator vawma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new VAWMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator vidya(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new VIDYAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator vwma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new VWMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator wma(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new WMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator zlema(BarSeries series, int barCount, int startIndex, Direction direction) {
		return trail(new ZLEMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
	}

	public static TrailingIndicator trail(Indicator<Num> indicator, int startIndex, Direction direction) {
		return new TrailingIndicator(indicator, startIndex, direction);
	}

	public TrailingIndicator(Indicator<Num> indicator, int startIndex, Direction direction) {
		super(indicator);

		this.indicator = indicator;
		this.startIndex = Math.max(startIndex, indicator.getBarSeries().getBeginIndex());
		this.direction = direction;
	}

	@Override
	protected Num calculate(int index) {
		if (index < startIndex) {
			return NaN;
		} else if (index == startIndex) {
			return indicator.getValue(index);
		}

		switch (direction) {
			case UP:
				return getValue(index - 1).max(indicator.getValue(index));
			case DOWN:
				return getValue(index - 1).min(indicator.getValue(index));
			default:
				throw new IllegalArgumentException("Direction " + direction + " not supported");
		}
	}

	public enum Direction {
		UP,
		DOWN
	}
}
