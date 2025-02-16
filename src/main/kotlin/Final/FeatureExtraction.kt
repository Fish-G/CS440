package Final

import java.io.File

class FeatureExtraction {
    // for digits, features are 19x29 with some blank lines between
    fun featureLabelExtractDigit(features: String, labels: String, points:Int): List<Pair<Int, List<Int>>> {
        val f = File(features).bufferedReader()
        val l = File(labels).inputStream()

        val labels = mutableListOf<Int>()
        l.bufferedReader().forEachLine {labels.add(it.toInt())}

        val features = mutableListOf<List<Int>>()

        var line: String? = f.readLine()
        while (line != null) {
            val feature = mutableListOf<Int>()
            if ("#" in line  || "+" in line) {
                for (i in 0..<19) {
                    line!!.map {if (it == ' ') 0 else 1}.forEach { feature.add(it) }
                    line = f.readLine()
                }
                features.add(feature.toList())
            }

            line = f.readLine()
        }

        return labels.zip(features).map { (label, feature) -> Pair(label,feature)}.toList().shuffled().subList(0,points)
    }

    fun featureLabelExtractFace(features:String, labels:String,points:Int): List<Pair<Int,List<Int>>> {
        //these are 70x60
        val f = File(features).bufferedReader()
        val l = File(labels).inputStream()

        val labels = mutableListOf<Int>()
        l.bufferedReader().forEachLine { labels.add(it.toInt()) }

        val features = mutableListOf<List<Int>>()


        out@ while (true) {
            val feature = mutableListOf<Int>()
            for (i in 0..<70) {
                val line= f.readLine() ?: break@out
                line.map {if (it == ' ')0 else 1}.forEach { feature.add(it) }
            }
            features.add(feature.toList())
        }

        return labels.zip(features).map {(label,feature)-> Pair(label,feature)}.toList().shuffled().subList(0,points)
    }

}
fun main() {
    val v = FeatureExtraction().featureLabelExtractDigit("src/main/Final Data/DigitData/trainingimages","src/main/Final Data/DigitData/traininglabels",5000)
    v.forEach { println("l: ${it.first} f: ${it.second}") }
}