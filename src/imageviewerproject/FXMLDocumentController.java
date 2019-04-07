package imageviewerproject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    private final List<Image> images = new ArrayList<>();
    private int currentImageIndex = 0;

    @FXML
    Parent root;

    @FXML
    private Button btnLoad;

    @FXML
    private Button btnPrevious;

    @FXML
    private Button btnNext;

    @FXML
    private ImageView imageView;

    private boolean running;

    private ScheduledExecutorService executor;

    int delay;
    @FXML
    private Label fileNameField;

    private void handleBtnLoadAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp", "*.jpeg"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty()) {
            files.forEach((File f)
                    -> {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
        }

    }

    private void handleBtnPreviousAction(ActionEvent event) {
        if (!images.isEmpty()) {
            currentImageIndex
                    = (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
            updateFileName();
        }
    }

    private void handleBtnNextAction(ActionEvent event) {

        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
            updateFileName();
        }
    }

    private void displayImage() {
        if (!images.isEmpty()) {
            imageView.setImage(images.get(currentImageIndex));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        running=false;
        btnLoad.setOnAction((ActionEvent event)
                -> {
            handleBtnLoadAction(event);
        });

        btnPrevious.setOnAction((ActionEvent event)
                -> {
            handleBtnPreviousAction(event);
        });

        btnNext.setOnAction((ActionEvent event)
                -> {
            handleBtnNextAction(event);
        });

        File folder = new File("src\\imageviewerproject\\gui\\images");

        List<File> files = Arrays.asList(folder.listFiles());

        if (!files.isEmpty()) {
            files.forEach((File f)
                    -> {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
            setFileName(images.get(currentImageIndex).impl_getUrl());
        }

    }

    @FXML
    private void startSlideshow(ActionEvent event) {
        start();
    }

    @FXML
    private void stopSlideshow(ActionEvent event) {
        stop();
    }

    public void start() {
        if(!running){
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                btnNext.fire();
            }
        };
        delay = 1;
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(thread, delay, delay, TimeUnit.SECONDS);
        running=true;
        }
    }

    public void updateFileName() {
        Platform.runLater(() -> {
            setFileName(images.get(currentImageIndex).impl_getUrl());
        });
    }

    public void setFileName(String fName) {
        fileNameField.setText("File name: " + fName.substring(fName.lastIndexOf("/") + 1, fName.length()));
    }

    public void stop() {
        running=false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

}
