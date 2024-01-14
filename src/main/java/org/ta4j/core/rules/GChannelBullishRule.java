package org.ta4j.core.rules;

import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.gchannel.GChannelAverageIndicator;

/**
 * Rule for GChannelAverageIndicator bullish.
 */
public class GChannelBullishRule extends AbstractRule {
	private final GChannelAverageIndicator gChannelAverageIndicator;

	public GChannelBullishRule(GChannelAverageIndicator gChannelAverageIndicator) {
		this.gChannelAverageIndicator = gChannelAverageIndicator;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean satisfied = gChannelAverageIndicator.bullish(index);
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
