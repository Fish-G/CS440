package Final

import kotlin.math.log
import kotlin.math.sqrt
import kotlin.time.measureTime

class naiveBayes {
    fun digitModel(features: String, labels: String, points: Int): List<List<Double>> {
        //process features into buckets of their labels to then use to calculate p(f_i|Y)
        val totals = Array(10) { DoubleArray(532) { 1.0 } }
        val counts = Array(10) { 1 }
        FeatureExtraction().featureLabelExtractDigit(features, labels, points).forEach { (label, featureVector) ->
            counts[label]++
            featureVector.forEachIndexed { index, i -> if (i != 0) totals[label][index]++ }
        }
        return totals.zip(counts).map { (t, c) -> t.map { it / c }.toList() }.toList()
    }

    fun digitCalculate(feature: List<Int>, model: List<List<Double>>, prior: List<Double>): List<Double> {
        val p = List(10) { 0.0 }.toMutableList()

        for (i in 0..<10) {
            p[i] =
                prior[i] * model[i].zip(feature).map { (modelP, feature) -> if (feature == 0) 1 - modelP else modelP }
                    .reduce { acc, d -> acc * d }
        }

        val sum = p.sum()
        return p.map { it / sum }.toList()
    }

    fun performanceTestingDigit(
        trainFeatures: String,
        trainLabels: String,
        trainPoints: Int,
        testFeatures: String,
        testLabels: String,
        testPoints: Int
    ): Pair<Long, Double> {
        val model: List<List<Double>>
        val modelTime = measureTime { model = naiveBayes().digitModel(trainFeatures, trainLabels, trainPoints) }
        //println("Model took $modelTime to calculate with $trainPoints many points")

        val prior = listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1)

        val testing = FeatureExtraction().featureLabelExtractDigit(testFeatures, testLabels, testPoints)
        var acc = 0

        testing.forEach { (label, feature) ->
            val distribution = digitCalculate(feature, model, prior)//.also { println(it) }
            if (label == distribution.indices.maxBy { distribution[it] }) acc++
        }
        val accuracy = acc / testPoints.toDouble() * 100
        //println("Accuracy of model: $accuracy%")
        return Pair(modelTime.inWholeMilliseconds, accuracy)
    }

    fun faceModel(features: String, labels: String, points: Int): List<List<Double>> {
        val totals = Array(2) { DoubleArray(4200) { 1.0 } }
        val counts = Array(2) { 1 }

        FeatureExtraction().featureLabelExtractFace(features, labels, points).forEach { (label, featureVector) ->
            counts[label]++
            featureVector.forEachIndexed { index, i -> if (i != 0) totals[label][index]++ }
        }
        return totals.zip(counts).map { (t, c) -> t.map { it / c }.toList() }.toList()
    }

    fun faceCalculate(feature: List<Int>, model: List<List<Double>>, prior: List<Double>): List<Double> {
        val p = List(2) { 0.0 }.toMutableList()

        for (i in 0..<2) {
            p[i] =
                prior[i] * model[i].zip(feature).map { (modelP, feature) -> if (feature == 0) 1 - modelP else modelP }
                    .sumOf { log(it, 10.0) } // my probabilities are falling to zero
        }

        val sum = p.sum()//.also { println("sum $it") }
        return p.map { it / sum }.toList()
    }

    fun performanceTestingFace(
        trainFeatures: String,
        trainLabels: String,
        trainPoints: Int,
        testFeatures: String,
        testLabels: String,
        testPoints: Int
    ): Pair<Long, Double> {
        val model: List<List<Double>>
        val modelTime = measureTime { model = naiveBayes().faceModel(trainFeatures, trainLabels, trainPoints) }
        //println("Model took $modelTime to calculate with $trainPoints many points")

        val prior = listOf(0.5, 0.5)

        val testing = FeatureExtraction().featureLabelExtractFace(testFeatures, testLabels, testPoints)
        var acc = 0

        testing.forEach { (label, feature) ->
            val distribution = faceCalculate(feature, model, prior)//.also { println(it) }
            if (label == distribution.indices.minBy { distribution[it] }) acc++
        }
        val accuracy = acc / testPoints.toDouble() * 100
        //println("Accuracy of model: $accuracy%")
        return Pair(modelTime.inWholeMilliseconds, accuracy)
    }
}

fun main() {
    println("Naive Bayes Digit Calculations")
    for (i in 1..10) {
        val results = mutableListOf<Pair<Long, Double>>()
        for (j in 0..<10) {
            results.add(
                naiveBayes().performanceTestingDigit(
                    "src/main/Final Data/DigitData/trainingimages",
                    "src/main/Final Data/DigitData/traininglabels",
                    i*(5000/10), "src/main/Final Data/DigitData/testimages",
                    "C:\\Users\\Marco Hu\\IdeaProjects\\CS440\\src\\main\\Final Data\\DigitData\\testlabels",
                    100
                )/*.also {println("with ${i*10}% data: model took ${it.first}ms, and accuracy ${it.second}%")}*/
            )
        }
        val averageTime = results.sumOf { it.first } / results.size
        val averageAcc = results.sumOf { it.second } / results.size
        val sigmaTime = sqrt((results.map { it.first }.sumOf { (it - averageTime) * (it - averageTime) }
            .toDouble() / (results.size - 1)))
        val sigmaAcc =
            sqrt(results.map { it.second }.sumOf { (it - averageAcc) * (it - averageAcc) } / (results.size - 1))
        println("with ${i * 10}% data: model took average of ${averageTime}ms, with standard deviation ${sigmaTime}ms\n average accuracy of $averageAcc% and standard deviation $sigmaAcc%")

    }

    println("Naive Bayes Face Calculations")
    for (i in 1..10) {
        val results = mutableListOf<Pair<Long, Double>>()
        for (j in 0..<10) {
            results.add(
                naiveBayes().performanceTestingFace(
                    "src/main/Final Data/FaceData/facedatatrain",
                    "src/main/Final Data/FaceData/facedatatrainlabels",
                    i * (451 / 10), "src/main/Final Data/FaceData/facedatatest",
                    "src/main/Final Data/FaceData/facedatatestlabels",
                    150,
                )/*.also {println("with ${i*10}% data: model took ${it.first}ms, and accuracy ${it.second}%")}*/
            )
        }
        val averageTime = results.sumOf { it.first } / results.size
        val averageAcc = results.sumOf { it.second } / results.size
        val sigmaTime = sqrt((results.map { it.first }.sumOf { (it - averageTime) * (it - averageTime) }
            .toDouble() / (results.size - 1)))
        val sigmaAcc =
            sqrt(results.map { it.second }.sumOf { (it - averageAcc) * (it - averageAcc) } / (results.size - 1))
        println("with ${i * 10}% data: model took average of ${averageTime}ms, with standard deviation ${sigmaTime}ms\n average accuracy of $averageAcc% and standard deviation $sigmaAcc%")

    }
}