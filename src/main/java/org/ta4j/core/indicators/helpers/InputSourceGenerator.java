package org.ta4j.core.indicators.helpers;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public final class InputSourceGenerator {
	private InputSourceGenerator() {
	}

	public static Indicator<Num> getInputSourceIndicator(BarSeries series, InputSource inputSource) {
		switch (inputSource) {
			case open:
				return new OpenPriceIndicator(series);
			case high:
				return new HighPriceIndicator(series);
			case low:
				return new LowPriceIndicator(series);
			case close:
				return new ClosePriceIndicator(series);
			case hl2:
				return new HL2PriceIndicator(series);
			case hlc3:
				return new HLC3PriceIndicator(series);
			case ohlc4:
				return new OHLC4PriceIndicator(series);
			default:
				throw new IllegalArgumentException("Input source " + inputSource + " not supported");
		}
	}
}
