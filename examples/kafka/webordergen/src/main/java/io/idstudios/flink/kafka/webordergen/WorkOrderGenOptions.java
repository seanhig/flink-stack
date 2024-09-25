package io.idstudios.flink.kafka.webordergen;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class WorkOrderGenOptions {

  private boolean isGenerator = false;
  private int orderCount = 30;
  private int batchSizeMax = 10;
  private int pauseSecondMax = 5;
  private int batchPauseSecondMax = 10;

  public static WorkOrderGenOptions getOpts(String[] args) throws ParseException{
    return new WorkOrderGenOptions(args);
  }
  
  public WorkOrderGenOptions(String[] args) throws ParseException{
    Options options = new Options();
    options.addOption("mode", true, "generator or monitor");
    options.addOption("count", true, "number of orders to generate");
    options.addOption("batchsizemax", true, "number of orders to generate in each batch");
    options.addOption("orderpausemax", true, "max seconds to pause between orders");
    options.addOption("batchpausemax", true, "max seconds to pause between batches of orders");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    if(!cmd.hasOption("mode")
      || (!cmd.getOptionValue("mode").startsWith("gen") && !cmd.getOptionValue("mode").startsWith("mon"))) {
      throw new ParseException("The `mode` option is required and must be specified as either `generator` or `monitor`!");
    }

    this.isGenerator = cmd.getOptionValue("mode").toLowerCase().startsWith("gen") ? true: false;
    this.orderCount = cmd.hasOption("count") ? Integer.parseInt(cmd.getOptionValue("count")) : this.orderCount;
    this.batchSizeMax = cmd.hasOption("batchsizemax") ? Integer.parseInt(cmd.getOptionValue("batchsizemax")) : this.batchSizeMax;
    this.pauseSecondMax = cmd.hasOption("orderpausemax") ? Integer.parseInt(cmd.getOptionValue("orderpausemax")) : this.pauseSecondMax;
    this.batchPauseSecondMax = cmd.hasOption("batchpausemax") ? Integer.parseInt(cmd.getOptionValue("batchpausemax")) : this.batchPauseSecondMax;    
  }

  public boolean isGenerator() {
    return this.isGenerator;
  }

  public int getOrderCount() {
    return this.orderCount;
  }

  public int getBatchSizeMax() {
    return this.batchSizeMax;
  }

  public int getOrderPauseMax() {
    return this.pauseSecondMax;
  }

  public int getBatchPauseMax() {
    return this.batchPauseSecondMax;
  }

  public String toString() {
    if(this.isGenerator()) {
      return String.format("Mode: generator, Count: %d, BatchMax: %d, PauseSecMax: %d, BatchPauseSecMax: %d",
      this.getOrderCount(),
      this.getBatchSizeMax(),
      this.getOrderPauseMax(), 
      this.getBatchPauseMax());
    } else {
      return String.format("Mode: monitor");
    }

  }

}
