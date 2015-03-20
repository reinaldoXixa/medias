/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geramedias;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author rbraga
 */
public class GeraMedias extends Application {
    Stage ppal;
    
    
    @Override
    public void start(Stage stage) throws Exception {
        ppal = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(GeraMedias.class.getResource("geraMedias.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        geraMediasController ctl = loader.getController();
        ctl.SetPapai(this);
    }
   @Override
    public void stop() {
        System.exit(0);
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
    public Stage getStage(){
        return ppal;
        
    }
}
