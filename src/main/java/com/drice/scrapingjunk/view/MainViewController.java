package com.drice.scrapingjunk.view;

import com.drice.scrapingjunk.listener.ScrapeControllerListener;
import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfo;
import com.drice.scrapingjunk.model.UrlParam;
import com.drice.scrapingjunk.scrapercontroller.*;
import com.drice.scrapingjunk.util.ScrapeTask;
import com.opencsv.CSVReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by DrIce on 8/26/17.
 */
public class MainViewController implements Initializable, ScrapeControllerListener {

    ScrapeTask currentScrapeTask;

    @FXML
    private TextField user_name;

    @FXML
    private TextField passwordField;

    @FXML
    ComboBox<ScrapeInfo> comboBox;

    @FXML
    private TextArea messageDisplayArea;

    @FXML
    private TextField urlTextFieldDisplay;

    @FXML
    private TextField progressTextField;

    @FXML
    private TextField totalToScrapeTextField;

    //This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        /*
        final ObservableList<ScrapeInfo> scanOptions =
                FXCollections.observableArrayList(
                        new ScrapeInfo("blah", "", "", "", "")
                );
        comboBox = new ComboBox<ScrapeInfo>(scanOptions);
        */
        comboBox.setCellFactory(new Callback<ListView<ScrapeInfo>, ListCell<ScrapeInfo>>() {
            public ListCell<ScrapeInfo> call(ListView<ScrapeInfo> p) {
                return new ListCell<ScrapeInfo>() {

                    @Override protected void updateItem(ScrapeInfo item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            //setGraphic(null);
                        } else {
                            setText(item.getScrapeType());
                            setAccessibleText(item.getScrapeType());
                            System.out.println(this.getText());
                        }
                    }
                };
            }
        });

        comboBox.setEditable(false);
        comboBox.valueProperty().addListener(new ChangeListener<ScrapeInfo>() {
            public void changed(ObservableValue<? extends ScrapeInfo> observable,
                                ScrapeInfo oldValue, ScrapeInfo newValue) {

            }
        });
    }

    @FXML protected void handleScrapeCSVButtonAction(ActionEvent event) {
        ScrapeInfo scrapeInfo = comboBox.getValue();
        if(scrapeInfo != null) {
            String scrapeSelected = scrapeInfo.getScrapeType();
            if (scrapeSelected.equals("Attorney General") ||
                    (!isNullOrBlank(user_name.getText()) && !isNullOrBlank(passwordField.getText()))) {
                LoginCredentials loginCredentials = new LoginCredentials(user_name.getText(), passwordField.getText());
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose CSV");
                Node source = (Node) event.getSource();
                Window stage = source.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    final String path = file.getAbsolutePath();
                    List<UrlParam> urlParamList = getUrlParamListFromCSV(path, "", null);
                    if (urlParamList != null && !urlParamList.isEmpty()) {
                        BaseScrapeController scrapeController;
                        if (scrapeSelected.equals("EntityID to Phone Number")) {
                            scrapeController = new EntityIdToContactInfoScraper();
                        } else if (scrapeSelected.equals("Attorney General")) {
                            scrapeController = new AttorneyGeneralScrapeController();
                        } else if (scrapeSelected.equals("EntityID to Phone Number Plus Reassign Button")){
                            scrapeController = new EntityIdToContactInfoWReassignScraper();
                        } else {
                            scrapeController = new AttorneyGeneralScrapeController();
                        }
                        scrapeController.setScanControllerListener(this);

                        boolean headlessBrowser = false;

                        currentScrapeTask = new ScrapeTask(scrapeController, scrapeInfo, loginCredentials,
                                urlParamList, headlessBrowser);
                        currentScrapeTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                                new EventHandler<WorkerStateEvent>() {
                                    public void handle(WorkerStateEvent t) {
                                        List<Object> result = currentScrapeTask.getValue();
                                        FileWriter fileWriter = null;
                                        try {
                                            fileWriter = new FileWriter("phone_numbers.csv");
                                            for (Object o : result) {
                                                fileWriter.append(o.toString() + "\n");
                                            }
                                            //fileWriter.append("\n");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            try {
                                                fileWriter.flush();
                                                fileWriter.close();
                                            } catch (IOException ie) {
                                                ie.printStackTrace();
                                            }
                                        }

                                    }
                                });
                        currentScrapeTask.start();
                    }
                }
            } else {
                String alertMessage = "Please enter username and password";
                createBasicAlert("Enter Login", alertMessage, Alert.AlertType.NONE);
            }
        } else {
            String alertMessage = "Please select a scrape type";
            createBasicAlert("Select Scrape", alertMessage, Alert.AlertType.NONE);
        }
    }

    private boolean isNullOrBlank(String text) {
        return text == null || text.equals("");
    }

    public List<UrlParam> getUrlParamListFromCSV(String filePath, String paramName, String regex) {
        List<UrlParam> urlParamList = null;
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(filePath));

            String [] nextLine;
            nextLine = reader.readNext();
            if(nextLine != null) {
                urlParamList = new ArrayList<UrlParam>();
                //String paramName = nextLine[0];
                int curLineNumber = 0;
                while ((nextLine = reader.readNext()) != null) {
                    String nextParam = nextLine[0];
                    System.out.println(nextParam);
                    if(regex == null || Pattern.matches(regex, nextParam)) {
                        urlParamList.add(new UrlParam(paramName, nextLine[0]));
                    } else {
                        //param doesn't match regex
                        String title = "Select CSV";
                        String alertMessage = "Line " + curLineNumber + " - " + nextParam
                                + " - does not match with desired pattern";
                        createBasicAlert(title, alertMessage, Alert.AlertType.NONE);
                        return null;
                    }
                    curLineNumber++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        if(urlParamList != null && urlParamList.isEmpty()) {
            String title = "Select CSV";
            String alertMessage = "Contents of csv file are empty";
            createBasicAlert(title, alertMessage, Alert.AlertType.NONE);
        }
        return urlParamList;
    }


    public void createBasicAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        String s = message;
        alert.setContentText(s);
        if (alertType.equals(Alert.AlertType.NONE)) {
            alert.getButtonTypes().add(new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE));
            alert.show();
        }
    }

    public void sendMessage(String message) {
        messageDisplayArea.appendText(message + "\n");
    }

    public void setTotalClientsToScrape(int totalClients) {
        totalToScrapeTextField.setText(String.valueOf(totalClients));
    }


    public void updateNumberClientsScraped(int clientsScraped) {
        progressTextField.setText(String.valueOf(clientsScraped));
    }

    public void onScanFinished() {

    }
}
