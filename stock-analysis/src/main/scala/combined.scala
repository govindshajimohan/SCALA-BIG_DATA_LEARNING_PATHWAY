import scala.io.StdIn.readLine
import scala.io.Source
import java.io.{BufferedWriter, FileWriter}
import sttp.client3._

object StockDataHandler {
  def main(args: Array[String]): Unit = {
    handleUserChoice() // Call the function to start processing
  }

  def handleUserChoice(): Unit = {
    println("Choose an option: \n1. Download stock data \n2. Process existing CSV file \n3. Exit")
    val choice = readLine().trim

    choice match {
      case "1" => 
        downloadStockData()
        handleUserChoice() // Ask the user again after execution
      case "2" => 
        processCSV("stock_data.csv")
        handleUserChoice() // Ask the user again after execution
      case "3" => 
        println("Exiting program. Goodbye!")
      case _ => 
        println("Invalid choice. Please enter 1, 2, or 3.")
        handleUserChoice() // Retry on invalid input
    }
  }


  def downloadStockData(): Unit = {
    print("Enter stock symbol (e.g., AAPL, TSLA): ")
    val stockSymbol = readLine().trim.toUpperCase
    val apiKey = "1ZPZV0XNY8N8OIV8" // Replace with your API key
    val url = s"https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=$stockSymbol&interval=5min&apikey=$apiKey&datatype=csv"

    val backend = HttpURLConnectionBackend()
    val request = basicRequest.get(uri"$url")
    val response = request.send(backend)

    response.body match {
      case Right(data) =>
        println(s"Stock Data for $stockSymbol fetched successfully!")
        saveToFile("stock_data.csv", data)
        println("Data saved to stock_data.csv")
      case Left(error) =>
        println(s"Error fetching stock data: $error")
    }
  }

  def processCSV(filename: String): Unit = {
    try {
      val bufferedSource = Source.fromFile(filename)
      val writer = new BufferedWriter(new FileWriter("processed_stock_data.csv", true)) // Append mode
      
      for (line <- bufferedSource.getLines().drop(1)) { // Skipping header
        val cols = line.split(",").map(_.trim)
        val modifiedCols = cols.map(_.replace(".", "-"))
        //println(s"Timestamp: ${modifiedCols(0)}, Open Price: ${modifiedCols(1)}, Close Price: ${modifiedCols(4)}")
        writer.write(modifiedCols.mkString(",") + "\n")
      }
      bufferedSource.close()
      writer.close()
      println("------------------------------------------------")
      println("Processed data saved to processed_stock_data.csv")
      println("------------------------------------------------")
    } catch {
      case _: Exception => println("Error processing the CSV file. Make sure 'stock_data.csv' exists.")
    }
  }

  def saveToFile(filename: String, data: String): Unit = {
    val writer = new BufferedWriter(new FileWriter(filename))
    writer.write(data)
    writer.close()
  }
} 
