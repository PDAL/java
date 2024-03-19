package com.azavea;

import io.pdal.*;

class MainJava {
  // to check laz "filename":"data/autzen_trim.laz"
  static String json = "{\"pipeline\":[{\"filename\":\"data/1.2-with-color.las\",\"spatialreference\":\"EPSG:2993\"},{\"type\":\"filters.reprojection\",\"out_srs\":\"EPSG:3857\"}]}";

  public static void main(String[] args) {
    var pipeline = new Pipeline(json, LogLevel.Error());
    pipeline.execute();
    System.out.println("pipeline.getMetadata():" + pipeline.getMetadata());
    pipeline.close();
  }
}
