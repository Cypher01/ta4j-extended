package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ChangeIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Relative Momentum Index (RMI) indicator by everget.
 * <a https://www.tradingview.com/script/kwIt9OgQ-Relative-Momentum-Index/">TradingView</a>
 */
public class RMIIndicator extends CachedIndicator<Num> {
	private final Indicator<Num> up;
	private final Indicator<Num> down;
	private final int unstableBars;

	public RMIIndicator(BarSeries series, int barCount, int barCountMomentum) {
		this(new ClosePriceIndicator(series), barCount, barCountMomentum);
	}

	public RMIIndicator(Indicator<Num> indicator, int barCount, int barCountMomentum) {
		super(indicator.getBarSeries());

		ChangeIndicator momentum = new ChangeIndicator(indicator, barCountMomentum);
		ConstantIndicator<Num> zero = new ConstantIndicator<>(indicator.getBarSeries(), zero());
		this.up = new MMAIndicator(CombineIndicator.max(momentum, zero), barCount);
		this.down = new MMAIndicator(TransformIndicator.abs(CombineIndicator.min(momentum, zero)), barCount);
		this.unstableBars = Math.max(barCount, barCountMomentum);
	}

	@Override
	protected Num calculate(int index) {
		Num upValue = up.getValue(index);
		Num downValue = down.getValue(index);

		if (zero().isEqual(downValue)) {
			return hundred();
		} else if (zero().isEqual(upValue)) {
			return zero();
		} else {
			return hundred().minus(hundred().dividedBy(one().plus(upValue.dividedBy(downValue))));
		}
	}

	@Override
	public int getUnstableBars() {
		return unstableBars;
	}
}
