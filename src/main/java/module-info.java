module bob.cloverville {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;

  requires com.google.gson;
  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires eu.hansolo.tilesfx;


  opens bob.cloverville to com.google.gson, javafx.fxml;
  opens bob.cloverville.controllers to javafx.fxml;
  exports bob.cloverville;
  exports bob.cloverville.controllers;
}