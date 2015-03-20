/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geramedias;

import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 *
 * @author rbraga
 */
public class DdeBrutas {
    Utils util;
    DdeMedias media;

    protected String tag;
    protected LocalDate menorDataIncorporada;// dados com offset zero
    protected int diaSemIni;
    protected LocalDate maiorDataIncorporada;// último dia
    protected int sizeTabs;// maior offset 
    int[] EnFora;
    int[] EnPonta;

    public DdeBrutas(String med) {
        util = new Utils();
        tag = med;
    }

    void setMedia( DdeMedias meda ){
        media = meda;
    }
    
    String publish(LocalDate dia) {
        String cpos[] = new String[4];
        cpos[0] = util.escDia(dia);
        cpos[1] = tag;
        if( dia.isBefore(menorDataIncorporada)
         || dia.isAfter(maiorDataIncorporada)){
            cpos[2]="0";
            cpos[3]="0";
        }
        else {
            int ate = (int) menorDataIncorporada.until(dia, DAYS);
            cpos[2] = String.valueOf(EnFora[ate]);
            cpos[3] = String.valueOf(EnPonta[ate]);
        }
        return String.join(",", cpos)+"\n";
    }
    public int getSize(){
        return (int) menorDataIncorporada.until(maiorDataIncorporada, DAYS);
    }
    public int getDiaSemIni(){
        return diaSemIni;
    }
    public int getFora( LocalDate dia){
        if( dia.isBefore(menorDataIncorporada))
            return 0;
        if( dia.isAfter(maiorDataIncorporada))
            return 0;
        int ate = (int) menorDataIncorporada.until(dia, DAYS);
        return EnFora[ate];
    }

    public int getPonta( LocalDate dia){
        if( dia.isBefore(menorDataIncorporada))
            return 0;
        if( dia.isAfter(maiorDataIncorporada))
            return 0;
        int ate = (int) menorDataIncorporada.until(dia, DAYS);
        return EnPonta[ate];
    }

    public int getTot( LocalDate dia){
        if( dia.isBefore(menorDataIncorporada))
            return 0;
        if( dia.isAfter(maiorDataIncorporada))
            return 0;
        int ate = (int) menorDataIncorporada.until(dia, DAYS);
        return (EnFora[ate] +EnPonta[ate]);
    }

    public int getTot( int ate){
        if( ate >=0 && ate <sizeTabs )
            return (EnFora[ate] +EnPonta[ate]);
        return 0;
    }
    public void ajusta( int ate, int val ){
        
        double medFp =media.getFora((diaSemIni+ate)%7);
        double medPt =media.getPonta((diaSemIni+ate)%7);
        double tot = medFp+medPt;
        if( tot > 10. ){
            double prop = (double)val/tot;
            int newfora = (int)(medFp*prop);
            EnFora[ate]= newfora;
            EnPonta[ate] = val-newfora;
        }
        else{
            EnFora[ate]=val;
            EnPonta[ate]=0;
        }
    }

    public void addDia(LocalDate dia, int fora, int ponta) {
        if (menorDataIncorporada == null) {
            menorDataIncorporada = dia;
            maiorDataIncorporada = dia;
            diaSemIni = dia.getDayOfWeek().getValue()-1;
            sizeTabs = 45;
            EnFora = new int[45];
            EnPonta = new int[45];
        }
        if (menorDataIncorporada.isAfter(dia)) {
            return;
        }
        if (maiorDataIncorporada.isBefore(dia)) {
            maiorDataIncorporada = dia;
        }
        int ate = (int) menorDataIncorporada.until(dia, DAYS);
        if (ate >= sizeTabs) {
            int novoTam = sizeTabs*2;
            while( novoTam <= ate )novoTam *=2;
           //System.out.println(tag+" desde "+menorDataIncorporada.toString()+" ate="+ate+" sizeTabs="+sizeTabs+" novoTam="+novoTam);
            int[] novo1 = new int[novoTam];
            int[] novo2 = new int[novoTam];
            for (int ind = 0; ind < sizeTabs; ++ind) {
                novo1[ind] = EnFora[ind];
                novo2[ind] = EnPonta[ind];
            }
            EnFora = novo1;
            EnPonta = novo2;
            sizeTabs = novoTam;
        }
        EnFora[ate] = fora;
        EnPonta[ate] = ponta;
    }
    @Override
    public String toString(){
        return tag;
    }
}