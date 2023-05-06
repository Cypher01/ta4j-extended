package org.ta4j.core.rules;

import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.SqueezeMomentumIndicator;

/**
 * Rule for SqueezeMomentumIndicator squeezeOn.
 */
public class SqueezeOnRule extends AbstractRule {
	private final SqueezeMomentumIndicator squeezeMomentumIndicator;

	public SqueezeOnRule(SqueezeMomentumIndicator squeezeMomentumIndicator) {
		this.squeezeMomentumIndicator = squeezeMomentumIndicator;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean satisfied = squeezeMomentumIndicator.squeezeOn(index);
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
