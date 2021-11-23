import org.apache.spark.sql._
import org.apache.spark.sql.functions._


object tfidf {
  def main(args: Array[String]): Unit = {
    // Создает сессию спарка
    val spark = SparkSession.builder()
      // адрес мастера
      .master("local[*]")
      // имя приложения в интерфейсе спарка
      .appName("homework")
      // взять текущий или создать новый
      .getOrCreate()

    // синтаксический сахар для удобной работы со спарк
    import spark.implicits._

    val data_path: String = "data/tripadvisor_hotel_reviews.csv"
    val max_count_words: Int = 100
    val result_path: String = "data/result"

    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(data_path)

    val df_lower = df
      .withColumn("Review_lower", lower(col("Review")))
    val df_lower_cleared = df_lower
      .withColumn("Review_lower_clear",
        split(functions.regexp_replace(col("Review_lower"), "[^a-z0-9 ]", ""), " "))
    val df_prepared = df_lower_cleared.withColumn("index", monotonically_increasing_id())

    val words = df_prepared
      .select(col("index"), explode(split(col("Review_lower"), " "))
        .alias("word"))
      .where(length($"word") > 1)

    val tf = words.groupBy("index", "word")
      .agg(count("word").alias("tf"))

    val docf = words
      .groupBy("word")
      .agg(countDistinct("index").alias("df"))
      .orderBy(desc("df"))
      .limit(max_count_words)

    def inverse_doc_freq(docs_count: Long, doc_freq: Long): Double ={
      math.log((docs_count.toDouble + 1)/(doc_freq.toDouble + 1))
    }
    val docs_count = df_prepared.count()
    val inv_df = udf { df: Long => inverse_doc_freq(docs_count, df) }
    val idf = docf
      .withColumn("idf", inv_df(col("df")))

    val tfidf = tf.join(idf, "word")
      .withColumn("tfidf", col("tf") * col("idf"))
      .select(col("word"), col("index"), col("tfidf"))

    val result = tfidf.groupBy("index")
      .pivot("word")
      .sum("tfidf")
      .orderBy("index")

    result.show()

    result
      .coalesce(1)
      .write
      .option("header", "true")
      .option("sep", ",")
      .csv(result_path)
  }
}