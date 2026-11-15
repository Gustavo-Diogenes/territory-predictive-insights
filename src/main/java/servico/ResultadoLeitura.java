package servico;

import dominio.Medicao;
import java.util.List;

/** Resultado de ler os sensores de um ponto: medições gravadas e falhas ocorridas. */
public record ResultadoLeitura(List<Medicao> medicoes, List<String> falhas) {
}
