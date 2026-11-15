package persistencia;

import dominio.Medicao;
import excecao.DadosInvalidosException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Grava e consulta medições no banco, sempre com PreparedStatement. */
public class MedicaoDAO {

    private final ConexaoBanco conexao;

    public MedicaoDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    /** Grava a medição e devolve o id gerado pelo banco. */
    public long salvar(Medicao medicao) throws SQLException {
        String sql = "INSERT INTO medicao (sensor_id, tipo, valor, data_hora) VALUES (?, ?, ?, ?)";
        try (Connection c = conexao.abrir();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medicao.getSensorId());
            ps.setString(2, medicao.getTipo());
            ps.setDouble(3, medicao.getValor());
            ps.setTimestamp(4, Timestamp.valueOf(medicao.getDataHora()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getLong(1);
                }
                throw new SQLException("Não foi possível obter o id da medição gravada.");
            }
        }
    }

    /** Histórico de um sensor, da medição mais recente para a mais antiga. */
    public List<Medicao> buscarHistoricoPorSensor(String sensorId) throws SQLException {
        String sql = "SELECT id, sensor_id, tipo, valor, data_hora FROM medicao "
                + "WHERE sensor_id = ? ORDER BY data_hora DESC, id DESC";
        List<Medicao> historico = new ArrayList<>();
        try (Connection c = conexao.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sensorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    historico.add(mapear(rs));
                }
            }
        }
        return historico;
    }

    /** Últimos valores de um sensor, em ordem cronológica (do mais antigo para o mais novo). */
    public List<Double> buscarUltimosValores(String sensorId, int quantidade) throws SQLException {
        String sql = "SELECT valor FROM medicao WHERE sensor_id = ? "
                + "ORDER BY data_hora DESC, id DESC LIMIT ?";
        List<Double> valores = new ArrayList<>();
        try (Connection c = conexao.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sensorId);
            ps.setInt(2, quantidade);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    valores.add(rs.getDouble("valor"));
                }
            }
        }
        Collections.reverse(valores); // a consulta traz do mais novo para o mais antigo
        return valores;
    }

    private Medicao mapear(ResultSet rs) throws SQLException {
        try {
            return new Medicao(rs.getLong("id"), rs.getString("sensor_id"), rs.getString("tipo"),
                    rs.getDouble("valor"), rs.getTimestamp("data_hora").toLocalDateTime());
        } catch (DadosInvalidosException e) {
            throw new SQLException("Medição inválida encontrada no banco: " + e.getMessage(), e);
        }
    }
}
