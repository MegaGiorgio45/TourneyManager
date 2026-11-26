/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Jugador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2DAM
 */
public class JugadorDAO {

    public List<Jugador> listar() {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT * FROM jugador ORDER BY equipo_id, nick";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo jugadores: " + e.getMessage());
        }
        return lista;
    }

    public List<Jugador> buscar(String texto) {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT * FROM jugador WHERE nick LIKE ? OR nombre LIKE ? ORDER BY equipo_id, nick";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error buscando jugadores: " + e.getMessage());
        }
        return lista;
    }

    public List<Jugador> listarPorEquipo(int equipoId) {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT * FROM jugador WHERE equipo_id=? ORDER BY nick";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, equipoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo jugadores por equipo: " + e.getMessage());
        }
        return lista;
    }

    public Jugador obtener(int id) {
        String sql = "SELECT * FROM jugador WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo jugador por id: " + e.getMessage());
        }
        return null;
    }

    public int insertar(Jugador objeto) {
        if (jugadoresEnEquipo(objeto.getEquipoId()) >= 7) {
            return -1;
        }
        String sql = "INSERT INTO jugador(equipo_id, rol_id, nick, nombre, edad) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, objeto.getEquipoId());
            ps.setInt(2, objeto.getRolId());
            ps.setString(3, objeto.getNick());
            ps.setString(4, objeto.getNombre());
            ps.setInt(5, objeto.getEdad());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    objeto.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error insertando jugador: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Jugador objeto) {
        String sql = "UPDATE jugador SET equipo_id=?, rol_id=?, nick=?, nombre=?, edad=? WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, objeto.getEquipoId());
            ps.setInt(2, objeto.getRolId());
            ps.setString(3, objeto.getNick());
            ps.setString(4, objeto.getNombre());
            ps.setInt(5, objeto.getEdad());
            ps.setInt(6, objeto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizando jugador: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM jugador WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminando jugador: " + e.getMessage());
            return false;
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM jugador";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error contando jugadores: " + e.getMessage());
        }
        return 0;
    }

    private int jugadoresEnEquipo(int equipoId) {
        String sql = "SELECT COUNT(*) FROM jugador WHERE equipo_id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, equipoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error contando jugadores del equipo: " + e.getMessage());
        }
        return 0;
    }

    private Jugador mapear(ResultSet rs) throws SQLException {
        Jugador j = new Jugador();
        j.setId(rs.getInt("id"));
        j.setEquipoId(rs.getInt("equipo_id"));
        j.setRolId(rs.getInt("rol_id"));
        j.setNick(rs.getString("nick"));
        j.setNombre(rs.getString("nombre"));
        j.setEdad(rs.getInt("edad"));
        return j;
    }
}
