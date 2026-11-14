package operacoes;


import enums.NivelRisco;
import enums.TipoTerreno;
import java.io.Serializable;


 public class AreaOperacao implements Serializable {
 private String nome;
 private final int id;
 private String localizacao;
 private TipoTerreno tipoTerreno;
 private String descricao;
 private NivelRisco nivelRisco;

 public AreaOperacao(String nome, int id,String localizacao, TipoTerreno tipoTerreno, String descricao,NivelRisco nivelRisco){
    this.nome = nome;
    this.id = id;
    this.localizacao = localizacao;
    this.tipoTerreno = tipoTerreno;
    this.descricao = descricao;
    this.nivelRisco = nivelRisco;
 }
 
 public NivelRisco atualizarNivelRisco(NivelRisco novoNivel){
    if (novoNivel == null){
      throw new IllegalArgumentException("Nível de risco não pode ser nulo.");
    }
    NivelRisco nivelAnterior = nivelRisco;
    this.nivelRisco = novoNivel;
    return nivelAnterior;
         
 }

 public void exibirInformacoes(){
        System.out.println("Área :" + id + " - " + nome);
        System.out.println("Localização: " + localizacao);
        System.out.println("Terreno: " + tipoTerreno);
        System.out.println("Risco: " + nivelRisco);
        System.out.println("Descrição: " + descricao);
    }
 

 public String getNome(){
    return this.nome;
 }

 public int getId(){
    return this.id;
 }
 
 public String getLocalizacao(){
    return this.localizacao;
 }

 public NivelRisco getNivelRisco(){
    return this.nivelRisco;
 }

 public TipoTerreno getTipoTerreno() { 
   return tipoTerreno; 
 }

 public String getDescricao() {
    return descricao; 
 }
}