/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geramedias;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author rbraga
 */
public class Utils {
      private DateTimeFormatter formatter; 
    
    public Utils(){
        formatter = DateTimeFormatter.ofPattern("yyyy/MMM/dd");
    }
    public DateTimeFormatter getFormat(){
        return formatter;
    }
    
    public String escDia( LocalDate dia ){
       return dia.format(formatter);
    }
    
    public LocalDate leDia(String data){
        // vem 2007/apr/01 ou 2007/abr/01 out 2015-02-03
        String sep = "/";
        if( data.contains("-"))
            sep = "-";
        String[] campos = data.split(sep);
        if( campos.length != 3 ){
            return LocalDate.of(2000,1,1);
        }
        int ano;
        try { 
            ano = Integer.parseInt(campos[0]);
            if( ano > 10000)
                ano = 1945;
        }
        catch (NumberFormatException e)  {
            ano=1945;
        }
        int dia;
        try{
            dia = Integer.parseInt(campos[2]);
        }
        catch (NumberFormatException e){
            dia = 1;
        }
        Month mes= Month.JANUARY;
        switch (campos[1].toLowerCase()) {
            case "04":
            case "abr":
            case "apr":  mes = Month.APRIL;     break;
            case "08":
            case "ago":
            case "aug":  mes = Month.AUGUST;    break;
            case "12":
            case "dec":
            case "dez":  mes = Month.DECEMBER;  break;
            case "02":
            case "feb":
            case "fev":  mes = Month.FEBRUARY;  break;
            case "01":
            case "jan":  mes =  Month.JANUARY;   break;
            case "06":
            case "jun":  mes = Month.JUNE;      break;
            case "07":
            case "jul":  mes = Month.JULY;      break;
            case "03":
            case "mar":  mes = Month.MARCH;     break;
            case "05":
            case "mai":
            case "may":  mes = Month.MAY;       break;
            case "11":
            case "nov":  mes = Month.NOVEMBER;  break;
            case "10":
            case "oct":
            case "out":  mes = Month.OCTOBER;   break;
            case "09":
            case "sep":
            case "set":  mes = Month.SEPTEMBER; break;
        }
        return LocalDate.of(ano, mes, dia);
    }
    private String limpa(String suja ){
        int inic = 0;
        for( ; inic < suja.length();++inic ){
            if( suja.charAt(inic) != '\'' && suja.charAt( inic ) != ' ' )
                break;
        }
        int fin = suja.length()-1;
        for( ;fin >=0; --fin ){
            if( suja.charAt(fin) != '\'' && suja.charAt( fin ) != ' ' )
                break;
        }
        return suja.substring(inic, fin+1);
    }

}
