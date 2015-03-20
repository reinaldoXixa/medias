/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geramedias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author rbraga
 */
public class geraMediasController implements Initializable {

    GeraMedias papai;
    MdeMedias mdmed;
    MdeBrutas mdbru;
    Timeline timeline;
    DadosTela tela;
// graficos 
    // variaveis utilizadas no grafico
    int numAmoGr;                   // numero de amostras de pulsos do medidor;
    int numAmoVi;                   // varia de acordo com os botoes encolhe e expande
    int offInicioVi;                // varia de acordo com os botoes >> e <<
    int offFimVi;
    Series<Integer, Integer> enfo;
    Series<Integer, Integer> medfo;
    boolean[] mudfo;

// variaveis setadas quando um medidor é escolhido
    DdeBrutas medidorGr;
    DdeMedias mediaGr;      // suas medias
    LocalDate dtInicioGr;
    LocalDate dtFimGr;
    LocalDate fimGr;
    LocalDate hoje;
    DropShadow ds;
    boolean temSelecao;
    int rangeInf;
    int rangeSup;

// variaveis setadas quando um medidor é escolhido
    @FXML
    private TextField tfLeitura;
    @FXML
    private TextField tfMedias;
    @FXML
    private Label avisos;
    @FXML
    private ComboBox<String> cbMedidores;
    @FXML
    private Label laNumMed;
    @FXML
    private AreaChart<Integer, Integer> bcRawDados;
    @FXML
    private NumberAxis eixoY;
    @FXML
    private NumberAxis eixoX;
    @FXML
    private TabPane tabpane;
    @FXML
    private Label laNumAmo;
    @FXML
    private Label laSelInf;
    @FXML
    private Label laSelSup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ds = new DropShadow();
        try (ObjectInputStream objIStrm = new ObjectInputStream(new FileInputStream("geraMedias.ini"))) {
            tela = (DadosTela) objIStrm.readObject();
        } catch (Exception e) {
            emiteMensagem("Exception during deserialization: " + e);
            tela = new DadosTela();
            tela.ArquivoInput = "leituras.dat";
            tela.ArquivoMedias = "conjuntos.dat";
            tela.ultimaLeitura = LocalDate.of(1945, Month.APRIL, 20);
        }
        tfLeitura.setText(tela.ArquivoInput);
        tfMedias.setText(tela.ArquivoMedias);
        createCursorGraphCoordsMonitorLabel();

    }

    private void createCursorGraphCoordsMonitorLabel() {

        final Node chartBackground = bcRawDados.lookup(".chart-plot-background");
        for (Node n: chartBackground.getParent().getChildrenUnmodifiable()) {
            if (n != chartBackground ) {
                n.setMouseTransparent(true);
            }
        }

        chartBackground.setOnMouseClicked((MouseEvent mouseEvent) -> {
            double pos = (double) eixoX.getValueForDisplay(mouseEvent.getX()+.5);
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                rangeInf = min(rangeInf,(int)pos);
                rangeSup = max(rangeSup,rangeInf);
            } else {
                rangeSup = max(rangeSup,(int)pos);
                rangeInf = min(rangeInf,rangeSup);
            }
            temSelecao = true;
            laSelInf.setText(rangeInf+"");
            laSelSup.setText(rangeSup+"");
        });
    }

    @FXML
    private void handleProcurarLeitura(ActionEvent event) {
        FileChooser filech = new FileChooser();
        filech.setTitle("Arquivo com Leituras");
        filech.getExtensionFilters().add(
                new ExtensionFilter("Dat Files", "*.dat"));
        File fifi = filech.showOpenDialog(papai.getStage());
        tela.ArquivoInput = fifi.getAbsolutePath();
        tfLeitura.setText(tela.ArquivoInput);
    }

    @FXML
    private void handleProcurarMedia(ActionEvent event) {
        FileChooser filech = new FileChooser();
        filech.setTitle("Arquivo com Medias");
        filech.getExtensionFilters().add(
                new ExtensionFilter("Dat Files", "*.dat"));
        File fifi = filech.showSaveDialog(papai.getStage());
        tela.ArquivoMedias = fifi.getAbsolutePath();
        tfMedias.setText(fifi.getAbsolutePath());
    }

    public void salvSerial() {
        try (ObjectOutputStream objOStrm = new ObjectOutputStream(new FileOutputStream("geraMedias.ini"))) {
            objOStrm.writeObject(tela);
        } catch (IOException e) {
            System.out.println("Exception during serialization: " + e);
        }
    }

    public void SetPapai(GeraMedias paps) {
        papai = paps;
        mdmed = new MdeMedias();
        //mdmed.lerMedias(tela.ArquivoMedias);
        mdbru = new MdeBrutas();
        hoje = LocalDate.now();
    }

    @FXML
    private void handleProcessar(ActionEvent event) {
        if (mdmed.numMedias == 0) {
            mdmed.lerMedias(tela.ArquivoMedias);
        }
        if (!mdbru.lerBrutas(tela.ArquivoInput)) {
            emiteMensagem("Não leu dados");
        }
        laNumMed.setText(mdbru.getLista().size() + "");
        cbMedidores.setItems(mdbru.getLista());
        tabpane.getSelectionModel().select(1);
        salvSerial();
        criaChart();
    }
    private void criaChart(){
        enfo = new Series<>();
        enfo.setName("pulsos");
        medfo = new Series<>();
        medfo.setName("médias");
        for (int i = 0; i < 500; ++i) {
            enfo.getData().add(new XYChart.Data<>(i,0,0));
        }
        for (int i = 0; i < 500; ++i) {
            medfo.getData().add(new XYChart.Data<>(i,0));
        }
        mudfo = new boolean[500];
        ObservableList<XYChart.Series<Integer, Integer>> answer = FXCollections.observableArrayList();
        answer.addAll(medfo, enfo);
        eixoX.setLabel("médias");
        eixoX.setAutoRanging(false);
        eixoY.setLabel("pulsos");
        eixoY.setAutoRanging(true);
        bcRawDados.setAnimated(false);
        bcRawDados.setData(answer);
    }

    /**
     * emite texto na linha de mensagens
     *
     * @param texto
     */
    public void emiteMensagem(String texto) {
        avisos.setText(texto);
        timeline = runLater(Duration.millis(5000),
                () -> {
                    this.clearMensagem();
                });

    }

    public void clearMensagem() {
        avisos.setText("");
        timeline.stop();
    }

    private Timeline runLater(Duration delay, Runnable action) {
        Timeline tline = new Timeline(new KeyFrame(delay, ae -> action.run()));
        tline.play();
        return tline;
    }

    @FXML
    private void handleSelMedidor(ActionEvent event) {
        medidorGr = mdbru.get(cbMedidores.getValue());
        mediaGr = mdmed.get(medidorGr.tag);
        medidorGr.setMedia(mediaGr);
        dtInicioGr = medidorGr.menorDataIncorporada;
        numAmoGr = medidorGr.getSize();
        dtFimGr = medidorGr.maiorDataIncorporada;
        preencheData();
        laNumAmo.setText(numAmoGr + "");
        offInicioVi = 0;
        offFimVi = min(numAmoGr-1,180);
        numAmoVi = offFimVi;
        temSelecao = false;
        poeNaCena();
    }

    private void preencheData() {
        int diaSemIni = medidorGr.getDiaSemIni();
        for( XYChart.Data<Integer,Integer> este : enfo.getData() ){
            int maiorx = este.getXValue();
            if( maiorx >= numAmoGr )
                break;
            int y = medidorGr.getTot(maiorx);
            este.setYValue(y);
            mudfo[maiorx] = false;
        }
        for( XYChart.Data<Integer,Integer> este : medfo.getData() ){
            int maiorx = este.getXValue();
            if( maiorx >= numAmoGr )
                break;
            este.setYValue(mediaGr.getTot((diaSemIni+maiorx)%7));
        }
    }

    private void soAlteraMedia() {
        int diaSemIni = medidorGr.getDiaSemIni();
        for( XYChart.Data<Integer,Integer> este : medfo.getData() ){
            int maiorx = este.getXValue();
            if( maiorx >= numAmoGr )
                break;
            este.setYValue(mediaGr.getTot((diaSemIni+maiorx)%7));
        }
    }
   void poeNaCena() {
        laSelInf.setText("");
        laSelSup.setText("");
        rangeInf = 10000;
        rangeSup = -1;
        temSelecao = false;
        eixoX.setLowerBound(offInicioVi);
        eixoX.setUpperBound(offFimVi);
        eixoX.setTickUnit(numAmoVi / 7);
    }

    @FXML
    private void handleSeguinte(ActionEvent event) {
        handleOk(event);
    }

    @FXML
    private void handleDireita(ActionEvent event) {
        offInicioVi = min(offInicioVi +numAmoVi,numAmoGr-1);
        offFimVi = min(offInicioVi+numAmoVi,numAmoGr);
        poeNaCena();
    }
 
    @FXML
    private void handleDirMeio(ActionEvent event) {
        offInicioVi = min(offInicioVi +numAmoVi/2,numAmoGr-1);
        offFimVi = min(offInicioVi+numAmoVi,numAmoGr);
        poeNaCena();
}


    @FXML
    private void handleEsqMeio(ActionEvent event) {
        offInicioVi = max( 0, offInicioVi- numAmoVi/2);
        offFimVi = min( offInicioVi +numAmoVi,numAmoGr);
        poeNaCena();
    }

 
    @FXML
    private void handleEsquerda(ActionEvent event) {
        offInicioVi = max( 0, offInicioVi- numAmoVi);
        offFimVi = min( offInicioVi +numAmoVi,numAmoGr);
        poeNaCena();
    }

    @FXML
    private void handleEncolhe(ActionEvent event) { // na realidade AUMENTA os pontos
        numAmoVi += numAmoVi;
        offFimVi = min( offInicioVi +numAmoVi, numAmoGr);
        poeNaCena();
    }

    @FXML
    private void handleExpande(ActionEvent event) {// agora diminóie
        numAmoVi /= 2;
        if (numAmoVi < 8) {
            numAmoVi = 8;
        }
        offFimVi = min( offInicioVi +numAmoVi, numAmoGr);
        poeNaCena();
    }
    @FXML
    private void handleAte(ActionEvent event) {
        if (medidorGr == null) {
            emiteMensagem("Selecione um Medidor");
            return;
        }
        int este = (int)(mediaGr.ate().toEpochDay()-dtInicioGr.toEpochDay());
        if( este < 0 )
            este = 0;
        for (; este <= offFimVi; ++este) {
            if( mudfo[este] )
                continue;
            XYChart.Data<Integer,Integer> val = medfo.getData().get(este);
            LocalDate hoje = dtInicioGr.plusDays(este);
            mediaGr.add(hoje, medidorGr.getFora(hoje),medidorGr.getPonta(hoje) );
        }
        soAlteraMedia();
    }
    private void cancelaSel(){
        temSelecao = false;
        rangeInf = 10000;
        rangeSup = 0;
        laSelInf.setText("");
        laSelSup.setText("");
    }
            
    @FXML
    private void handleCancSel(ActionEvent event) {
        cancelaSel();
    }

    @FXML
    private void handleZerarSel(ActionEvent event) {
        if (temSelecao) {
            for (int este = rangeInf; este <= rangeSup; ++este) {
                XYChart.Data<Integer,Integer> val = enfo.getData().get(este);
                val.setYValue(0);
                mudfo[este]=true;
            }
        }
        cancelaSel();
    }

    @FXML
    private void handlePropSel(ActionEvent event) {
        LocalDate dia;
        if (temSelecao) {
            int sumCampo = 0;
            for (int este = rangeInf; este <= rangeSup; ++este) {
                XYChart.Data<Integer,Integer> val = enfo.getData().get(este);
                sumCampo += val.getYValue();
            }
            int sumMedia = 0;
            for (int este = rangeInf; este <= rangeSup; ++este) {
                XYChart.Data<Integer,Integer> val = medfo.getData().get(este);
                sumMedia += val.getYValue();
            }
            if (sumMedia > 10) {
                double prop = (sumCampo + 0.) / (sumMedia + 0.);
                for (int este = rangeInf; este < rangeSup; ++este) {
                    XYChart.Data<Integer,Integer> med = medfo.getData().get(este);
                    int valmed = (int) (med.getYValue()*prop);
                    XYChart.Data<Integer,Integer> val = enfo.getData().get(este);
                    val.setYValue(valmed);
                    mudfo[este] = true;
                    sumCampo -= valmed;
                }
                XYChart.Data<Integer,Integer> val = enfo.getData().get(rangeSup);
                val.setYValue(sumCampo);
                mudfo[rangeSup]=true;
            }
        }
        cancelaSel();
    }

    @FXML
    private void handleMediaSel(ActionEvent event) {
        if (temSelecao) {
            for (int este = rangeInf; este <= rangeSup; ++este) {
                XYChart.Data<Integer,Integer> val = enfo.getData().get(este);
                XYChart.Data<Integer,Integer> med = medfo.getData().get(este);
                val.setYValue(med.getYValue());
                mudfo[este] = true;
            }
        }
        cancelaSel();
    }

    @FXML
    private void handleOk(ActionEvent event) {
        // atualiza medias
        if (medidorGr == null) {
            emiteMensagem("Selecione um Medidor");
            return;
        }
        if (mediaGr.ate().isBefore(dtFimGr)) {
            LocalDate dia = mediaGr.ate();
            int offGraf = (int)(dia.toEpochDay()-dtInicioGr.toEpochDay());
            if( offGraf < 0){
                offGraf= 0;
                dia = dtInicioGr;
            }
            for (; offGraf < numAmoGr; ++offGraf, dia = dia.plusDays(1)) {
                if( !mudfo[offGraf])
                    mediaGr.add(dia, medidorGr.getFora(dia), medidorGr.getPonta(dia));
            }
        }
        for( XYChart.Data<Integer,Integer> este : enfo.getData() ){
            int maiorx = este.getXValue();
            if( maiorx >= numAmoGr )
                break;
            if( mudfo[maiorx] ){
                int y = este.getYValue();
                medidorGr.ajusta(maiorx,y);
            }
            mudfo[maiorx]=false;
        }
        cbMedidores.getSelectionModel().selectNext();
    }
    private void saiBrutas(){
        File velha = new File(tela.ArquivoInput);
        File salva = new File(velha.getParent(), String.format("ene%d.dat", java.lang.System.currentTimeMillis()/1000));
        velha.renameTo(salva);
        mdbru.salvaBrutas(tela.ArquivoInput);
        
    }

    private void saiMedias(){
        File velha = new File(tela.ArquivoMedias);
        File salva = new File(velha.getParent(), String.format("med%d.dat", java.lang.System.currentTimeMillis()/1000));
        velha.renameTo(salva);
        mdmed.salvaMedias(tela.ArquivoMedias);
    }
    @FXML
    private void handleSaiOk(ActionEvent event) {
        saiMedias();
        saiBrutas();
        System.exit(0);
    }

    @FXML
    private void handleSaiMedia(ActionEvent event) {
        saiMedias();
        System.exit(0);
    }

    @FXML
    private void handleSaiCanc(ActionEvent event) {
        System.exit(0);
    }
}
