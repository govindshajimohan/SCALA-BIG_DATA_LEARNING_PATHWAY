import java.io.{BufferedWriter, FileWriter}
import sttp.client3._

object AlphaVantageStock {
  def main(args: Array[String]): Unit = {
    val stockSymbol = "AAPL" // Change to any stock symbol
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

  def saveToFile(filename: String, data: String): Unit = {
    val writer = new BufferedWriter(new FileWriter(filename))
    writer.write(data)
    writer.close()
  }
}
