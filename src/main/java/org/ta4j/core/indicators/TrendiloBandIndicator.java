package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Trendilo indicator by dudeowns.
 * <a href="https://www.tradingview.com/script/h5kMWewu-Trendilo-OPEN-SOURCE/">TradingView</a>
 */
public class TrendiloBandIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> rms;

	public TrendiloBandIndicator(TrendiloIndicator trendiloIndicator, double multiplier, int length) {
		super(trendiloIndicator.getBarSeries());

		TransformIndicator avpchPow = new TransformIndicator(trendiloIndicator, val -> val.pow(2));
		SMAIndicator avpchPowSma = new SMAIndicator(avpchPow, length);
		this.rms = TransformIndicator.multiply(TransformIndicator.sqrt(avpchPowSma), multiplier);
	}

	@Override
	public Num getValue(int index) {
		return rms.getValue(index);
	}
}
