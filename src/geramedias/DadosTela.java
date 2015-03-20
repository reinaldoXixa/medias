/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geramedias;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * variaveis que passam entre execuções
 * @author rbraga
 */
public class DadosTela implements Serializable {
    protected String ArquivoInput;
    protected String ArquivoMedias;
    protected LocalDate ultimaLeitura;
}
