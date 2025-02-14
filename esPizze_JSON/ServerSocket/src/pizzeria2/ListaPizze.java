package pizzeria2;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;

public class ListaPizze {
    private String [] listaPizze;

    //Costruttori
    public ListaPizze(){}
    public ListaPizze(int dim){
        this.listaPizze=new String[dim];
    }
    public ListaPizze(String [] listaPizze){
        this.listaPizze=listaPizze;
    }

    //Get
    public String[] getListaPizze() { return listaPizze; }
    @JsonIgnore
    public int getLength() {
        return listaPizze.length;
    }
    @JsonIgnore
    public String getPizzaIndice(int indice) {
        return this.listaPizze[indice];
    }

    //Metodi per la scelta o la ricerca
    public String listaScelta(){
        String s="";
        for (int i = 0; i < listaPizze.length; i++) {
            s=s+i + ": " + listaPizze[i]+"\n";
        }
        return s;
    }
    public boolean cercaPizza(String s){
        for (String pizza : listaPizze){
            if (pizza.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
    //Stampa
    @Override
    public String toString(){
        return Arrays.toString(listaPizze);
    }
}
