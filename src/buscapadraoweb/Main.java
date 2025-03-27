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
        crw.getListaRecursos().add("https://pt.wikipedia.org/wiki/Endere%C3%A7o_MAC");//ENDERECO 3
        //crw.getListaRecursos().add("https://www.controle.net/faq/o-que-e-mac-address");//ENDERECO 4 nAO ENCONTRA
        //crw.getListaRecursos().add("https://help.gnome.org/users/gnome-help/stable/net-macaddress.html.pt#:~:text=Um%20endere%C3%A7o%20MAC%20consiste%20em,um%20exemplo%20de%20endere%C3%A7o%20MAC.");
        //crw.getListaRecursos().add("http://localhost:3000/");// ENDERECO 1


        ArrayList<String> listaCodigos = crw.carregarRecursos();

        String codigoHTML = listaCodigos.get(0);

        //mapa do alfabeto
        char[] alfabeto = new char[24];

        int index = 0;

        for(char c = '0'; c <= '9'; c++){
            alfabeto[index++] = c;
        }

        for (char c = 'A'; c <= 'F'; c++) {
            alfabeto[index++] = c;
        }

        for (char c = 'a'; c <= 'f'; c++) {
            alfabeto[index++] = c;
        }

        alfabeto[index++] = ':';
        alfabeto[index] = '-';

        //mapa de estados
        String[] estados = new String[33];
        for (int i = 0; i < 33; i++) {
            estados[i] = "q" + i;
        }

        String estado_inicial = "q0";

        //estados finais
        String[] estados_finais = new String[2];
        estados_finais[0] = "q17";
        estados_finais[1] = "q32";

        //tabela de transição de AFD para reconhecimento MAC
        int[][] matriz = new int[33][24]; //[estados][alfabeto]

        for(int i = 0; i < 33; i++){
            boolean ehEstadoFinal = (i == 17 || i == 32);
            boolean ehDivisivelPor3 = (i + 1) % 3 == 0;

            for (int y = 0; y < alfabeto.length - 2; y++) {
                if (ehEstadoFinal) {
                    matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, alfabeto[y])] = -1;

                    if (y == alfabeto.length -3) {
                        matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, ':')] = -1;
                        matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, '-')] = -1;
                    }
                } else if (ehDivisivelPor3) {
                    matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, alfabeto[y])] = -1;

                    if (y == alfabeto.length -3) {
                        if (i == 2) {
                            matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, ':')] = get_string_ref(estados, "q" + (i + 1));
                            matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, '-')] = get_string_ref(estados, "q" + (i + 16));
                        } else if( i > 18 ){
                            matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, ':')] = -1;
                            matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, '-')] = get_string_ref(estados, "q" + (i + 1));
                        } else {
                            matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, ':')] = get_string_ref(estados, "q" + (i + 1));
                            matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, '-')] = -1;
                        }
                    }
                } else {
                    matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, alfabeto[y])] = get_string_ref(estados, "q" + (i + 1));

                    if (y == alfabeto.length -3) {
                        matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, ':')] = -1;
                        matriz[get_string_ref(estados, "q" + i)][get_char_ref(alfabeto, '-')] = -1;
                    }
                }
            }
        }

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
