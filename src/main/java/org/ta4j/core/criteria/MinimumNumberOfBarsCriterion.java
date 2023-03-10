package org.ta4j.core.criteria;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Position;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;

/**
 * Minimum number of bars criterion. Based on {@link NumberOfBarsCriterion}.
 */
public class MinimumNumberOfBarsCriterion extends AbstractAnalysisCriterion {
	private final NumberOfBarsCriterion numberOfBarsCriterion = new NumberOfBarsCriterion();

	@Override
	public Num calculate(BarSeries series, Position position) {
		return numberOfBarsCriterion.calculate(series, position);
	}

	@Override
	public Num calculate(BarSeries series, TradingRecord tradingRecord) {
		return tradingRecord.getPositions()
				.stream()
				.filter(Position::isClosed)
				.map(t -> calculate(series, t))
				.min(Num::compareTo)
				.orElse(series.numOf(0));
	}

	@Override
	public boolean betterThan(Num criterionValue1, Num criterionValue2) {
		return criterionValue1.isLessThan(criterionValue2);
	}
}
