package lin_reg
import breeze.linalg._
import java.io._
import scala.util.Random
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets


object model {
  def main(args: Array[String]) = {

    val data_train_file: String = args(0) // house_train.csv
    val data_inference_file: String = args(1) // house_inference.csv
    val prediction_store_file: String = args(2) // house_prediction.csv
    val validation_score_file: String = args(3) // validation_score.csv

    // read
    println("Data reading (see sklearn.datasets fetch_california_housing)...")
    val data_train = breeze.linalg.csvread(new File(data_train_file),',')
    println(f"Shape of train data = ${data_train.rows} x ${data_train.cols}")
    val data_inf = breeze.linalg.csvread(new File(data_inference_file),',')
    println(f"Shape of inference data = ${data_inf.rows} x ${data_inf.cols}")

    // shuffle indexes
    println("Indexes shuffling...")
    val index = (0 to (data_train.rows - 1)).toArray
    val index_shuffle = Random.shuffle(index)
    println(f"First 10 index elements after shuffle: ${index_shuffle.take(10)}")
    val train_size = (data_train.rows * 0.7).toInt
    val train_index = index_shuffle.take(train_size)
    val test_index = index_shuffle.drop(train_size)
    println(f"Train size = ${train_index.length}")
    println(f"Test size = ${test_index.length}")


    // prepare x_train data based on train_index
    println("Datasets preparation...")
    val builder_x = new CSCMatrix.Builder[Double](rows=train_index.length, cols=data_train.cols-1)
    for (i <- 0 to (train_index.length - 1)) {
      for (j <- 0 to (data_train.cols - 2)) {
        builder_x.add(i, j, data_train(train_index(i), j))
      }
    }
    val x_train = builder_x.result().toDenseMatrix

    // prepare y_train data based on train_index
    val builder_y = new CSCMatrix.Builder[Double](rows=train_index.length, cols=1)
    for (i <- 0 to (train_index.length - 1)) {
      builder_y.add(i, 0, data_train(train_index(i), 8))
      }
    val y_train = builder_y.result().toDenseMatrix

    // prepare x_test data based on test_index
    val builder_x_test = new CSCMatrix.Builder[Double](rows=test_index.length, cols=data_train.cols-1)
    for (i <- 0 to (test_index.length - 1)) {
      for (j <- 0 to (data_train.cols - 2)) {
        builder_x_test.add(i, j, data_train(test_index(i), j))
      }
    }
    val x_test = builder_x_test.result().toDenseMatrix

    // prepare y_test data based on train_index
    val builder_y_test = new CSCMatrix.Builder[Double](rows=test_index.length, cols=1)
    for (i <- 0 to (test_index.length - 1)) {
      builder_y_test.add(i, 0, data_train(test_index(i), 8))
    }
    val y_test = builder_y_test.result().toDenseMatrix
    println(f"x_train = ${x_train.rows} x ${x_train.cols}")
    println(f"y_train = ${y_train.rows} x ${y_train.cols}")
    println(f"x_test = ${x_test.rows} x ${x_test.cols}")
    println(f"y_test = ${y_test.rows} x ${y_test.cols}")

    // model
    println("Train model...")
    val b = inv(x_train.t * x_train) * x_train.t * y_train
    println(f"get weights: ${b}")

    // get score
    val y_hat_train = x_train * b
    val y_hat_test = x_test * b
    val mse_train = sum((y_train - y_hat_train) *:* (y_train - y_hat_train)) / y_train.rows
    val mse_test = sum((y_test - y_hat_test) *:* (y_test - y_hat_test)) / y_test.rows
    val message = f"MSE train = ${mse_train}, MSE test = ${mse_test}"
    println(message)
    Files.write(Paths.get(validation_score_file), message.getBytes(StandardCharsets.UTF_8))

    // inference
    val y_hat_inference = data_inf * b
    breeze.linalg.csvwrite(new File(prediction_store_file), y_hat_inference, ',')

  }
}
