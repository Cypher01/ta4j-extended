package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import java.util.function.BiFunction;

/**
 * Boolean combine indicator.
 * This is based on BooleanTransformIndicator with the same logical operators,
 * but instead of comparing one indicator to a constant, it compares two indicators.
 * Additionally, the indicator supports converting true and false to 1 and 0.
 */
public class BooleanCombineIndicator extends CachedIndicator<Boolean> {
	private final Indicator<Num> indicator1;
	private final Indicator<Num> indicator2;
	private final BiFunction<Num, Num, Boolean> transform;

	public static BooleanCombineIndicator isEqual(Indicator<Num> indicator1, Indicator<Num> indicator2) {
		return new BooleanCombineIndicator(indicator1, indicator2, Num::isEqual);
	}

	public static BooleanCombineIndicator isGreaterThan(Indicator<Num> indicator1, Indicator<Num> indicator2) {
		return new BooleanCombineIndicator(indicator1, indicator2, Num::isGreaterThan);
	}

	public static BooleanCombineIndicator isGreaterThanOrEqual(Indicator<Num> indicator1, Indicator<Num> indicator2) {
		return new BooleanCombineIndicator(indicator1, indicator2, Num::isGreaterThanOrEqual);
	}

	public static BooleanCombineIndicator isLessThan(Indicator<Num> indicator1, Indicator<Num> indicator2) {
		return new BooleanCombineIndicator(indicator1, indicator2, Num::isLessThan);
	}

	public static BooleanCombineIndicator isLessThanOrEqual(Indicator<Num> indicator1, Indicator<Num> indicator2) {
		return new BooleanCombineIndicator(indicator1, indicator2, Num::isLessThanOrEqual);
	}

	public BooleanCombineIndicator(Indicator<Num> indicator1, Indicator<Num> indicator2, BiFunction<Num, Num, Boolean> transform) {
		super(indicator1);

		this.indicator1 = indicator1;
		this.indicator2 = indicator2;
		this.transform = transform;
	}

	@Override
	protected Boolean calculate(int index) {
		return transform.apply(indicator1.getValue(index), indicator2.getValue(index));
	}

	@Override
	public int getUnstableBars() {
		return Math.max(indicator1.getUnstableBars(), indicator2.getUnstableBars());
	}

	public BooleanToNumIndicator asNum() {
		return new BooleanToNumIndicator(this);
	}

	public static class BooleanToNumIndicator extends CachedIndicator<Num> {
		private final BooleanCombineIndicator indicator;

		public BooleanToNumIndicator(BooleanCombineIndicator indicator) {
			super(indicator);

			this.indicator = indicator;
		}

		@Override
		protected Num calculate(int index) {
			return indicator.getValue(index) ? one() : zero();
		}

		@Override
		public int getUnstableBars() {
			return indicator.getUnstableBars();
		}
	}
}
