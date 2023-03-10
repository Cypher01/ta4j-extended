package org.ta4j.core.criteria;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Position;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;

/**
 * Average number of bars criterion. Uses {@link NumberOfBarsCriterion}.
 */
public class AverageNumberOfBarsCriterion extends AbstractAnalysisCriterion {
	private final NumberOfBarsCriterion numberOfBarsCriterion = new NumberOfBarsCriterion();

	@Override
	public Num calculate(BarSeries series, Position position) {
		return numberOfBarsCriterion.calculate(series, position);
	}

	@Override
	public Num calculate(BarSeries series, TradingRecord tradingRecord) {
		return numberOfBarsCriterion.calculate(series, tradingRecord).dividedBy(series.numOf(tradingRecord.getPositionCount()))
				.plus(series.numOf(0.5)).floor();
	}

	@Override
	public boolean betterThan(Num criterionValue1, Num criterionValue2) {
		return criterionValue1.isLessThan(criterionValue2);
	}
}
