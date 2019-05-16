/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package walutownik;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;


/**
 *
 * @author Łukasz Zientarski
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private DatePicker dataFx;
    @FXML
    private ComboBox<String> waluta1;
    @FXML
    private ComboBox<String> waluta2;
    @FXML
    private TextField iloscWaluty;
    @FXML
    private Button Przelicz;
    @FXML
    private Label kurs;
    @FXML
    private Label wynik;
    @FXML
    private Label numerTabeli;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //inicjalizacja daty
        dataFx.setValue(LocalDate.now());
       
            List<String> lista = new ArrayList<>(Dane.Daty().keySet());

        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
     @Override
     public DateCell call(final DatePicker dataFx) {
         return new DateCell() {
             @Override public void updateItem(LocalDate item, boolean empty) {
                 super.updateItem(item, empty);
                 setDisable(true);

                 
                 for(int i = 0; i<lista.size(); i++) {
                     String tmp = lista.get(i);
                     int rok = 2000 + Integer.parseInt(tmp.substring(0, 2));
                     int mie = Integer.parseInt(tmp.substring(2, 4));
                     int dzi = Integer.parseInt(tmp.substring(4, 6));
                     if(item.equals(LocalDate.of(rok, mie, dzi))) {
                     setDisable(false);
                 }
                 
                 }
                 
             }
         };
     }
 };
        dataFx.setDayCellFactory(dayCellFactory);
              
        try {
            ObservableList<String> opcje = FXCollections.observableArrayList();
            opcje.add("PLN Złoty polski");
            TabelaKursow t = Dane.DaneNBPXml(Dane.UrlGenerator("160509", Dane.Daty()));
            t.getPozycja().stream().forEach((TabelaKursow.Pozycja t2) -> {
                opcje.add(t2.getKodWaluty() + " " + t2.getNazwaWaluty());
            });  
            waluta1.setItems(opcje);
            waluta2.setItems(opcje);
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }    

    @FXML
    private void initialize(KeyEvent event) {
        char klik = event.getCharacter().charAt(0);
        if(!Character.isDigit(klik) && klik != '.') {
            event.consume();
    }
    }


    @FXML
    private void handle(ActionEvent event) throws MalformedURLException {
            
            
            String data = dataFx.getValue().toString();
            String tmp = "" + data.substring(2, 4) + data.substring(5, 7) + data.substring(8, 10);
            if(iloscWaluty.getText() != null && waluta1.getValue() != null && waluta2.getValue() != null)
                 {
            TabelaKursow t = Dane.DaneNBPXml(Dane.UrlGenerator(tmp, Dane.Daty()));
            numerTabeli.setText(t.getNumerTabeli());
            
            if("PLN".equals(waluta1.getValue().substring(0, 3))) {
                kurs.setText(Dane.ZwrocKurs(t, waluta2.getValue().substring(0, 3)));
            }
            else {
                kurs.setText(Dane.ZwrocKurs(t, waluta1.getValue().substring(0, 3)));
            }
            String w;

                w = Dane.PrzeliczWalute(iloscWaluty.getText(), Dane.ZwrocKurs(t, waluta1.getValue().substring(0, 3)), Dane.ZwrocKurs(t, waluta2.getValue().substring(0, 3)), waluta2.getValue().substring(0, 3), Dane.ZwrocPrzelicznik(t, waluta2.getValue().substring(0, 3)));
                wynik.setText(w);
            }
            else {
                wynik.setText("Podaj parametry obliczeń!!!");
            }
             
            
            
    }


   
    
}
