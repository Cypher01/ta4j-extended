package org.ta4j.core.rules;

import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;

/**
 * Switch Rule.
 * Satisfied when the rule switched from true to false or vice versa.
 */
public class SwitchRule extends AbstractRule {
	private final Rule rule;

	public SwitchRule(Rule rule) {
		this.rule = rule;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		if (index == 0) {
			return false;
		}

		boolean satisfied = rule.isSatisfied(index) != rule.isSatisfied(index - 1);
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}
}
