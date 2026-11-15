package dominio;

/** Qualquer elemento monitorado do território. */
public abstract class ComponenteTerritorial {

    private final String id;
    private final String nome;
    private final String regiao;
    private String status;

    protected ComponenteTerritorial(String id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = "NÃO VERIFICADO";
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getRegiao() { return regiao; }
    public String getStatus() { return status; }

    /** Só as subclasses alteram o status. */
    protected void setStatus(String status) { this.status = status; }

    /** Cada tipo de componente sabe verificar o próprio estado. */
    public abstract String verificarStatus();
}
