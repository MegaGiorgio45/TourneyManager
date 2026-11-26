/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Juego;
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
public class JuegoDAO {

    public List<Juego> listar() {
        List<Juego> lista = new ArrayList<>();
        String sql = "SELECT * FROM juego ORDER BY id";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo juegos: " + e.getMessage());
        }
        return lista;
    }

    public Juego obtener(int id) {
        String sql = "SELECT * FROM juego WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo juego por id: " + e.getMessage());
        }
        return null;
    }

    public int insertar(Juego objeto) {
        String sql = "INSERT INTO juego(nombre) VALUES(?)";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, objeto.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    objeto.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error insertando juego: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Juego objeto) {
        String sql = "UPDATE juego SET nombre=? WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, objeto.getNombre());
            ps.setInt(2, objeto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizando juego: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sqlUso = "SELECT COUNT(*) FROM torneo WHERE juego_id=?";
        String sqlBorrar = "DELETE FROM juego WHERE id=?";
        try (Connection con = DatabaseManager.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(sqlUso)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlBorrar)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error eliminando juego: " + e.getMessage());
            return false;
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM juego";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error contando juegos: " + e.getMessage());
        }
        return 0;
    }

    private Juego mapear(ResultSet rs) throws SQLException {
        Juego j = new Juego();
        j.setId(rs.getInt("id"));
        j.setNombre(rs.getString("nombre"));
        return j;
    }

}
