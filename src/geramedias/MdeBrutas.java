/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geramedias;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author rbraga
 */
public class MdeBrutas {
    Utils util;

    ArrayList<DdeBrutas> dadosBrutos;

    public MdeBrutas() {
        util = new Utils();
        dadosBrutos = new ArrayList();
    }

    public DdeBrutas get(String med) {
        for (DdeBrutas este : dadosBrutos) {
            if (este.tag.equals(med)) {
                return este;
            }
        }
        DdeBrutas este = new DdeBrutas(med);
        dadosBrutos.add(este);
        return este;
    }
    public ObservableList<String> getLista(){
        ObservableList<String> lista = FXCollections.observableArrayList();
        for( DdeBrutas este :dadosBrutos){
            lista.add(este.tag);
        }
        return lista;
    }
    public boolean lerBrutas(String arqLeit) {
        dadosBrutos = new ArrayList();
        boolean leu = false;
        Charset charset = Charset.forName("UTF8");
        Path file = Paths.get(arqLeit);
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                processaNovo(line);
            }
            leu = true;
        } catch (IOException x) {
            System.out.println("Não abriu arquivo enercol");
        }
        return leu;
    }
    void processaNovo(String linha) {
        /*
         1- datco DATE               <= data de coleta das leituras
         2- tag   character(15)      <= identifica o medidor
         3- pulFora int              <= pulsos fora da ponta
         4- pulPonta int		    <= pulsos na ponta
         */
        String[] campos = linha.split(",");
        if( campos.length<4)
            return;
        String dia = campos[0];
        String tag = campos[1];
        String fora = campos[2];
        String ponta = campos[3];
        DdeBrutas este = get(tag);
        LocalDate esteDia = util.leDia(dia);
        este.addDia(esteDia, Integer.parseInt(fora), Integer.parseInt(ponta));
    }
    public boolean salvaBrutas(String arqLeit) {
        Charset charset = Charset.forName("UTF8");
        Path file = Paths.get(arqLeit);
        boolean salvou = false;
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            for (DdeBrutas esteMed : dadosBrutos) {
                for( LocalDate estaData = esteMed.menorDataIncorporada; !estaData.isAfter(esteMed.maiorDataIncorporada);estaData=estaData.plusDays(1))
                    writer.write(esteMed.publish(estaData));
            }
            salvou = true;
        } catch (IOException x) {
            System.out.println("Não salvou arquivo enercol");
        }
        return salvou;
    }
}
