package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;
import excecao.DadosInvalidosException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Grava e consulta alertas no banco, sempre com PreparedStatement. */
public class AlertaDAO {

    private final ConexaoBanco conexao;

    public AlertaDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    /** Grava o alerta e devolve o id gerado pelo banco. */
    public long salvar(Alerta alerta) throws SQLException {
        String sql = "INSERT INTO alerta (sensor_id, tipo, nivel, valor_previsto, limite_ultrapassado, data_hora) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = conexao.abrir();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, alerta.getSensorId());
            ps.setString(2, alerta.getTipo());
            ps.setString(3, alerta.getNivel().name());
            ps.setDouble(4, alerta.getValorPrevisto());
            ps.setDouble(5, alerta.getLimiteUltrapassado());
            ps.setTimestamp(6, Timestamp.valueOf(alerta.getDataHora()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getLong(1);
                }
                throw new SQLException("Não foi possível obter o id do alerta gravado.");
            }
        }
    }

    /** Todos os alertas emitidos, do mais recente para o mais antigo. */
    public List<Alerta> listarTodos() throws SQLException {
        String sql = "SELECT id, sensor_id, tipo, nivel, valor_previsto, limite_ultrapassado, data_hora "
                + "FROM alerta ORDER BY data_hora DESC, id DESC";
        List<Alerta> alertas = new ArrayList<>();
        try (Connection c = conexao.abrir();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                alertas.add(mapear(rs));
            }
        }
        return alertas;
    }

    private Alerta mapear(ResultSet rs) throws SQLException {
        try {
            return new Alerta(rs.getLong("id"), rs.getString("sensor_id"), rs.getString("tipo"),
                    NivelAlerta.valueOf(rs.getString("nivel")), rs.getDouble("valor_previsto"),
                    rs.getDouble("limite_ultrapassado"), rs.getTimestamp("data_hora").toLocalDateTime());
        } catch (DadosInvalidosException | IllegalArgumentException e) {
            throw new SQLException("Alerta inválido encontrado no banco: " + e.getMessage(), e);
        }
    }
}
