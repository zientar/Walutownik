/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package walutownik;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;



/**
 *
 * @author Łukasz Zientarski
 */
public class Dane {
    
    //Wczytanie listy plików XML ze strony NBP
    public static Map Daty() {
        try {
            Map<String, String> lista = new HashMap<>();
            URL con = new URL("http://www.nbp.pl/kursy/xml/dir.txt");
            Scanner tmp = new Scanner(con.openStream());
            
            while(tmp.hasNext()){
                
                String nPliku = tmp.next();
               
                if(nPliku.charAt(0) == 'a')
                {
                    String data = nPliku.substring(5, 11);
                    lista.put(data, nPliku);
                }
            }
            
            return lista;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Dane.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Dane.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    //Zbudowanie URLa do pobrania pliku XML
    public static URL UrlGenerator(String wData, Map<String, String> lista) throws MalformedURLException {
         String tmp = "http://www.nbp.pl/kursy/xml/";
         
       if(lista.get(wData) != null){
           tmp += lista.get(wData) + ".xml";
           URL con = new URL(tmp);
           return con;
         }
       else 
           return null;
    }
    //Wczytnie Kursów walut ze strony NBP o podanej dacie
    public static TabelaKursow DaneNBPXml(URL url) {
            JAXBContext ctx;
         try {
             
             ctx = JAXBContext.newInstance(TabelaKursow.class);
             Unmarshaller u = ctx.createUnmarshaller();
             TabelaKursow t1 = (TabelaKursow) u.unmarshal(url);
             
             t1.getPozycja().stream().forEach((tabela) -> {
                 tabela.setKursSredni(tabela.getKursSredni().replace(',', '.'));
                });
             
             return t1;
             
         } catch (JAXBException ex) {
             Logger.getLogger(Dane.class.getName()).log(Level.SEVERE, null, ex);
             return null;
         }
}
    //Przeliczanie
    public static String PrzeliczWalute(String ilosc, String kurs1, String kurs2, String waluta, String przelicznik) {
        if(waluta == "PLN") {
            BigDecimal p = new BigDecimal((Double.parseDouble(ilosc))/Double.parseDouble(kurs1)/Double.parseDouble(kurs2)/Double.parseDouble(przelicznik));
            p = p.setScale(4, RoundingMode.HALF_UP);
            return p.toString();
        }
        else {
            BigDecimal p = new BigDecimal((Double.parseDouble(ilosc))*Double.parseDouble(kurs1)/Double.parseDouble(kurs2)*Double.parseDouble(przelicznik));
            p = p.setScale(4, RoundingMode.HALF_UP);
            return p.toString();
        }
        
    }
    
    public static String ZwrocKurs(TabelaKursow t, String waluta) {
        if(waluta.equals("PLN")) {
            return "1";
        }
        for(TabelaKursow.Pozycja t2 : t.pozycja) {
            if(t2.kodWaluty.equals(waluta)) {
                return t2.kursSredni;
            }
        }
        return null;
     
    }
    
    public static String ZwrocPrzelicznik(TabelaKursow t, String waluta) {
        if(waluta.equals("PLN")) {
            return "1";
        }
        for(TabelaKursow.Pozycja t2 : t.pozycja) {
            if(t2.kodWaluty.equals(waluta)) {
                return "" + t2.przelicznik;
            }
        }
        return null;
     
    }

    
}
