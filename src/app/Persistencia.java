package app;

import operacoes.CentralResgate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Persistencia {
    private static final String PASTA = "dados";
    private static final String ARQUIVO = PASTA + File.separator + "central.dat";
    private static final String NOME_CENTRAL = "Central de Resgate";

    public static boolean salvar(CentralResgate central){
        if(central = null){
            System.out.println("Erro ao salvar: não foi possível criar pasta.");
            return false;
        }

        File pasta = new File(PASTA);
        if(!pasta.exists() && !pasta.mkdirs()){
            System.out.println("Erro ao salvar: não foi possível criar pasta.'" + PASTA + "'.");
            return false;
        }

        try(ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(ARQUIVO))){
            saida.writeObject(central);
            return true;
        }catch(IOException e){
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
            return false;
        }
    }

    public static CentralResgate carregar(){
        File arquivo = new File(ARQUIVO);
        if(!arquivo.exists()){
            System.out.println("Nenhum dado salvo encontrado. Iniciando com a central vazia.");
            return new CentralResgate(NOME_CENTRAL);
        }

        try(ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivo))){
            Object lido = entrada.readObject();
            if(lido instanceof CentralResgate){
                System.out.println("Dados carregados de " + ARQUIVO + ".");
                return (CentralResgate) lido;
            }
            System.out.println("O arquivo de dados tem um formato inválido. Iniciando com a central vazia.");
        }catch(IOException | ClassNotFoundException e){
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
            System.out.println("Iniciando com a central vazia.");
        }
        return new CentralResgate(NOME_CENTRAL);
    }

}
