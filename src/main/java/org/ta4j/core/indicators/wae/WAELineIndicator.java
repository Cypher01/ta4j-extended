package org.ta4j.core.indicators.wae;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandFacade;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.num.Num;

/**
 * Waddah Attar Explosion Line indicator by ShayanKM.
 * <a href="https://www.tradingview.com/script/d9IjcYyS-Waddah-Attar-Explosion-V2-SHK/">TradingView</a>
 */
public class WAELineIndicator extends AbstractIndicator<Num> {
	private final Indicator<Num> lineIndicator;
	private final int barCount;

	public WAELineIndicator(BarSeries series, int barCount, double multiplier) {
		this(new ClosePriceIndicator(series), barCount, multiplier);
	}

	public WAELineIndicator(Indicator<Num> indicator, int barCount, double multiplier) {
		super(indicator.getBarSeries());

		BollingerBandFacade bollingerBandFacade = new BollingerBandFacade(indicator, barCount, multiplier);
		this.lineIndicator = CombineIndicator.minus(bollingerBandFacade.upper(), bollingerBandFacade.lower());
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		return lineIndicator.getValue(index);
	}

	@Override
	public int getCountOfUnstableBars() {
		return barCount;
	}
}
