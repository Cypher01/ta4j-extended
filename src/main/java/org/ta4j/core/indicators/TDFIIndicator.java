package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.AverageIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.num.Num;

/**
 * Trend Direction Force Index (TDFI) by causecelebre.
 * <a href="https://www.tradingview.com/script/HUpIful1-Trend-Direction-Force-Index-v2-TDFI-wm/">TradingView</a>
 * The MA mode configuration was not added, this implementation only supports EMA.
 */
public class TDFIIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> tdfiIndicator;
	private final int unstableBars;

	public TDFIIndicator(BarSeries series, int lookback, int mmaLength, int smmaLength, int nLength) {
		this(new ClosePriceIndicator(series), lookback, mmaLength, smmaLength, nLength);
	}

	public TDFIIndicator(Indicator<Num> indicator, int lookback, int mmaLength, int smmaLength, int nLength) {
		super(indicator.getBarSeries());

		Indicator<Num> mmaIndicator = new EMAIndicator(TransformIndicator.multiply(indicator, 1000), mmaLength);
		Indicator<Num> smmaIndicator = new EMAIndicator(mmaIndicator, smmaLength);
		CombineIndicator impetMmaIndicator = CombineIndicator.minus(mmaIndicator, new PreviousValueIndicator(mmaIndicator));
		CombineIndicator impetSmmaIndicator = CombineIndicator.minus(smmaIndicator, new PreviousValueIndicator(smmaIndicator));
		TransformIndicator divMa = TransformIndicator.abs(CombineIndicator.minus(mmaIndicator, smmaIndicator));
		AverageIndicator averimpet = new AverageIndicator(impetMmaIndicator, impetSmmaIndicator);
		CombineIndicator tdf = CombineIndicator.multiply(divMa, TransformIndicator.pow(averimpet, nLength));
		this.tdfiIndicator = CombineIndicator.divide(tdf, new HighestValueIndicator(TransformIndicator.abs(tdf), lookback * nLength));
		this.unstableBars = Math.max(Math.max(mmaLength, smmaLength), lookback * nLength);
	}

	@Override
	public Num getValue(int index) {
		return tdfiIndicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return unstableBars;
	}
}
