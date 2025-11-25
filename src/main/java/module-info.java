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

  opens bob.cloverville to javafx.fxml;
//  opens bob.cloverville.JsonStorage to com.google.gson;
  exports bob.cloverville;
}