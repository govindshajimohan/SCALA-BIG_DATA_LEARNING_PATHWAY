import scala.io.Source
import java.io.{BufferedWriter, FileWriter}

object StockDataProcessor {
  def main(args: Array[String]): Unit = {
    val filename = "stock_data.csv"
    val bufferedSource = Source.fromFile(filename)

    // Open the output file once
    val writer = new BufferedWriter(new FileWriter("processed1_stock_data.csv", true)) // Append mode

    try {
      for (line <- bufferedSource.getLines().drop(1)) { // Skipping header
        val cols = line.split(",").map(_.trim)
        
        // Replace `.` with `-` in each column
        val modifiedCols = cols.map(_.replace(".", "-"))

        // Print the modified data
        println(s"Timestamp: ${modifiedCols(0)}, Open Price: ${modifiedCols(1)}, Close Price: ${modifiedCols(4)}")

        // Save the modified data to the output file
        val outputLine = modifiedCols.mkString(",") + "\n" // Convert array to a CSV line
        writer.write(outputLine)
      }
    } finally {
      // Ensure resources are closed
      bufferedSource.close()
      writer.close()
    }
  }
}