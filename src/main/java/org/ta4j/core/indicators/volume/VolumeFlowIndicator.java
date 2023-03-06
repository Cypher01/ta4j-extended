package org.ta4j.core.indicators.volume;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.helpers.SumValuesIndicator;
import org.ta4j.core.indicators.helpers.TransformIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

/**
 * Volume flow indicator by LazyBear.
 * <a href="https://www.tradingview.com/script/MhlDpfdS-Volume-Flow-Indicator-LazyBear/">TradingView</a>
 * The smoothing was not added, as this indicator can be simply wrapped into an SMAIndicator with barCount 3.
 */
public class VolumeFlowIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> volumeFlowIndicator;

	public VolumeFlowIndicator(BarSeries series, int barCount, double coeff, double vcoeff) {
		this(new TypicalPriceIndicator(series), barCount, coeff, vcoeff);
	}

	public VolumeFlowIndicator(Indicator<Num> indicator, int barCount, double coeff, double vcoeff) {
		super(indicator.getBarSeries());

		TransformIndicator log = TransformIndicator.log(indicator);
		PreviousValueIndicator prevLog = new PreviousValueIndicator(log, 1);
		CombineIndicator inter = CombineIndicator.minus(log, prevLog);
		StandardDeviationIndicator vinter = new StandardDeviationIndicator(inter, 30);
		TransformIndicator cutoff = TransformIndicator.multiply(CombineIndicator.multiply(vinter, new ClosePriceIndicator(indicator.getBarSeries())), coeff);
		VolumeIndicator volumeIndicator = new VolumeIndicator(indicator.getBarSeries());
		SMAIndicator vave = new SMAIndicator(volumeIndicator, barCount);
		TransformIndicator vmax = TransformIndicator.multiply(vave, vcoeff);
		CombineIndicator vc = CombineIndicator.min(volumeIndicator, vmax);
		PreviousValueIndicator prevIndicator = new PreviousValueIndicator(indicator, 1);
		CombineIndicator mf = CombineIndicator.minus(indicator, prevIndicator);
		VPCIndicator vcp = new VPCIndicator(mf, cutoff, vc);
		SumValuesIndicator sum = new SumValuesIndicator(vcp, barCount);
		this.volumeFlowIndicator = CombineIndicator.divide(sum, vave);
	}

	@Override
	public Num getValue(int index) {
		return volumeFlowIndicator.getValue(index);
	}

	private static class VPCIndicator extends CachedIndicator<Num> {
		private final Indicator<Num> mf;
		private final Indicator<Num> cutoff;
		private final Indicator<Num> cutoffNeg;
		private final Indicator<Num> vc;
		private final Indicator<Num> vcNeg;

		private VPCIndicator(Indicator<Num> mf, Indicator<Num> cutoff, Indicator<Num> vc) {
			super(mf);

			this.mf = mf;
			this.cutoff = cutoff;
			this.cutoffNeg = TransformIndicator.multiply(cutoff, -1);
			this.vc = vc;
			this.vcNeg = TransformIndicator.multiply(vc, -1);
		}

		@Override
		protected Num calculate(int index) {
			if (mf.getValue(index).isGreaterThan(cutoff.getValue(index))) {
				return vc.getValue(index);
			} else if (mf.getValue(index).isLessThan(cutoffNeg.getValue(index))) {
				return vcNeg.getValue(index);
			} else {
				return numOf(0);
			}
		}
	}
}
