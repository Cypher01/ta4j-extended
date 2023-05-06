package org.ta4j.core.rules;

import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.SqueezeMomentumIndicator;

/**
 * Rule for SqueezeMomentumIndicator squeezeOff.
 */
public class SqueezeOffRule extends AbstractRule {
	private final SqueezeMomentumIndicator squeezeMomentumIndicator;

	public SqueezeOffRule(SqueezeMomentumIndicator squeezeMomentumIndicator) {
		this.squeezeMomentumIndicator = squeezeMomentumIndicator;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean satisfied = squeezeMomentumIndicator.squeezeOff(index);
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
