import scala.io.StdIn.readLine
import scala.io.Source
import java.io.{BufferedWriter, FileWriter}
import java.sql.{Connection, DriverManager, PreparedStatement, SQLException}
import sttp.client3._

object StockDataHandler1 {
  val dbUrl = "jdbc:postgresql://localhost:5432/stock_data_db" // Update if needed
  val dbUser = "postgres"
  val dbPassword = "bnbn"

  def main(args: Array[String]): Unit = {
    ensureTableExists()
    handleUserChoice() // Start processing
  }

  def handleUserChoice(): Unit = {
    while (true) { // Keep asking user until they exit
      println("\nChoose an option:\n1. Download stock data\n2. Process existing CSV file\n3. Exit")
      readLine().trim match {
        case "1" => downloadStockData()
        case "2" => processCSV("stock_data.csv")
        case "3" =>
          println("Exiting program. Goodbye!")
          sys.exit(0)
        case _ => println("Invalid choice. Please enter 1, 2, or 3.")
      }
    }
  }

  def downloadStockData(): Unit = {
    print("Enter stock symbol (e.g., AAPL, TSLA): ")
    val stockSymbol = readLine().trim.toUpperCase
    val apiKey = "1ZPZV0XNY8N8OIV8" // Replace with your actual API key
    val apiUrl = s"https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=$stockSymbol&interval=5min&apikey=$apiKey&datatype=csv"

    val backend = HttpURLConnectionBackend()
    val request = basicRequest.get(uri"$apiUrl")
    val response = request.send(backend)

    response.body match {
      case Right(data) =>
        println(s"✅ Stock data for $stockSymbol fetched successfully!")
        saveToFile("stock_data.csv", data)
        println("📄 Data saved to stock_data.csv")
      case Left(error) =>
        println(s"❌ Error fetching stock data: $error")
    }
  }

  def processCSV(filename: String): Unit = {
    try {
      val bufferedSource = Source.fromFile(filename)

      for (line <- bufferedSource.getLines().drop(1)) { // Skipping header
        val cols = line.split(",").map(_.trim)
        if (cols.length >= 5) { // Ensure valid data row
          val timestamp = cols(0)
          val openPrice = cols(1)
          val closePrice = cols(4)

          println(s"💾 Saving to database: Timestamp: $timestamp, Open: $openPrice, Close: $closePrice")
          saveToDatabase(timestamp, openPrice, closePrice, "AAPL") // Change symbol if needed
        } else {
          println("⚠️ Skipping invalid row in CSV.")
        }
      }

      bufferedSource.close()
      println("✅ All processed data saved to the database.")

    } catch {
      case _: java.io.FileNotFoundException =>
        println("❌ Error: CSV file not found! Make sure 'stock_data.csv' exists.")
      case e: Exception =>
        println(s"❌ Error processing the CSV file: ${e.getMessage}")
    }
  }

  def saveToDatabase(timestamp: String, openPrice: String, closePrice: String, stockSymbol: String): Unit = {
    var connection: Connection = null
    var preparedStatement: PreparedStatement = null

    try {
      connection = getDBConnection()
      val query = "INSERT INTO stocks (timestamp, open_price, close_price, stock_symbol) VALUES (?, ?, ?, ?)"
      preparedStatement = connection.prepareStatement(query)

      preparedStatement.setString(1, timestamp)
      preparedStatement.setString(2, openPrice)
      preparedStatement.setString(3, closePrice)
      preparedStatement.setString(4, stockSymbol)

      preparedStatement.executeUpdate()
      println(s"✅ Inserted data into database for $timestamp.")

    } catch {
      case e: SQLException if e.getMessage.contains("password authentication failed") =>
        println("❌ Database authentication failed! Check your username/password.")
      case e: Exception =>
        println(s"❌ Error inserting into database: ${e.getMessage}")
    } finally {
      if (preparedStatement != null) preparedStatement.close()
      if (connection != null) connection.close()
    }
  }

  def saveToFile(filename: String, data: String): Unit = {
    val writer = new BufferedWriter(new FileWriter(filename))
    writer.write(data)
    writer.close()
  }

  def getDBConnection(): Connection = {
    try {
      Class.forName("org.postgresql.Driver") // Load the driver
      DriverManager.getConnection(dbUrl, dbUser, dbPassword)
    } catch {
      case e: Exception =>
        println(s"❌ Database connection failed: ${e.getMessage}")
        throw e
    }
  }

  def ensureTableExists(): Unit = {
    var connection: Connection = null
    var statement: PreparedStatement = null

    try {
      connection = getDBConnection()
      val createTableQuery =
        """CREATE TABLE IF NOT EXISTS stocks (
          |  id SERIAL PRIMARY KEY,
          |  timestamp TIMESTAMP NOT NULL,
          |  open_price VARCHAR(50),
          |  close_price VARCHAR(50),
          |  stock_symbol VARCHAR(10)
          |);""".stripMargin

      statement = connection.prepareStatement(createTableQuery)
      statement.executeUpdate()
      println("✅ Verified database table exists.")

    } catch {
      case e: Exception => println(s"❌ Error ensuring table exists: ${e.getMessage}")
    } finally {
      if (statement != null) statement.close()
      if (connection != null) connection.close()
    }
  }
}
