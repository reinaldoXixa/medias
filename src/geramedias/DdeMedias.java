/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geramedias;

import java.time.LocalDate;

/**
 *
 * @author rbraga
 */
public class DdeMedias {
    protected final static int NUMAMO = 10; 
    protected String tag;
    private LocalDate maiorDataIncorporada;
    private final int medMovFora[][];
    private final int medMovPonta[][];
    private final int numAmostras[];
    private final int rover[];
    private final int mediaFora[];
    private final int mediaPonta[];
    private final boolean recalc[];
    
    void martela( String[] pedacos){//0;10;4;5202|5202|7604|7367|5723|6002|5206|6160|3639|5202;1291|1291|0|0|1329|1319|1291|1427|114|1291
        int diaSem = Integer.parseInt(pedacos[0]);
        recalc[diaSem]=true;
        numAmostras[diaSem] = Integer.parseInt(pedacos[1]);
        rover[diaSem] = Integer.parseInt(pedacos[2]);
        String[] mmov = pedacos[3].split("[|]");
        for (int namo = 0; namo < NUMAMO; ++namo) {
            medMovFora[diaSem][namo] = Integer.parseInt(mmov[namo]);
        }
        mmov = pedacos[4].split("[|]");
        for (int namo = 0; namo < NUMAMO; ++namo) {
            medMovPonta[diaSem][namo] = Integer.parseInt(mmov[namo]);
        }
    }
    String publish(int diaSem) {
        String cpos[] = new String[5];
        cpos[0] = diaSem+"";
        cpos[1] = String.valueOf(numAmostras[diaSem]);
        cpos[2] = String.valueOf(rover[diaSem]);
        String[] txamo = new String[NUMAMO];
        for (int namo = 0; namo < NUMAMO; ++namo) {
            txamo[namo] = String.valueOf(medMovFora[diaSem][namo]);
        }
        cpos[3] = String.join("|", txamo);
        txamo = new String[NUMAMO];
        for (int namo = 0; namo < NUMAMO; ++namo) {
            txamo[namo] = String.valueOf(medMovPonta[diaSem][namo]);
        }
        cpos[4] = String.join("|", txamo);
        return String.join(";", cpos)+"\n";
    }
    
    public void add( LocalDate dia, int fora, int ponta ){
        if( maiorDataIncorporada.isAfter(dia))
            return;
        maiorDataIncorporada = dia;
        int diaSem = dia.getDayOfWeek().getValue()-1;
        
        medMovFora[diaSem][rover[diaSem]] = fora;
        medMovPonta[diaSem][rover[diaSem]] = ponta;
        if( ++rover[diaSem] >= NUMAMO)
            rover[diaSem]=0;
        if(numAmostras[diaSem]< NUMAMO)
            ++numAmostras[diaSem];
        recalc[diaSem]=true;
    }
    private void recalcula(int diaSem){
        int sigmaF = 0;
        int sigmaP = 0;
        if( numAmostras[diaSem]==0){// nunca recalcula em zero!
            mediaFora[diaSem]=0;
            mediaPonta[diaSem]=0;
        }
        else {
            for( int amo = 0; amo < numAmostras[diaSem]; ++amo ){
                sigmaF += medMovFora[diaSem][amo];
                sigmaP += medMovPonta[diaSem][amo];
            }
            mediaFora[diaSem] = sigmaF/(numAmostras[diaSem]);
            mediaPonta[diaSem] = sigmaP/(numAmostras[diaSem]);
        }
        recalc[diaSem]= false;
    }
    public LocalDate ate(){
        return maiorDataIncorporada;
    }
    public void setAte(LocalDate dia){
        maiorDataIncorporada = dia;
    }
    public int getFora( LocalDate dia ){
        int diaSem = dia.getDayOfWeek().getValue()-1;
        if( recalc[diaSem] )
            recalcula(diaSem);
        return mediaFora[diaSem];
    }
    public int getPonta( LocalDate dia ){
        int diaSem = dia.getDayOfWeek().getValue()-1;
        if( recalc[diaSem] )
            recalcula(diaSem);
        return mediaPonta[ diaSem ];
    }
    public int getTot( LocalDate dia ){
        int diaSem = dia.getDayOfWeek().getValue()-1;
        if( recalc[diaSem] )
            recalcula(diaSem);
        return (mediaFora[diaSem]+mediaPonta[diaSem]);
    }
    public int getTot( int diaSem ){
        if( recalc[diaSem] )
            recalcula(diaSem);
        return (mediaFora[diaSem]+mediaPonta[diaSem]);
    }
    public int getFora( int diaSem ){
        if( recalc[diaSem] )
            recalcula(diaSem);
        return mediaFora[diaSem];
    }
    public int getPonta( int diaSem ){
        if( recalc[diaSem] )
            recalcula(diaSem);
        return mediaPonta[ diaSem ];
    }
    public DdeMedias(String med){
        medMovFora = new int[7][NUMAMO];
        medMovPonta = new int[7][NUMAMO];
        mediaFora = new int[7];
        mediaPonta = new int[7];
        numAmostras = new int[7];
        rover = new int[7];
        recalc = new boolean[7];
        tag = med;
        maiorDataIncorporada = LocalDate.of(1945,4,20);
        for( int diaSem=0;diaSem < 7;++diaSem){
  //          for( int mmtam=0; mmtam<NUMAMO;++mmtam){
    //            medMovFora[diaSem][mmtam]=0.;
      //          medMovPonta[diaSem][mmtam]=0.;
        //    }
//            numAmostras[diaSem]= 0;
//            mediaFora[diaSem]= 0.;
  //          mediaPonta[diaSem]= 0.;
            recalc[diaSem] = false;
        }
    }
    @Override
    public String toString(){
        return tag;
    }
}
