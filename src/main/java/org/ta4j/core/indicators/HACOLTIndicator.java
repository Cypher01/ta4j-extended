package org.ta4j.core.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.TripleEMAIndicator;
import org.ta4j.core.indicators.candles.RealBodyIndicator;
import org.ta4j.core.indicators.candles.RealRangeIndicator;
import org.ta4j.core.indicators.helpers.BooleanCombineIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LogicIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.indicators.helpers.OHLC4PriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousBooleanValueIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.numeric.BinaryOperationIndicator;
import org.ta4j.core.indicators.numeric.UnaryOperationIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.NumFactory;

/**
 * Heikin Ashi Candles Oscillator Long Term (HACOLT) indicator by LazyBear. <a
 * href="https://www.tradingview.com/script/4zuhGaAU-Vervoort-Heiken-Ashi-LongTerm-Candlestick-Oscillator-LazyBear/">
 * TradingView</a>
 * Original PineScript implementation:
 * <p>
 * length = input(defval=55, title="TEMA Period")
 * emaLength = input(defval=60, title="EMA Period")
 * candleSizeFactor = input(defval=1.1, title="Candle size factor")
 * <p>
 * calc_tema(src, length) =>
 * ema1 = ema(src, length)
 * ema2 = ema(ema1, length)
 * ema3 = ema(ema2, length)
 * 3 * (ema1 - ema2) + ema3
 * <p>
 * haOpen = nz((haOpen[1] + ohlc4) / 2, ohlc4)
 * haClose = (haOpen + max(high, haOpen) + min(low, haOpen) + ohlc4) / 4
 * thaClose = calc_tema(haClose, length)
 * thl2 = calc_tema(hl2, length)
 * haCloseSmooth = 2 * thaClose - calc_tema(thaClose, length)
 * hl2Smooth = 2 * thl2 - calc_tema(thl2, length)
 * shortCandle = abs(close - open) < ((high - low) * candleSizeFactor)
 * keepn1 = ((haClose >= haOpen) and (haClose[1] >= haOpen[1])) or (close >= haClose) or (high > high[1]) or (low >
 * low[1]) or (hl2Smooth >= haCloseSmooth)
 * keepall1 = keepn1 or (keepn1[1] and (close >= open) or (close >= close[1]))
 * keep13 = shortCandle and (high >= low[1])
 * utr = keepall1 or (keepall1[1] and keep13)
 * keepn2 = (haClose < haOpen) and (haClose[1] < haOpen[1]) or (hl2Smooth < haCloseSmooth)
 * keep23 = shortCandle and (low <= high[1])
 * keepall2 = keepn2 or (keepn2[1] and (close < open) or (close < close[1]))
 * dtr = keepall2 or (keepall2[1] and keep23)
 * upw = dtr == 0 and dtr[1] and utr
 * dnw = utr == 0 and utr[1] and dtr
 * upwWithOffset = upw != dnw ? upw : nz(upwWithOffset[1])
 * <p>
 * buySig = upw or (not dnw and (na(upwWithOffset) ? 0 : upwWithOffset))
 * ltSellSig = close < ema(close, emaLength)
 * neutralSig = buySig or (ltSellSig ? 0 : nz(neutralSig[1]))
 * hacolt = buySig ? 1 : neutralSig ? 0 : -1
 * <p>
 * Cleaned up PineScript v6 implementation:
 * <p>
 * //@version=6
 * indicator("HACOLT", shorttitle = "HACOLT", overlay = false)
 * <p>
 * import TradingView/ta/9
 * <p>
 * length = input.int(55, title = "TEMA Period")
 * emaLength = input.int(60, title = "EMA Period")
 * candleSizeFactor = input.float(1.1, title = "Candle size factor")
 * <p>
 * var float haOpen = na
 * haOpen := na(haOpen[1]) ? ohlc4 : (haOpen[1] + ohlc4) / 2
 * haClose = (haOpen + math.max(high, haOpen) + math.min(low, haOpen) + ohlc4) / 4
 * <p>
 * thl2 = ta.tema(hl2, length)
 * thaClose = ta.tema(haClose, length)
 * hl2Smooth = 2 * thl2 - ta.tema(thl2, length)
 * haCloseSmooth = 2 * thaClose - ta.tema(thaClose, length)
 * <p>
 * shortCandle = math.abs(close - open) < ((high - low) * candleSizeFactor)
 * <p>
 * keepn1 = (haClose >= haOpen) and (haClose[1] >= haOpen[1]) or (close >= haClose) or (high > high[1]) or (low >
 * low[1]) or (hl2Smooth >= haCloseSmooth)
 * keepall1 = keepn1 or keepn1[1] and (close >= open) or (close >= close[1])
 * keep13 = shortCandle and (high >= low[1])
 * keepn2 = (haClose < haOpen) and (haClose[1] < haOpen[1]) or (hl2Smooth < haCloseSmooth)
 * keep23 = shortCandle and (low <= high[1])
 * keepall2 = keepn2 or keepn2[1] and (close < open) or (close < close[1])
 * <p>
 * dtr = keepall2 or keepall2[1] and keep23
 * utr = keepall1 or keepall1[1] and keep13
 * upw = not dtr and dtr[1] and utr
 * dnw = not utr and utr[1] and dtr
 * var bool upwWithOffset = false
 * upwWithOffset := upw != dnw ? upw : upwWithOffset[1]
 * <p>
 * buySig = upw or not dnw and upwWithOffset
 * ltSellSig = close < ta.ema(close, emaLength)
 * var bool neutralSig = false
 * neutralSig := buySig or not ltSellSig and neutralSig[1]
 * <p>
 * hacolt = buySig ? 1 : neutralSig ? 0 : -1
 * <p>
 * plot(hacolt, style = plot.style_columns, color = hacolt > 0 ? color.green : hacolt < 0 ? color.red : color.blue,
 * title = "HACOLT")
 */
public class HACOLTIndicator extends CachedIndicator<Num> {
    private final Indicator<Boolean> buySig;
    private final Indicator<Boolean> neutralSig;
    private final int unstableBars;
    private final NumFactory numFactory;

    private final int barCount;
    private final int barCountEma;

    private final OpenPriceIndicator open;
    private final HighPriceIndicator high;
    private final LowPriceIndicator low;
    private final ClosePriceIndicator close;
    private final MedianPriceIndicator hl2;
    private final OHLC4PriceIndicator ohlc4;
    private final HeikinAshiOpenIndicator haOpen;
    private final HeikinAshiCloseIndicator haClose;

    private final Indicator<Num> haCloseSmooth;
    private final Indicator<Num> hl2Smooth;

    private final BooleanCombineIndicator shortCandle;

    private final LogicIndicator keepn1;
    private final LogicIndicator keepall1;
    private final LogicIndicator keep13;
    private final LogicIndicator keepn2;
    private final LogicIndicator keep23;
    private final LogicIndicator keepall2;
    private final LogicIndicator utr;
    private final LogicIndicator dtr;
    private final LogicIndicator upw;
    private final LogicIndicator dnw;
    private final UpwWithOffset upwWithOffset;

    private final BooleanCombineIndicator ltSellSig;

    public HACOLTIndicator(BarSeries series, int barCount, int barCountEma, double candleSizeFactor) {
        super(series);

        this.barCount = barCount;
        this.barCountEma = barCountEma;

        numFactory = series.numFactory();

        this.open = new OpenPriceIndicator(series);
        this.high = new HighPriceIndicator(series);
        this.low = new LowPriceIndicator(series);
        this.close = new ClosePriceIndicator(series);
        this.hl2 = new MedianPriceIndicator(series);
        this.ohlc4 = new OHLC4PriceIndicator(series);
        this.haOpen = new HeikinAshiOpenIndicator(ohlc4);
        this.haClose = new HeikinAshiCloseIndicator(high, low, ohlc4, haOpen);

        PreviousValueIndicator highPrev = new PreviousValueIndicator(high);
        PreviousValueIndicator lowPrev = new PreviousValueIndicator(low);
        PreviousValueIndicator closePrev = new PreviousValueIndicator(close);
        PreviousValueIndicator haOpenPrev = new PreviousValueIndicator(haOpen);
        PreviousValueIndicator haClosePrev = new PreviousValueIndicator(haClose);

        PineCompatTripleEMAIndicator thl2 = new PineCompatTripleEMAIndicator(hl2, barCount);
        PineCompatTripleEMAIndicator thaClose = new PineCompatTripleEMAIndicator(haClose, barCount);
        haCloseSmooth = BinaryOperationIndicator.difference(BinaryOperationIndicator.product(thaClose, 2),
                new PineCompatTripleEMAIndicator(thaClose, barCount));
        hl2Smooth = BinaryOperationIndicator.difference(BinaryOperationIndicator.product(thl2, 2),
                new PineCompatTripleEMAIndicator(thl2, barCount));

        shortCandle = BooleanCombineIndicator.isLessThan(UnaryOperationIndicator.abs(new RealBodyIndicator(series)),
                BinaryOperationIndicator.product(new RealRangeIndicator(series), candleSizeFactor));

        BooleanCombineIndicator haGreen = BooleanCombineIndicator.isGreaterThanOrEqual(haClose, haOpen);
        BooleanCombineIndicator haPrevGreen = BooleanCombineIndicator.isGreaterThanOrEqual(haClosePrev, haOpenPrev);
        BooleanCombineIndicator closeGreaterHaClose = BooleanCombineIndicator.isGreaterThanOrEqual(close, haClose);
        BooleanCombineIndicator highRising = BooleanCombineIndicator.isGreaterThan(high, highPrev);
        BooleanCombineIndicator lowRising = BooleanCombineIndicator.isGreaterThan(low, lowPrev);
        BooleanCombineIndicator hl2SmoothGreaterHaCloseSmooth = BooleanCombineIndicator.isGreaterThanOrEqual(hl2Smooth,
                haCloseSmooth);
        keepn1 = LogicIndicator.or(LogicIndicator.and(haGreen, haPrevGreen), closeGreaterHaClose, highRising, lowRising,
                hl2SmoothGreaterHaCloseSmooth);

        BooleanCombineIndicator green = BooleanCombineIndicator.isGreaterThanOrEqual(close, open);
        BooleanCombineIndicator closeRising = BooleanCombineIndicator.isGreaterThanOrEqual(close, closePrev);
        // PineScript: keepall1 = keepn1 or keepn1[1] and (close >= open) or (close >= close[1])
        // "and" binds tighter → keepn1 OR (keepn1[1] AND green) OR closeRising
        keepall1 = LogicIndicator.or(keepn1,
                LogicIndicator.and(new PreviousBooleanValueIndicator(keepn1), green),
                closeRising);

        BooleanCombineIndicator highGreaterPrevLow = BooleanCombineIndicator.isGreaterThanOrEqual(high, lowPrev);
        keep13 = LogicIndicator.and(shortCandle, highGreaterPrevLow);

        BooleanCombineIndicator haRed = BooleanCombineIndicator.isLessThan(haClose, haOpen);
        BooleanCombineIndicator haPrevRed = BooleanCombineIndicator.isLessThan(haClosePrev, haOpenPrev);
        BooleanCombineIndicator hl2SmoothLessHaCloseSmooth = BooleanCombineIndicator.isLessThan(hl2Smooth,
                haCloseSmooth);
        // PineScript: keepn2 = (haClose < haOpen) and (haClose[1] < haOpen[1]) or (hl2Smooth < haCloseSmooth)
        // "and" binds tighter than "or" → (haRed AND haPrevRed) OR hl2SmoothLess
        keepn2 = LogicIndicator.or(LogicIndicator.and(haRed, haPrevRed), hl2SmoothLessHaCloseSmooth);
        keep23 = LogicIndicator.and(shortCandle, BooleanCombineIndicator.isLessThanOrEqual(low, highPrev));

        BooleanCombineIndicator red = BooleanCombineIndicator.isLessThan(close, open);
        BooleanCombineIndicator closeFalling = BooleanCombineIndicator.isLessThan(close, closePrev);
        // PineScript: keepall2 = keepn2 or keepn2[1] and (close < open) or (close < close[1])
        // "and" binds tighter → keepn2 OR (keepn2[1] AND red) OR closeFalling
        keepall2 = LogicIndicator.or(keepn2,
                LogicIndicator.and(new PreviousBooleanValueIndicator(keepn2), red),
                closeFalling);

        utr = LogicIndicator.or(keepall1, LogicIndicator.and(new PreviousBooleanValueIndicator(keepall1), keep13));
        dtr = LogicIndicator.or(keepall2, LogicIndicator.and(new PreviousBooleanValueIndicator(keepall2), keep23));
        upw = LogicIndicator.and(LogicIndicator.not(dtr), new PreviousBooleanValueIndicator(dtr), utr);
        dnw = LogicIndicator.and(LogicIndicator.not(utr), new PreviousBooleanValueIndicator(utr), dtr);
        upwWithOffset = new UpwWithOffset(upw, dnw);

        buySig = LogicIndicator.or(upw, LogicIndicator.and(LogicIndicator.not(dnw), upwWithOffset));
        ltSellSig = BooleanCombineIndicator.isLessThan(close, new EMAIndicator(close, barCountEma));
        neutralSig = new NeutralSig(this.buySig, ltSellSig);
        unstableBars = Math.max(barCount, barCountEma);
    }

    @Override
    protected Num calculate(int index) {
        if (buySig.getValue(index)) {
            return numFactory.one();
        }
        if (neutralSig.getValue(index)) {
            return numFactory.zero();
        }
        return numFactory.minusOne();
    }

    @Override
    public int getCountOfUnstableBars() {
        return unstableBars;
    }

    public static class HeikinAshiOpenIndicator extends CachedIndicator<Num> {
        private final OHLC4PriceIndicator ohlc4;

        public HeikinAshiOpenIndicator(OHLC4PriceIndicator ohlc4) {
            super(ohlc4);

            this.ohlc4 = ohlc4;
        }

        @Override
        protected Num calculate(int index) {
            if (index == 0) {
                return ohlc4.getValue(index);
            } else {
                return getValue(index - 1).plus(ohlc4.getValue(index)).dividedBy(
                        ohlc4.getBarSeries().numFactory().numOf(2));
            }
        }

        @Override
        public int getCountOfUnstableBars() {
            return 0;
        }
    }

    public static class HeikinAshiCloseIndicator extends CachedIndicator<Num> {
        private final HighPriceIndicator high;
        private final LowPriceIndicator low;
        private final OHLC4PriceIndicator ohlc4;
        private final HeikinAshiOpenIndicator haOpen;

        public HeikinAshiCloseIndicator(HighPriceIndicator high, LowPriceIndicator low, OHLC4PriceIndicator ohlc4,
                                        HeikinAshiOpenIndicator haOpen) {
            super(high);

            this.high = high;
            this.low = low;
            this.ohlc4 = ohlc4;
            this.haOpen = haOpen;
        }

        @Override
        protected Num calculate(int index) {
            return haOpen.getValue(index).plus(high.getValue(index).max(haOpen.getValue(index))).plus(
                    low.getValue(index).min(haOpen.getValue(index))).plus(ohlc4.getValue(index)).dividedBy(
                    haOpen.getBarSeries().numFactory().numOf(4));
        }

        @Override
        public int getCountOfUnstableBars() {
            return 0;
        }
    }

    private static class UpwWithOffset extends CachedIndicator<Boolean> {
        private final Indicator<Boolean> upw;
        private final Indicator<Boolean> dnw;

        private UpwWithOffset(Indicator<Boolean> upw, Indicator<Boolean> dnw) {
            super(upw);

            this.upw = upw;
            this.dnw = dnw;
        }

        @Override
        protected Boolean calculate(int index) {
            boolean prevValue = false;

            if (index > 0) {
                prevValue = getValue(index - 1);
            }

            final boolean upwValue = upw.getValue(index);
            final boolean dnwValue = dnw.getValue(index);
            if (upwValue != dnwValue) {
                return upwValue;
            }
            return prevValue;
        }

        @Override
        public int getCountOfUnstableBars() {
            return 0;
        }
    }

    private static class NeutralSig extends CachedIndicator<Boolean> {
        private final Indicator<Boolean> buySig;
        private final Indicator<Boolean> ltSellSig;

        public NeutralSig(Indicator<Boolean> buySig, Indicator<Boolean> ltSellSig) {
            super(buySig);

            this.buySig = buySig;
            this.ltSellSig = ltSellSig;
        }

        @Override
        protected Boolean calculate(int index) {
            boolean prevValue = false;

            if (index > 0) {
                prevValue = getValue(index - 1);
            }

            final boolean buySigValue = buySig.getValue(index);
            final boolean ltSellSigValue = ltSellSig.getValue(index);

            return buySigValue || (!ltSellSigValue && prevValue);
        }

        @Override
        public int getCountOfUnstableBars() {
            return 0;
        }
    }

    /**
     * PineScript-compatible TripleEMAIndicator that computes from bar 0, exactly like PineScript's {@code ta.tema()}.
     *
     * <p>In ta4j &ge; 0.22.2 the stock {@link TripleEMAIndicator} introduced an unstable-bar guard of
     * {@code indicator.getCountOfUnstableBars() + barCount * 3}, which returns {@code NaN} for the first
     * {@code 3 * barCount} bars. This changes the early-bar history fed into the recursive {@code UpwWithOffset}
     * state machine and produces wrong results at later indices compared to TradingView.
     *
     * <p>By overriding {@link #getCountOfUnstableBars()} to return {@code 0} the NaN guard is disabled:
     * the inherited {@code calculate()} falls through to the actual formula for every index, and the inner
     * {@link EMAIndicator} instances still seed naturally at their own warm-up point (first non-NaN source bar),
     * which exactly mirrors PineScript behaviour.
     */
    private static final class PineCompatTripleEMAIndicator extends TripleEMAIndicator {

        private PineCompatTripleEMAIndicator(Indicator<Num> indicator, int barCount) {
            super(indicator, barCount);
        }

        @Override
        public int getCountOfUnstableBars() {
            return 0;
        }
    }
}
