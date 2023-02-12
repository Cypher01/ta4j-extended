package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.ATRIndicatorPlus.Input;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.AverageIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.indicators.statistics.SimpleLinearRegressionIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

/**
 * Squeeze Momentum indicator by LazyBear.
 * <a href="https://www.tradingview.com/script/nqQ1DT5a-Squeeze-Momentum-Indicator-LazyBear/">TradingView</a>
 * The keltner channel indicators of ta4j-core are not used, because they use slightly different calculations (SMA vs. EMA).
 */
public class SqueezeMomentumIndicator extends CachedIndicator<Num> {
	private final BollingerBandsUpperIndicator upperBB;
	private final BollingerBandsLowerIndicator lowerBB;
	private final Indicator<Num> upperKC;
	private final Indicator<Num> lowerKC;
	private final Indicator<Num> squeezeMomentumIndicator;

	public SqueezeMomentumIndicator(BarSeries series) {
		this(series, 20, 2d, 20, 1.5);
	}

	public SqueezeMomentumIndicator(BarSeries series, int bbLength, double bbMultFactor, int kcLength, double kcMultFactor) {
		this(new ClosePriceIndicator(series), bbLength, bbMultFactor, kcLength, kcMultFactor);
	}

	public SqueezeMomentumIndicator(ClosePriceIndicator indicator, int bbLength, double bbMultFactor, int kcLength, double kcMultFactor) {
		super(indicator);
		BarSeries series = indicator.getBarSeries();

		BollingerBandsMiddleIndicator middleBB = new BollingerBandsMiddleIndicator(new SMAIndicator(indicator, bbLength));
		StandardDeviationIndicator standardDev = new StandardDeviationIndicator(indicator, bbLength);
		this.upperBB = new BollingerBandsUpperIndicator(middleBB, standardDev, numOf(bbMultFactor));
		this.lowerBB = new BollingerBandsLowerIndicator(middleBB, standardDev, numOf(bbMultFactor));

		Indicator<Num> ma = new SMAIndicator(indicator, kcLength);
		Indicator<Num> range = TransformIndicator.multiply(new ATRIndicatorPlus(series, kcLength, Input.SMA), kcMultFactor);
		this.upperKC = CombineIndicator.plus(ma, range);
		this.lowerKC = CombineIndicator.minus(ma, range);

		HighestValueIndicator highestHigh = new HighestValueIndicator(new HighPriceIndicator(series), kcLength);
		LowestValueIndicator lowestLow = new LowestValueIndicator(new LowPriceIndicator(series), kcLength);
		Indicator<Num> averageIndicator = new AverageIndicator(highestHigh, lowestLow, ma);
		this.squeezeMomentumIndicator = new SimpleLinearRegressionIndicator(CombineIndicator.minus(indicator, averageIndicator), kcLength);
	}

	public boolean squeezeOn(int index) {
		return (lowerBB.getValue(index).isGreaterThan(lowerKC.getValue(index))) && (upperBB.getValue(index).isLessThan(upperKC.getValue(index)));
	}

	public boolean squeezeOff(int index) {
		return (lowerBB.getValue(index).isLessThan(lowerKC.getValue(index))) && (upperBB.getValue(index).isGreaterThan(upperKC.getValue(index)));
	}

	@Override
	protected Num calculate(int index) {
		return squeezeMomentumIndicator.getValue(index);
	}
}
