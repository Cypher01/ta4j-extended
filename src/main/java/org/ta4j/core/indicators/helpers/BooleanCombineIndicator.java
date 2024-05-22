package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.BooleanTransformIndicator.BooleanTransformType;
import org.ta4j.core.num.Num;

/**
 * Boolean combine indicator.
 * This is based on BooleanTransformIndicator with the same logical operators,
 * but instead of comparing one indicator to a coefficient, it compares two indicators.
 * Additionally, the indicator supports converting true and false to 1 and 0.
 */
public class BooleanCombineIndicator extends CachedIndicator<Boolean> {
	private final Indicator<Num> indicator1;
	private final Indicator<Num> indicator2;
	private final BooleanTransformType type;

	public BooleanCombineIndicator(Indicator<Num> indicator1, Indicator<Num> indicator2, BooleanTransformType type) {
		super(indicator1);

		this.indicator1 = indicator1;
		this.indicator2 = indicator2;
		this.type = type;
	}

	@Override
	protected Boolean calculate(int index) {
		Num value1 = indicator1.getValue(index);
		Num value2 = indicator2.getValue(index);

		switch (type) {
			case equals:
				return value1.isEqual(value2);
			case isGreaterThan:
				return value1.isGreaterThan(value2);
			case isGreaterThanOrEqual:
				return value1.isGreaterThanOrEqual(value2);
			case isLessThan:
				return value1.isLessThan(value2);
			case isLessThanOrEqual:
				return value1.isLessThanOrEqual(value2);
			default:
				return false;
		}
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
