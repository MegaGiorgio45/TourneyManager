/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Inscripcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2DAM
 */
public class InscripcionDAO {

    public List<Inscripcion> listar() {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscripcion ORDER BY torneo_id, posicion IS NULL, posicion";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo inscripciones: " + e.getMessage());
        }
        return lista;
    }

    public List<Inscripcion> listarPorEquipo(int equipoId) {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscripcion WHERE equipo_id=? ORDER BY torneo_id";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, equipoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo inscripciones por equipo: " + e.getMessage());
        }
        return lista;
    }

    public List<Inscripcion> listarPorTorneo(int torneoId) {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscripcion WHERE torneo_id=? ORDER BY posicion IS NULL, posicion";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, torneoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo inscripciones por torneo: " + e.getMessage());
        }
        return lista;
    }

    public Inscripcion obtener(int id) {
        String sql = "SELECT * FROM inscripcion WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo inscripcion por id: " + e.getMessage());
        }
        return null;
    }

    public int insertar(Inscripcion objeto) {
        if (inscritosEnTorneo(objeto.getTorneoId()) >= 16) {
            return -1;
        }
        if (objeto.getPosicion() != null && posicionOcupada(objeto.getTorneoId(), objeto.getPosicion(), 0)) {
            return -1;
        }
        String sql = "INSERT INTO inscripcion(equipo_id, torneo_id, posicion) VALUES(?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, objeto.getEquipoId());
            ps.setInt(2, objeto.getTorneoId());
            if (objeto.getPosicion() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, objeto.getPosicion());
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    objeto.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error insertando inscripcion: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Inscripcion objeto) {
        if (objeto.getPosicion() != null && posicionOcupada(objeto.getTorneoId(), objeto.getPosicion(), objeto.getId())) {
            return false;
        }
        String sql = "UPDATE inscripcion SET equipo_id=?, torneo_id=?, posicion=? WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, objeto.getEquipoId());
            ps.setInt(2, objeto.getTorneoId());
            if (objeto.getPosicion() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, objeto.getPosicion());
            }
            ps.setInt(4, objeto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizando inscripcion: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM inscripcion WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminando inscripcion: " + e.getMessage());
            return false;
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM inscripcion";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error contando inscripciones: " + e.getMessage());
        }
        return 0;
    }

    private int inscritosEnTorneo(int torneoId) {
        String sql = "SELECT COUNT(*) FROM inscripcion WHERE torneo_id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, torneoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error contando inscritos del torneo: " + e.getMessage());
        }
        return 0;
    }

    private boolean posicionOcupada(int torneoId, int posicion, int idExcluido) {
        String sql = "SELECT COUNT(*) FROM inscripcion WHERE torneo_id=? AND posicion=? AND id<>?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, torneoId);
            ps.setInt(2, posicion);
            ps.setInt(3, idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error comprobando posicion: " + e.getMessage());
        }
        return false;
    }

    private Inscripcion mapear(ResultSet rs) throws SQLException {
        Inscripcion i = new Inscripcion();
        i.setId(rs.getInt("id"));
        i.setEquipoId(rs.getInt("equipo_id"));
        i.setTorneoId(rs.getInt("torneo_id"));
        int posicion = rs.getInt("posicion");
        i.setPosicion(rs.wasNull() ? null : posicion);
        return i;
    }
}
