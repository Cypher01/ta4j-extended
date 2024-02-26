package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.BooleanCombineIndicator;
import org.ta4j.core.indicators.helpers.BooleanTransformIndicator.BooleanTransformType;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.SumValuesIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

/**
 * Stiffness indicator by everget.
 * <a href="https://www.tradingview.com/script/Ds1akUBu-Stiffness-Indicator/">TradingView</a>
 */
public class StiffnessIndicator extends AbstractIndicator<Num> {
	private final EMAIndicator stiffness;

	public StiffnessIndicator(BarSeries series, int barCount, int stiffLength, int stiffSmooth) {
		this(new ClosePriceIndicator(series), barCount, stiffLength, stiffSmooth);
	}

	public StiffnessIndicator(ClosePriceIndicator indicator, int barCount, int stiffLength, int stiffSmooth) {
		super(indicator.getBarSeries());

		CombineIndicator bound = CombineIndicator.minus(new SMAIndicator(indicator, barCount), TransformIndicator.divide(new StandardDeviationIndicator(indicator, barCount), 5));
		SumValuesIndicator sumAbove = new SumValuesIndicator(new BooleanCombineIndicator(indicator, bound, BooleanTransformType.isGreaterThan).asNum(), stiffLength);
		this.stiffness = new EMAIndicator(TransformIndicator.divide(TransformIndicator.multiply(sumAbove, 100), stiffLength), stiffSmooth);
	}

	@Override
	public Num getValue(int index) {
		return stiffness.getValue(index);
	}
}
