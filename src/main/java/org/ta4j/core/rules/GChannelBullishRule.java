package org.ta4j.core.rules;

import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.GChannelIndicator;

/**
 * Rule for GChannelIndicator bullish.
 */
public class GChannelBullishRule extends AbstractRule {
	private final GChannelIndicator gChannelIndicator;

	public GChannelBullishRule(GChannelIndicator gChannelIndicator) {
		this.gChannelIndicator = gChannelIndicator;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean satisfied = gChannelIndicator.bullish(index);
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
