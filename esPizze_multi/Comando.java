package esPizze_multi;

public class Comando {
    private String nomeDelComando;
    private int parametro1;

    public Comando(){}
    public Comando(String msg, int parametro1){
        this.nomeDelComando=msg;
        this.parametro1=parametro1;
    }

    public String getNomeDelComando() {
        return nomeDelComando;
    }
    public int getParametro1() {
        return parametro1;
    }
    public void setNomeDelComando(String nomeDelComando) {
        this.nomeDelComando = nomeDelComando;
    }
    public void setParametro1(int parametro1) {
        this.parametro1 = parametro1;
    }
}
