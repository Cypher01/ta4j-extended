package org.ta4j.core.indicators.helpers;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.SMMAIndicator;
import org.ta4j.core.indicators.VIDYAIndicator;
import org.ta4j.core.indicators.VWMAIndicator;
import org.ta4j.core.indicators.averages.*;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.indicators.statistics.SimpleLinearRegressionIndicator;
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

    public static TrailingIndicator atr(BarSeries series, int barCount, double multiplier, int startIndex, Direction direction) {
        return atr(new ClosePriceIndicator(series), barCount, multiplier, startIndex, direction);
    }

    public static TrailingIndicator atr(Indicator<Num> indicator, int barCount, double multiplier, int startIndex, Direction direction) {
        return atr(indicator, new ATRIndicatorPlus(indicator.getBarSeries(), barCount), multiplier, startIndex, direction);
    }

    public static TrailingIndicator atr(Indicator<Num> indicator, ATRIndicatorPlus atrIndicator, double multiplier, int startIndex, Direction direction) {
        return switch (direction) {
            case UP ->
                    trail(BinaryOperationIndicator.difference(indicator, BinaryOperationIndicator.product(atrIndicator, multiplier)), startIndex, direction);
            case DOWN ->
                    trail(BinaryOperationIndicator.sum(indicator, BinaryOperationIndicator.product(atrIndicator, multiplier)), startIndex, direction);
        };
    }

    public static TrailingIndicator cma(BarSeries series, int barCount, int startIndex, Direction direction) {
        return trail(new CMAIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
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

    public static TrailingIndicator lsma(BarSeries series, int barCount, int startIndex, Direction direction) {
        return trail(new SimpleLinearRegressionIndicator(new ClosePriceIndicator(series), barCount), startIndex, direction);
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

    public Direction getDirection() {
        return direction;
    }

    @Override
    protected Num calculate(int index) {
        if (index < startIndex) {
            return NaN;
        } else if (index == startIndex) {
            return indicator.getValue(index);
        }

        return switch (direction) {
            case UP -> getValue(index - 1).max(indicator.getValue(index));
            case DOWN -> getValue(index - 1).min(indicator.getValue(index));
        };
    }

    @Override
    public int getCountOfUnstableBars() {
        return getBarSeries().getBarCount();
    }

    public enum Direction {
        UP,
        DOWN
    }
}
