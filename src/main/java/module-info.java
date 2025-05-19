module uet.oop.space_invaders.spaceinvaders {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.media;

    opens uet.oop.space_invaders.spaceinvaders to javafx.fxml;
    exports uet.oop.space_invaders.spaceinvaders;
}