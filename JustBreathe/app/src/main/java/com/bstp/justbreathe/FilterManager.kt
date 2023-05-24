package com.bstp.justbreathe

import android.content.Context
import com.github.psambit9791.jdsp.filter.Butterworth
import com.github.psambit9791.jdsp.signal.peaks.FindPeak
import com.github.psambit9791.jdsp.signal.peaks.Peak
import java.lang.Math.abs

class FilterManager {
    companion object {
        val windowAverageValues = mutableListOf<Float>()

        fun windowAverage(values: MutableList<Float>, startPosition: Int): Float // Ritorna sempre un array lungo [values.size - windowSize + 1]
        {
            val rangeArray = values.slice(startPosition until values.size)
            return rangeArray.average().toFloat()
        }
    }

    /* Variabili per il filtraggio */
    /**************************************************/
    private val Fs = 50 //Sampling Frequency in Hz
    private val order = 5 //Order of the filter
    private val cutOff = 0.25 //Cut-off Frequency
    private val flt = Butterworth(Fs.toDouble()) //signal is of type double[]

    fun filterData(): DoubleArray? // Può ritornare null se qualcosa non va
    {
        var signalFiltered: DoubleArray? = null
        if (windowAverageValues.size <= 0) {
            clearWindowAverageValues()
            return signalFiltered // Se ritorna null, allora la funzione che l'ha chiamata capisce che non ci sono dati
        }                         // a sufficienza per calcolare la windowAverage, quindi è stato premuto troppo presto
                                  // il tasto stop.
        /**************************************************/
        /* Filtraggio */
        /**************************************************/
        val windowAverageValuesDouble: MutableList<Double> = windowAverageValues.map { it.toDouble() }.toMutableList<Double>()

        signalFiltered = flt.lowPassFilter(windowAverageValuesDouble.toDoubleArray(), order, cutOff)

        clearWindowAverageValues()
        return (signalFiltered)
    }

    private fun computeHoldInterval(samples: DoubleArray, start: Int, end: Int, hold_period: Int, hold_tollerance: Int): Int {
        val derivate = 0.00001
        var countFlatInterval = 0
        var rappIncr: Double
        for (i in start..end) // volevi mettere until ?
        {
            if (i == (end - 1))
                break
            rappIncr = abs(samples[i] - samples[i + 1])
            if (rappIncr <= derivate)
                countFlatInterval++
        }
        val holdTime = countFlatInterval * 20
        //file.appendText("\nholdTime: " + holdTime)
        if (abs(holdTime - hold_tollerance) < hold_period)
            return 1
        else
            return 0
    }

    fun scoreBreathing(samples: DoubleArray?, pattern: String, context : Context): Float
    {
        if (samples == null)
            return -1f

        val peakTolerance = 0.015 //0.0
        //val avoid_whiteNoise = 0.0
        var breath_period = 0
        val breath_period_tolerance = 1000 //0
        var hold_period = 0
        var hold_tollerance = 0

        if (pattern.compareTo(context.getString(R.string.PATTERN_404))== 0) { //we are in 4-0-4
            //peak_tolerance = 0.015 //0.005
            breath_period = 4000
            //breath_period_tolerance = 500
        } else if (pattern.compareTo(context.getString(R.string.PATTERN_478)) == 0) { //we are in 4-7-8
           // peak_tolerance = 0.015 //0.005
            breath_period = 12000
            //breath_period_tolerance = 750
            hold_period = 7000
            hold_tollerance = 1000
        } else if (pattern.compareTo(context.getString(R.string.PATTERN_444)) == 0) {  //we are in 4-4-4
            //peak_tolerance = 0.015 //0.005
            breath_period = 8000 //to check if correct
            //breath_period_tolerance = 500
            hold_period = 4000
            hold_tollerance = 500
        }

        val fp = FindPeak(samples)
        val out : Peak?
        try {
            out = fp.detectPeaks()
        }
        catch (e : Exception)
        {
            return -1f
        }
        val max_peaks = out.peaks
        //val max_peaks = out.filterByHeight(avoid_whiteNoise, 100.0); //To filter peaks by height
        val out2 : Peak?
        try {
            out2 = fp.detectTroughs()
        }
        catch (e : Exception)
        {
            return -1f
        }
        val min_peaks = out2.peaks
       // val min_peaks = out2.filterByHeight(-100.0, -avoid_whiteNoise); //probabili bug

        var sum_max = 0.0
        for (ind in max_peaks) {
            sum_max = sum_max + samples[ind]
        }

        var sum_min = 0.0
        for (ind in min_peaks) {
            sum_min = sum_min + samples[ind]
        }

        val mean_max_peaks: Double = sum_max / max_peaks.size
        val mean_min_peaks: Double = sum_min / min_peaks.size

        var count_max = 0
        for (ind in max_peaks) {
            if (abs(samples[ind] - mean_max_peaks) < peakTolerance)
                count_max = count_max + 1
        }

        var count_min = 0
        for (ind in min_peaks) {
            if (abs(samples[ind] - mean_min_peaks) < peakTolerance)
                count_min = count_min + 1
        }

        val quality_max_peaks = count_max.toDouble() / max_peaks.size
        val quality_min_peaks = count_min.toDouble() / min_peaks.size

        val all_peaks = max_peaks.plus(min_peaks)
        all_peaks.sort()

        var first = 0
        var second = 1

        val diff_intervals = IntArray(all_peaks.size - 1)
        var i = 0
        while (second < all_peaks.size) {
            diff_intervals[i] = (all_peaks[second] - all_peaks[first]) * 20
            first = first + 1
            second = second + 1
            i = i + 1
        }

        var count = 0
        for (interval in diff_intervals) {
            if (abs(interval - breath_period) <= breath_period_tolerance) {
                count = count + 1
            }
        }

        val qualityIntervals = count.toDouble() / diff_intervals.size

        var quality_hold = 0.0
        if ((pattern.compareTo(context.getString(R.string.PATTERN_444)) == 0)
            or
            (pattern.compareTo(context.getString(R.string.PATTERN_478)) == 0)) {
            var count_goodHoldInterval = 0
            var all_holdInterval = 0
            for (index in 0..all_peaks.size) {
                if (index == (all_peaks.size - 1))
                    break
                    //Check on avoid_whiteNoise deleted. To be improved, possible approache is the evaluation of absolute of avoid_whiteNoise
                    count_goodHoldInterval += computeHoldInterval(
                        samples,
                        all_peaks[index],
                        all_peaks[index + 1],
                        hold_period,
                        hold_tollerance
                    )
                    all_holdInterval = all_holdInterval + 1
            }
            //To Solve the hold_pattern problem is going check the all_holdInterval value
            quality_hold = if(all_holdInterval == 0) 0.0 else count_goodHoldInterval.toDouble() / all_holdInterval
        }

        val score : Double =
            if (pattern.compareTo(context.getString(R.string.PATTERN_404)) == 0)
            //Tuning of the paramaters balanced
                0.25 * quality_max_peaks + 0.25 * quality_min_peaks + 0.50 * qualityIntervals
            else
                0.20 * quality_max_peaks + 0.20 * quality_min_peaks + 0.20 * quality_hold + 0.40 * qualityIntervals

        return score.toFloat() * 100
    }

    private fun clearWindowAverageValues() {
        windowAverageValues.clear()
    }
}