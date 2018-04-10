package com.drice.scrapingjunk.view;

import com.drice.scrapingjunk.listener.ScrapeControllerListener;
import com.drice.scrapingjunk.model.LoginCredentials;
import com.drice.scrapingjunk.model.ScrapeInfoAndInput;
import com.drice.scrapingjunk.model.CSVInputParam;
import com.drice.scrapingjunk.scrapercontroller.*;
import com.drice.scrapingjunk.util.Constants;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    ComboBox<ScrapeInfoAndInput> comboBox;

    @FXML
    ScrapeInfoAndInput emailClientByEntityIdOption;

    @FXML
    HBox emailTemplateHBox;

    @FXML
    ComboBox<String> emailTemplateTypeComboBox;

    @FXML
    VBox customScrapeInfoAndInputsVBox;

    @FXML
    TextField customLoginUrlTextField;

    @FXML
    TextField customElemSelTextField;

    @FXML
    TextField customPrefixUrlTextField;

    @FXML
    TextField customSuffixUrlTextField;

    @FXML
    HBox customSearchbarSelHBox;

    @FXML
    TextField customSearchbarSelTextField;

    @FXML
    TextField customSearchButtonSelTextField;

    @FXML
    private TextArea messageDisplayArea;

    @FXML
    private TextField urlTextFieldDisplay;

    @FXML
    private TextField progressTextField;

    @FXML
    private TextField totalToScrapeTextField;

    private String emailButtonSelector = "input[onclick=submitForm(this.form, 'TEMPLATENUMBER')]";
    private String templateNumberPlaceholder = "TEMPLATENUMBER";

    //This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        comboBox.setCellFactory(new Callback<ListView<ScrapeInfoAndInput>, ListCell<ScrapeInfoAndInput>>() {
            public ListCell<ScrapeInfoAndInput> call(ListView<ScrapeInfoAndInput> p) {
                return new ListCell<ScrapeInfoAndInput>() {

                    @Override protected void updateItem(ScrapeInfoAndInput item, boolean empty) {
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
        comboBox.valueProperty().addListener(new ChangeListener<ScrapeInfoAndInput>() {
            public void changed(ObservableValue<? extends ScrapeInfoAndInput> observable,
                                ScrapeInfoAndInput oldValue, ScrapeInfoAndInput newValue) {
                //System.out.println("changed comboBox " + newValue.getScrapeType());
                if(oldValue != null && oldValue.getScrapeType().equals(Constants.EMAIL_CLIENT_BY_ENTITY_ID) &&
                        !newValue.getScrapeType().equals(Constants.EMAIL_CLIENT_BY_ENTITY_ID)) {
                    emailTemplateHBox.setDisable(true);
                } else if(newValue.getScrapeType().equals(Constants.EMAIL_CLIENT_BY_ENTITY_ID)) {
                    if(newValue.getEmailTemplateNumber() != null) {
                        //System.out.println("email template number now " + newValue.getEmailTemplateNumber());
                    }
                    emailTemplateHBox.setDisable(false);
                }

                if(oldValue != null && oldValue.getScrapeType().contains(Constants.CUSTOM)
                        && !newValue.getScrapeType().contains(Constants.CUSTOM)) {
                    customScrapeInfoAndInputsVBox.setDisable(true);
                } else if(newValue.getScrapeType().contains(Constants.CUSTOM)) {
                    customScrapeInfoAndInputsVBox.setDisable(false);
                    if(newValue.getScrapeType().equals(Constants.CUSTOM_SEARCHBAR_PARAM)) {
                        customSearchbarSelHBox.setDisable(false);
                    } else {
                        customSearchbarSelHBox.setDisable(true);
                    }
                }
            }
        });

        emailTemplateTypeComboBox.setEditable(false);
        emailTemplateTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                //System.out.println("changed emailTemplateTypeComboBox " + oldValue + " | " + newValue);
                emailClientByEntityIdOption.setEmailTemplateNumber(newValue);
                String emailButtonSelector4Template = emailButtonSelector;
                emailButtonSelector4Template = emailButtonSelector4Template.replace(templateNumberPlaceholder, newValue);
                emailClientByEntityIdOption.setWebElementSelector(emailButtonSelector4Template);
            }
        });
    }

    @FXML protected void handleScrapeCSVButtonAction(ActionEvent event) {
        ScrapeInfoAndInput scrapeInfo = comboBox.getValue();
        if(scrapeInfo != null) {
            String scrapeSelected = scrapeInfo.getScrapeType();
            if (checkScrapeOkForSelection(scrapeSelected)) {
                LoginCredentials loginCredentials = new LoginCredentials(user_name.getText(), passwordField.getText());
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose CSV");
                Node source = (Node) event.getSource();
                Window stage = source.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    final String path = file.getAbsolutePath();
                    List<CSVInputParam> urlParamList = getUrlParamListFromCSV(path, "", null);
                    if (urlParamList != null && !urlParamList.isEmpty()) {
                        BaseScrapeController scrapeController;
                        if (scrapeSelected.equals(Constants.ENTITY_ID_TO_PHONE)) {
                            scrapeController = new EntityIdToContactInfoScraper();
                        } else if (scrapeSelected.equals(Constants.ATTORNEY_GENERAL)) {
                            scrapeController = new AttorneyGeneralScrapeController();
                        } else if (scrapeSelected.equals(Constants.ENTITY_ID_TO_PHONE_NUMBER_PLUS_REASSIGN_BUTTON)) {
                            scrapeController = new EntityIdToContactInfoWReassignScraper();
                        } else if (scrapeSelected.equals(Constants.PHONE_NUMBER_TO_REASSIGN_BUTTON)) {
                            scrapeController = new PhoneNumberToReassignUrl();
                        } else if (scrapeSelected.equals(Constants.EMAIL_CLIENT_BY_ENTITY_ID)) {
                            scrapeController = new EmailClientByEntityIdScrapController();
                        } else if (scrapeSelected.equals(Constants.CUSTOM_URL_PARAM)) {
                            scrapeController = new UrlWithParamScrapeController();
                            //If custom, then we need to set its scrapeInfoAndInput with input from textfields
                            setCustomScrapeInfoValues(scrapeInfo);
                        } else if (scrapeSelected.equals(Constants.CUSTOM_SEARCHBAR_PARAM)){
                            scrapeController = new SearchBarParamScrapeController();
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
                                            if (result != null && result.size() > 0) {
                                                fileWriter = new FileWriter("phone_numbers.csv");
                                                for (Object o : result) {
                                                    fileWriter.append(o.toString() + "\n");
                                                }
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
            }
        } else {
            String alertMessage = "Please select a scrape type";
            createBasicAlert("Select Scrape", alertMessage, Alert.AlertType.NONE);
        }
    }

    private boolean isNullOrBlank(String text) {
        return text == null || text.equals("");
    }

    public List<CSVInputParam> getUrlParamListFromCSV(String filePath, String paramName, String regex) {
        List<CSVInputParam> urlParamList = null;
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(filePath));

            String [] nextLine;
            nextLine = reader.readNext();
            if(nextLine != null) {
                urlParamList = new ArrayList<CSVInputParam>();
                //String paramName = nextLine[0];
                int curLineNumber = 0;
                while ((nextLine = reader.readNext()) != null) {
                    String nextParam = nextLine[0];
                    System.out.println(nextParam);
                    if(regex == null || Pattern.matches(regex, nextParam)) {
                        urlParamList.add(new CSVInputParam(paramName, nextLine[0]));
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

    public void setCustomScrapeInfoValues(ScrapeInfoAndInput scrapeInfoValues) {
        scrapeInfoValues.setLoginUrl(customLoginUrlTextField.getText().trim());
        scrapeInfoValues.setWebElementSelector(customElemSelTextField.getText().trim());
        scrapeInfoValues.setTargetUrlPrefix(customPrefixUrlTextField.getText().trim());
        scrapeInfoValues.setTargetUrlSuffix(customSuffixUrlTextField.getText().trim());
        scrapeInfoValues.setSearchBarSelector(customSearchbarSelTextField.getText().trim());
        scrapeInfoValues.setSearchButtonSelector(customSearchButtonSelTextField.getText().trim());
    }

    public boolean checkScrapeOkForSelection(String scrapeSelected) {
        boolean scrapeIsOk = true;
        if(!scrapeSelected.equals(Constants.ATTORNEY_GENERAL) &&
                (isNullOrBlank(user_name.getText()) || isNullOrBlank(passwordField.getText()))) {
            scrapeIsOk = false;
            String alertMessage = "Please enter username and password";
            createBasicAlert("Enter Login", alertMessage, Alert.AlertType.NONE);
        } else if(scrapeSelected.equals(Constants.EMAIL_CLIENT_BY_ENTITY_ID) &&
                isNullOrBlank(emailClientByEntityIdOption.getEmailTemplateNumber())) {
            scrapeIsOk = false;
            String alertMessage = "Cannot Email Client by Entity Id scrape without selecting email template number";
            createBasicAlert("Select Email Template Number", alertMessage, Alert.AlertType.NONE);
        }
        return scrapeIsOk;
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
