import java.io._

import breeze.linalg._

class LinearRegression(n_iterations: Int, learning_rate: Double) {
  var w: DenseVector[Double] = DenseVector[Double]()

  def mse(y_true: DenseVector[Double], y_pred: DenseVector[Double]): Double = {
    val sq_diff = (y_pred - y_true) * (y_pred - y_true)
    sum(sq_diff) / y_true.length
  }

  def fit(X: DenseMatrix[Double], y: DenseVector[Double]): Unit = {
    this.w = DenseVector.zeros[Double](X.cols)
    for (_ <- 1 to this.n_iterations) {
      val preds = this.predict(X)
      val grad = 2.0 * X.t * (preds - y) / (y.length : Double)
      this.w = this.w - this.learning_rate * grad
    }
  }

  def predict(X: DenseMatrix[Double]): DenseVector[Double] = {
    X * this.w
  }
}

class TrainTestSplit(test_size: Double) {
  def split(data: DenseMatrix[Double]):
  (DenseMatrix[Double], DenseVector[Double], DenseMatrix[Double], DenseVector[Double]) = {
    val bound = scala.math.ceil(data.rows * test_size).toInt
    val X_train = data(0 until bound, 0 until data.cols - 1)
    val y_train = data(0 until bound, data.cols - 1)
    val X_val = data(bound until data.rows, 0 until data.cols - 1)
    val y_val = data(bound until data.rows, data.cols - 1)
    (X_train, y_train, X_val, y_val)
  }
}

object main {
  def main(args: Array[String]): Unit = {
    val input_train_file = new File(args(0))
    val input_test_file = new File(args(1))
    val output_file = new File(args(2))
    val train_data = csvread(input_train_file, ',')
    val test_data = csvread(input_test_file, ',')
    val score_file = new PrintWriter("score.txt")

    val splitter = new TrainTestSplit(0.8)
    val (x_train, y_train, x_val, y_val) = splitter.split(train_data)

    val model = new LinearRegression(10000, 0.001)
    model.fit(x_train, y_train)

    val train_mse = model.mse(y_train, model.predict(x_train))
    val val_mse = model.mse(y_val, model.predict(x_val))
    score_file.write("Train_mse = " + train_mse + "\n")
    score_file.write("Validation_mse = " + val_mse + "\n")
    score_file.close()

    val y_pred = model.predict(test_data)
    csvwrite(output_file, y_pred.toDenseMatrix.t)
  }
}