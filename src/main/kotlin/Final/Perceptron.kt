package Final

import kotlin.math.sqrt
import kotlin.time.measureTime

enum class Type { DIGIT, FACE }
class Perceptron {
    fun dotProduct(a: List<Double>, b: List<Int>): Double = a.zip(b).sumOf { (i, j) -> i * j }
    infix fun MutableList<Double>.plus(b: List<Int>) = this.zip(b).map { (a, b) -> a + b }.toMutableList()
    infix fun MutableList<Double>.minus(b: List<Int>) = this.zip(b).map { (a, b) -> a - b }.toMutableList()

    fun weightCalculate(features: String, labels: String, points: Int, type: Type): List<List<Double>> {
        val weights =
            Array(if (type == Type.DIGIT) 10 else 2) { DoubleArray(if (type == Type.DIGIT) 532 else 4200) { 0.0 }.toMutableList() }.toMutableList()

        val featureLabels = if (type == Type.DIGIT) FeatureExtraction().featureLabelExtractDigit(
            features,
            labels,
            points
        ) else FeatureExtraction().featureLabelExtractFace(features, labels, points)
        for ((label, feature) in featureLabels) {
            if (weights.indices.maxBy { dotProduct(weights[it], feature) } != label) {
                for (i in weights.indices) {
                    if (i == label) weights[i] = weights[i] plus feature
                    else weights[i] = weights[i] minus feature
                }
            }
        }
        return weights.toList()
    }

    fun calculate(weights: List<List<Double>>, feature: List<Int>) =
        weights.indices.maxBy { dotProduct(weights[it], feature) }

    // returns pair of model train time and model accuracy
    fun performanceTesting(
        trainFeatures: String,
        trainLabels: String,
        trainPoints: Int,
        testFeatures: String,
        testLabels: String,
        testPoints: Int,
        type: Type
    ): Pair<Long, Double> {
        val weights: List<List<Double>>
        val modelTime = measureTime { weights = weightCalculate(trainFeatures, trainLabels, trainPoints, type) }
        //println("Model took $modelTime to calculate with $trainPoints many points")

        val testing = if (type == Type.DIGIT) FeatureExtraction().featureLabelExtractDigit(
            testFeatures,
            testLabels,
            testPoints
        ) else FeatureExtraction().featureLabelExtractFace(testFeatures, testLabels, testPoints)
        var acc = 0

        testing.forEach { (label, feature) ->
            if (label == calculate(weights, feature)) acc++
        }
        val accuracy = acc / testPoints.toDouble() * 100
        //println("Accuracy of model: $accuracy%")
        return Pair(modelTime.inWholeMilliseconds, accuracy)
    }
}

fun main() {
    println("Perceptron Digit Calculations")
    for (i in 1..10) {
        val results = mutableListOf<Pair<Long, Double>>()
        for (j in 0..<10) {
            results.add(
                Perceptron().performanceTesting(
                    "src/main/Final Data/DigitData/trainingimages",
                    "src/main/Final Data/DigitData/traininglabels",
                    i*(5000/10), "src/main/Final Data/DigitData/testimages",
                    "C:\\Users\\Marco Hu\\IdeaProjects\\CS440\\src\\main\\Final Data\\DigitData\\testlabels",
                    100,
                    Type.DIGIT
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

    println("Perceptron Face Calculations")
    for (i in 1..10) {
        val results = mutableListOf<Pair<Long, Double>>()
        for (j in 0..<10) {
            results.add(
                Perceptron().performanceTesting(
                    "src/main/Final Data/FaceData/facedatatrain",
                    "src/main/Final Data/FaceData/facedatatrainlabels",
                    i * (451 / 10), "src/main/Final Data/FaceData/facedatatest",
                    "src/main/Final Data/FaceData/facedatatestlabels",
                    150,
                    Type.FACE
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