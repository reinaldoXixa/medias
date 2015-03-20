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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rbraga em casa
 */
public class MdeMedias {
    Utils util;
    private List<DdeMedias> lista;
    public int numMedias;

    public MdeMedias() {
        util = new Utils();
        numMedias = 0;
        lista = new ArrayList(200);
    }

    public void limpa() {
        numMedias = 0;
        lista = new ArrayList(200);
    }

    public DdeMedias get(String medidor) {
        for (DdeMedias este : lista) {
            if (este.tag.equals(medidor)) {
                return este;
            }
        }
        ++numMedias;
        DdeMedias este = new DdeMedias(medidor);
        lista.add(este);
        return este;
    }

    public int numMed() {
        return numMedias;
    }

    public void set(DdeMedias novo) { // pra que serve isto!???
        for (DdeMedias este : lista) {
            if (este.tag.equals(novo.tag)) {
                lista.remove(este);
            }
        }
        lista.add(novo);
    }

    public boolean lerMedias(String repo){
        limpa();
        boolean leu = false;
        Charset charset = Charset.forName("UTF8");
        if( repo == null )
            return leu;
        Path file = Paths.get(repo);//EN0001TEREC01;2007/mmm/22
                                    // 0;10;4;5202|5202|7604|7367|5723|6002|5206|6160|3639|5202;1291|1291|0|0|1329|1319|1291|1427|114|1291
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            while ( true ){
                String line = reader.readLine();
                if( line == null )
                    break;
                String[] picotado = line.split(";");
                DdeMedias este = get( picotado[0] );
                este.setAte(util.leDia(picotado[1]));
                for( int dia =0;dia<7;++dia){
                    line = reader.readLine();
                    picotado = line.split(";");
                    este.martela(picotado);
                }
            }
            leu = true;
        } catch (IOException x) {
            System.out.println("Não abriu arquivo medias");
        }
        return leu;
    }
    public boolean salvaMedias(String repo){
        Charset charset = Charset.forName("UTF8");
        Path file = Paths.get(repo);
        boolean salvou = false;
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            for (DdeMedias este : lista) {
                String cabecalho = este.tag +";"+util.escDia(este.ate())+"\n";
                writer.write(cabecalho);
                for( int esteDiaSem = 0; esteDiaSem<7;esteDiaSem++)
                    writer.write(este.publish(esteDiaSem));
            }
            salvou = true;
        } catch (IOException x) {
            System.out.println("Não abriu arquivo medias");
        }
        return salvou;
    }
}
