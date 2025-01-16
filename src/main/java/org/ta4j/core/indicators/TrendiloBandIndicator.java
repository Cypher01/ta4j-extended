package org.ta4j.core.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.NzIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Trendilo indicator by dudeowns.
 * <a href="https://www.tradingview.com/script/h5kMWewu-Trendilo-OPEN-SOURCE/">TradingView</a>
 */
public class TrendiloBandIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> rms;
	private final int barCount;

	public TrendiloBandIndicator(TrendiloIndicator trendiloIndicator, double multiplier, int barCount) {
		super(trendiloIndicator.getBarSeries());

		TransformIndicator avpchPow = TransformIndicator.pow(new NzIndicator(trendiloIndicator), 2);
		SMAIndicator avpchPowSma = new SMAIndicator(avpchPow, barCount);
		this.rms = TransformIndicator.multiply(TransformIndicator.sqrt(avpchPowSma), multiplier);
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		return rms.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}
}
