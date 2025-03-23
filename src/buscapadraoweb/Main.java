/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buscapadraoweb;

import buscaweb.CapturaRecursosWeb;
import java.util.ArrayList;

/**
 *
 * @author Santiago
 */
public class Main {

    // busca char em vetor e retorna indice
    public static int get_char_ref(char[] vet, char ref) {
        for (int i = 0; i < vet.length; i++) {
            if (vet[i] == ref) {
                return i;
            }
        }
        return -1;
    }

    // busca string em vetor e retorna indice
    public static int get_string_ref(String[] vet, String ref) {
        for (int i = 0; i < vet.length; i++) {
            if (vet[i].equals(ref)) {
                return i;
            }
        }
        return -1;
    }

    //retorna o próximo estado, dado o estado atual e o símbolo lido
    public static int proximo_estado(char[] alfabeto, int[][] matriz, int estado_atual, char simbolo) {
        int simbol_indice = get_char_ref(alfabeto, simbolo);
        if (simbol_indice != -1) {
            return matriz[estado_atual][simbol_indice];
        } else {
            return -1;
        }
    }

    /*
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //instancia e usa objeto que captura código-fonte de páginas Web
        CapturaRecursosWeb crw = new CapturaRecursosWeb();
        //crw.getListaRecursos().add("https://www.techtudo.com.br/noticias/2014/11/o-que-e-endereco-mac-saiba-como-encontrar.ghtml");// ENDERECO 1 
        //crw.getListaRecursos().add("https://www.cisco.com/c/pt_br/support/docs/ip/routing-information-protocol-rip/13788-3.html ");//ENDERECO 2 NAO CONTRA
       //crw.getListaRecursos().add("https://pt.wikipedia.org/wiki/Endere%C3%A7o_MAC");//ENDERECO 3
       //crw.getListaRecursos().add("https://www.controle.net/faq/o-que-e-mac-address");//ENDERECO 4 nAO ENCONTRA
       //crw.getListaRecursos().add("https://help.gnome.org/users/gnome-help/stable/net-macaddress.html.pt#:~:text=Um%20endere%C3%A7o%20MAC%20consiste%20em,um%20exemplo%20de%20endere%C3%A7o%20MAC.");
        
        ArrayList<String> listaCodigos = crw.carregarRecursos();

        String codigoHTML = listaCodigos.get(0);

        //mapa do alfabeto
        char[] alfabeto = new char[24];
        int index = 0;

        for (char c = '0'; c <= '9'; c++) {
            alfabeto[index++] = c; // 0-9 (10 caracteres)
        }
        for (char c = 'A'; c <= 'F'; c++) {
            alfabeto[index++] = c; // A-F (6 caracteres)
        }
        for (char c = 'a'; c <= 'f'; c++) {
            alfabeto[index++] = c; // a-f (6 caracteres)
        }
        alfabeto[index++] = ':'; // Delimitador 1
        alfabeto[index++] = '-'; // Delimitador 2

        //mapa de estados
        String[] estados = new String[18];
        for (int i = 0; i < 18; i++) {
            estados[i] = "q" + i;
        }

        String estado_inicial = "q0";

        //estados finais
        String[] estados_finais = new String[1];
        estados_finais[0] = "q17";

        //tabela de transição de AFD para reconhecimento números de dois dígitos
        int[][] matriz = new int[18][24];
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 24; j++) {
                matriz[i][j] = -1;  //Inicializa com -1 (sem transição)
            }
        };

        //Prenche a tabela de tranficao
        for (int i = 0; i < 16; i++) {
            matriz[0][i] = 1; // q0 -> q1
            matriz[1][i] = 2; // q1 -> q2
            matriz[3][i] = 4; // q3 -> q4
            matriz[4][i] = 5; // q4 -> q5
            matriz[6][i] = 7; // q6 -> q7
            matriz[7][i] = 8; // q7 -> q8
            matriz[9][i] = 10; // q9 -> q10
            matriz[10][i] = 11; // q10 -> q11
            matriz[12][i] = 13; // q12 -> q13
            matriz[13][i] = 14; // q13 -> q14
            matriz[15][i] = 16; // q15 -> q16
            matriz[16][i] = 17; // q16 -> q17
        }

        matriz[2][22] = 3; // q2 -> q3 (delimitador :)
        matriz[2][23] = 3; // q2 -> q3 (delimitador -)
        matriz[5][22] = 6; // q5 -> q6 (delimitador :)
        matriz[5][23] = 6; // q5 -> q6 (delimitador -)
        matriz[8][22] = 9; // q8 -> q9 (delimitador :)
        matriz[8][23] = 9; // q8 -> q9 (delimitador -)
        matriz[11][22] = 12; // q11 -> q12 (delimitador :)
        matriz[11][23] = 12; // q11 -> q12 (delimitador -)
        matriz[14][22] = 15; // q14 -> q15 (delimitador :)
        matriz[14][23] = 15; // q14 -> q15 (delimitador -)

        int estado = get_string_ref(estados, estado_inicial);
        int estado_anterior = -1;
        ArrayList<String> palavras_reconhecidas = new ArrayList();

        String palavra = "";

        //varre o código-fonte de um código
        for (int i = 0; i < codigoHTML.length(); i++) {

            estado_anterior = estado;
            estado = proximo_estado(alfabeto, matriz, estado, codigoHTML.charAt(i));
            //se o não há transição
            if (estado == -1) {
                //pega estado inicial
                estado = get_string_ref(estados, estado_inicial);
                // se o estado anterior foi um estado final
                if (get_string_ref(estados_finais, estados[estado_anterior]) != -1) {
                    //se a palavra não é vazia adiciona palavra reconhecida
                    if (!palavra.equals("")) {
                        palavras_reconhecidas.add(palavra);
                    }
                    // se ao analisar este caracter não houve transição
                    // teste-o novamente, considerando que o estado seja inicial
                    i--;
                }
                //zera palavra
                palavra = "";

            } else {
                //se houver transição válida, adiciona caracter a palavra
                palavra += codigoHTML.charAt(i);
            }
        }
        //foreach no Java para exibir todas as palavras reconhecidas
        if (palavras_reconhecidas.isEmpty()) {
            System.err.println("Nenhum endereco MAC foi reconhecido");
        } else {
            System.err.println("Enderecos MAC reconhecidos:");
            for (String p : palavras_reconhecidas) {
                System.out.println(p);
            }
        }

    }

}
