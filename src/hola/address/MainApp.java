/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hola.address;

import hola.address.model.Person;
import hola.address.model.PersonListWrapper;
import hola.address.view.PersonEditDialogController;
import hola.address.view.PersonOverviewController;
import hola.address.view.RootLayoutController;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author duccuong
 */
public class MainApp extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    
    /**
     * Sample data
     */
    private ObservableList<Person> personData = FXCollections.observableArrayList();

    public MainApp() {
        // Add some sample data
        personData.add(new Person("Cương", "Đào Đức"));
        personData.add(new Person("Tiến", "Lê Anh"));
        personData.add(new Person("Tuấn", "Hoàng Minh"));
    }
    
    /**
     * Returns the data as an observable list of Persons
     * @return
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }
    
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressFX");
        
        initRootLayout();
        showPersonOverview();
        
    }
    
    /**
     * Initialized the root layout
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane)loader.load();
            
            // Show the scene containing the root layout
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            
            // Give the controller access to mainApp
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            
            primaryStage.show();
        } catch (IOException e) {
        }
        
        // Try to load last opened file.
        File file = getPersonFilePath();
//        if (file != null)
//            loadPersonDataFromFile(file);
    }
    
    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane)loader.load();
            
            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
            
            // Give the controller access to the main app.
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the main stage.
     * @return 
     */
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public boolean showPersonEditDialog(Person person) {
        try {
            // Load the fxml file and create a new stage for popup dialog
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane)loader.load();
            
            // Create the dialog Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the person into the controller.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);
            
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            
            return controller.isOkClicked();
        } catch(IOException e) {
            return false;
        }
    }
    
    /**
     * Returns the person file preferences, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     * 
     * @return 
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null)
            return new File(filePath);
        else
            return null;
    }
    
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
            
            // Update the stage title.
            primaryStage.setTitle("AddressFX - " + file.getName());
        } else {
            prefs.remove("filePath");
            
            // Update the stage title.
            primaryStage.setTitle("AddressFX");
        }
    }
    
    /**
     * Loads person data from the specified file. The current person data will be replaced.
     * 
     * @param file 
     */
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();
            
            // Read XML from the file and unmarshalling.
            PersonListWrapper wrapper = (PersonListWrapper)um.unmarshal(file);
            
            personData.clear();
            personData.addAll(wrapper.getPersons());
            
            // save the file path to the registry
            setPersonFilePath(file);
        } catch (Exception e) {
            //
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());
            
            alert.showAndWait();
        }
    }
    
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            // Wrap person data.
            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);
            
            // Marshalling and vaing XML to the file.
            m.marshal(wrapper, file);
            
            // save the file path to the registry
            setPersonFilePath(file);
        } catch (Exception e) {
             //
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());
            
            alert.showAndWait();
        }
    }
}
