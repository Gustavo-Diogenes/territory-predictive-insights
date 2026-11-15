package servico;

import dominio.Alerta;
import java.util.List;

/** Resultado de rodar as previsões: linhas explicativas e alertas emitidos. */
public record RelatorioPrevisao(List<String> linhas, List<Alerta> alertas) {
}
