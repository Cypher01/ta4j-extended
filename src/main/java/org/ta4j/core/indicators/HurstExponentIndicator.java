package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Simple Hurst Exponent indicator by QuantNomad.
 * <a href="https://www.tradingview.com/script/v267CGzx-Simple-Hurst-Exponent-QuantNomad/">TradingView</a>
 */
public class HurstExponentIndicator extends CachedIndicator<Num> {
	private final int barCount;
	private final TransformIndicator pnl;
	private final SMAIndicator meanPnl;

	public HurstExponentIndicator(BarSeries series, int barCount) {
		this(new ClosePriceIndicator(series), barCount);
	}

	public HurstExponentIndicator(ClosePriceIndicator indicator, int barCount) {
		super(indicator.getBarSeries());

		this.barCount = barCount;
		this.pnl = TransformIndicator.minus(CombineIndicator.divide(indicator, new PreviousValueIndicator(indicator)), 1);
		this.meanPnl = new SMAIndicator(pnl, barCount);
	}

	@Override
	protected Num calculate(int index) {
		TransformIndicator distanceFromMean = TransformIndicator.minus(pnl, meanPnl.getValue(index).doubleValue());
		TransformIndicator distanceFromMeanPow = new TransformIndicator(distanceFromMean, val -> val.pow(2));

		Num cum = numOf(0);
		Num cumMin = numOf(Double.MAX_VALUE);
		Num cumMax = numOf(Double.MIN_VALUE);

		for (int i = Math.max(0, index - barCount + 1); i <= index; i++) {
			cum = cum.plus(distanceFromMean.getValue(i));
			cumMin = cumMin.min(cum);
			cumMax = cumMax.max(cum);
		}

		Num devSum = numOf(0);

		for (int i = Math.max(0, index - barCount + 1); i <= index; i++) {
			devSum = devSum.plus(distanceFromMeanPow.getValue(i));
		}

		Num sd = devSum.dividedBy(numOf(barCount - 1)).sqrt();
		Num rs = cumMax.minus(cumMin).dividedBy(sd);

		return rs.log().dividedBy(numOf(barCount).log());
	}
}
