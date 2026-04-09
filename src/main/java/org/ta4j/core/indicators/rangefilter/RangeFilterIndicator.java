package org.ta4j.core.indicators.rangefilter;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

/**
 * //@version=5
 * indicator(title="Range Filter Buy and Sell 5min Test", shorttitle="Range Filter", overlay=true)
 *
 * // Original Script > @DonovanWall
 * // Adapted Version > @guikroth
 * //
 * // Updated PineScript to version 5
 * // Republished by > @tvenn
 *
 * //////////////////////////////////////////////////////////////////////////
 * // Settings for 5min chart, BTCUSDC. For Other coin, change the parameters
 * //////////////////////////////////////////////////////////////////////////
 *
 * // Color variables
 * upColor   = color.white
 * midColor  = #90bff9
 * downColor = color.blue
 *
 * // Source
 * src = input(defval=close, title="Source")
 *
 * // Sampling Period
 * // Settings for 5min chart, BTCUSDC. For Other coin, change the paremeters
 * per = input.int(defval=100, minval=1, title="Sampling Period")
 *
 * // Range Multiplier
 * mult = input.float(defval=3.0, minval=0.1, title="Range Multiplier")
 *
 * // Smooth Average Range
 * smoothrng(x, t, m) =>
 *     wper = t * 2 - 1
 *     avrng = ta.ema(math.abs(x - x[1]), t)
 *     smoothrng = ta.ema(avrng, wper) * m
 *     smoothrng
 * smrng = smoothrng(src, per, mult)
 *
 * // Range Filter
 * rngfilt(x, r) =>
 *     rngfilt = x
 *     rngfilt := x > nz(rngfilt[1]) ? x - r < nz(rngfilt[1]) ? nz(rngfilt[1]) : x - r :
 *        x + r > nz(rngfilt[1]) ? nz(rngfilt[1]) : x + r
 *     rngfilt
 * filt = rngfilt(src, smrng)
 *
 * // Filter Direction
 * upward = 0.0
 * upward := filt > filt[1] ? nz(upward[1]) + 1 : filt < filt[1] ? 0 : nz(upward[1])
 * downward = 0.0
 * downward := filt < filt[1] ? nz(downward[1]) + 1 : filt > filt[1] ? 0 : nz(downward[1])
 *
 * // Target Bands
 * hband = filt + smrng
 * lband = filt - smrng
 *
 * // Colors
 * filtcolor = upward > 0 ? upColor : downward > 0 ? downColor : midColor
 * barcolor = src > filt and src > src[1] and upward > 0 ? upColor :
 *    src > filt and src < src[1] and upward > 0 ? upColor :
 *    src < filt and src < src[1] and downward > 0 ? downColor :
 *    src < filt and src > src[1] and downward > 0 ? downColor : midColor
 *
 * filtplot = plot(filt, color=filtcolor, linewidth=2, title="Range Filter")
 *
 * // Target
 * hbandplot = plot(hband, color=color.new(upColor, 70), title="High Target")
 * lbandplot = plot(lband, color=color.new(downColor, 70), title="Low Target")
 *
 * // Fills
 * fill(hbandplot, filtplot, color=color.new(upColor, 90), title="High Target Range")
 * fill(lbandplot, filtplot, color=color.new(downColor, 90), title="Low Target Range")
 *
 * // Bar Color
 * barcolor(barcolor)
 *
 * // Break Outs
 * longCond = bool(na)
 * shortCond = bool(na)
 * longCond := src > filt and src > src[1] and upward > 0 or
 *    src > filt and src < src[1] and upward > 0
 * shortCond := src < filt and src < src[1] and downward > 0 or
 *    src < filt and src > src[1] and downward > 0
 *
 * CondIni = 0
 * CondIni := longCond ? 1 : shortCond ? -1 : CondIni[1]
 * longCondition = longCond and CondIni[1] == -1
 * shortCondition = shortCond and CondIni[1] == 1
 *
 * //Alerts
 * plotshape(longCondition, title="Buy Signal", text="Buy", textcolor=color.white, style=shape.labelup, size=size.small, location=location.belowbar, color=color.new(#aaaaaa, 20))
 * plotshape(shortCondition, title="Sell Signal", text="Sell", textcolor=color.white, style=shape.labeldown, size=size.small, location=location.abovebar, color=color.new(downColor, 20))
 *
 *
 * alertcondition(longCondition, title="Buy alert on Range Filter", message="Buy alert on Range Filter")
 * alertcondition(shortCondition, title="Sell alert on Range Filter", message="Sell alert on Range Filter")
 * alertcondition(longCondition or shortCondition, title="Buy and Sell alert on Range Filter", message="Buy and Sell alert on Range Filter")
 *
 *
 * // For use as a Strategy
 * // ___________________________________________________________________________________________________________
 *
 * // 1. Replace the word "indicator" on line 2 with the word "strategy"
 * // 2. Uncomment the code below by highlighting it and pressing Ctl + '/'
 *
 * // Settings for 5min BTCUSDT. For other timeframes and assets, adjust the parameters within the settings menu.
 * // ___________________________________________________________________________________________________________
 *
 * // grp_STRAT   = "Strategy settings"
 * // timeInput   = input.time(timestamp("1 Nov 2022 00:00 +0000"), title="Start date", group=grp_STRAT)
 * // tpInPips    = input.int(400, title="TP (in pips)", group=grp_STRAT)
 * // slInPips    = input.int(100, title="SL (in pips)", group=grp_STRAT)
 *
 * // timePeriod  = time >= timeInput
 * // notInTrade  = strategy.position_size <= 0
 *
 * // if(longCondition and timePeriod and notInTrade)
 * //     strategy.entry("Long", strategy.long)
 * //     strategy.exit("Exit long", "Long", loss=slInPips, profit=tpInPips)
 * // if(shortCondition and timePeriod and notInTrade)
 * //     strategy.entry("Short", strategy.short)
 * //     strategy.exit("Exit short", "Short", loss=slInPips, profit=tpInPips)
 *
 * Range Filter indicator by guikroth.
 * <a
 * href="https://www.tradingview.com/script/J8GzFGfD-Range-Filter-Buy-and-Sell-5min-guikroth-version/">TradingView</a>
 */
public class RangeFilterIndicator extends CachedIndicator<Num> {
    private final Indicator<Num> indicator;
    private final SmoothRangeIndicator smoothRangeIndicator;

    public RangeFilterIndicator(BarSeries series, int barCount, double multiplier) {
        this(new ClosePriceIndicator(series), barCount, multiplier);
    }

    public RangeFilterIndicator(Indicator<Num> indicator, int barCount, double multiplier) {
        this(indicator, new SmoothRangeIndicator(indicator, barCount, multiplier));
    }

    public RangeFilterIndicator(Indicator<Num> indicator, SmoothRangeIndicator smoothRangeIndicator) {
        super(indicator);

        this.indicator = indicator;
        this.smoothRangeIndicator = smoothRangeIndicator;
    }

    @Override protected Num calculate(int index) {
        // PineScript: nz(rngfilt[1]) — replaces na with 0.
        // When the previous rngfilt value is NaN (because smrng was still NaN on early bars),
        // we must substitute 0, otherwise all comparisons against NaN return false and the
        // filter takes the wrong branch (x + r instead of x - r for positive prices),
        // corrupting every subsequent cached value.
        Num prevValue = getBarSeries().numFactory().zero();

        if (index > 0) {
            Num prev = getValue(index - 1);
            if (!Num.isNaNOrNull(prev)) {
                prevValue = prev;
            }
        }

        Num x = indicator.getValue(index);
        Num r = smoothRangeIndicator.getValue(index);
        Num rngfilt;

        if (x.isGreaterThan(prevValue)) {
            if (x.minus(r).isLessThan(prevValue)) {
                rngfilt = prevValue;
            } else {
                rngfilt = x.minus(r);
            }
        } else {
            if (x.plus(r).isGreaterThan(prevValue)) {
                rngfilt = prevValue;
            } else {
                rngfilt = x.plus(r);
            }
        }

        return rngfilt;
    }

    @Override public int getCountOfUnstableBars() {
        return smoothRangeIndicator.getCountOfUnstableBars();
    }
}
