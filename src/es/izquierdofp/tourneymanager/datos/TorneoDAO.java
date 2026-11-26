/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Torneo;
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
public class TorneoDAO {

    public List<Torneo> listar() {
        List<Torneo> lista = new ArrayList<>();
        String sql = "SELECT * FROM torneo ORDER BY fecha";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo torneos: " + e.getMessage());
        }
        return lista;
    }

    public List<Torneo> buscar(String texto) {
        List<Torneo> lista = new ArrayList<>();
        String sql = "SELECT * FROM torneo WHERE nombre LIKE ? ORDER BY fecha";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error buscando torneos: " + e.getMessage());
        }
        return lista;
    }

    public Torneo obtener(int id) {
        String sql = "SELECT * FROM torneo WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo torneo por id: " + e.getMessage());
        }
        return null;
    }

    public int insertar(Torneo objeto) {
        String sql = "INSERT INTO torneo(nombre, juego_id, fecha, premio, online) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, objeto.getNombre());
            ps.setInt(2, objeto.getJuegoId());
            ps.setString(3, objeto.getFecha());
            ps.setDouble(4, objeto.getPremio());
            ps.setInt(5, objeto.isOnline() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    objeto.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error insertando torneo: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Torneo objeto) {
        String sql = "UPDATE torneo SET nombre=?, juego_id=?, fecha=?, premio=?, online=? WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, objeto.getNombre());
            ps.setInt(2, objeto.getJuegoId());
            ps.setString(3, objeto.getFecha());
            ps.setDouble(4, objeto.getPremio());
            ps.setInt(5, objeto.isOnline() ? 1 : 0);
            ps.setInt(6, objeto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizando torneo: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM torneo WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminando torneo: " + e.getMessage());
            return false;
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM torneo";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error contando torneos: " + e.getMessage());
        }
        return 0;
    }

    private Torneo mapear(ResultSet rs) throws SQLException {
        Torneo t = new Torneo();
        t.setId(rs.getInt("id"));
        t.setNombre(rs.getString("nombre"));
        t.setJuegoId(rs.getInt("juego_id"));
        t.setFecha(rs.getString("fecha"));
        t.setPremio(rs.getDouble("premio"));
        t.setOnline(rs.getInt("online") == 1);
        return t;
    }

}
