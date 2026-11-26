/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.izquierdofp.tourneymanager.datos;

import es.izquierdofp.tourneymanager.modelo.Equipo;
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
public class EquipoDAO {

    public List<Equipo> listar() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipo ORDER BY nombre";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo equipos: " + e.getMessage());
        }
        return lista;
    }

    public List<Equipo> buscar(String texto) {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipo WHERE nombre LIKE ? OR ciudad LIKE ? ORDER BY nombre";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error buscando equipos: " + e.getMessage());
        }
        return lista;
    }

    public Equipo obtener(int id) {
        String sql = "SELECT * FROM equipo WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo equipo por id: " + e.getMessage());
        }
        return null;
    }

    public int insertar(Equipo objeto) {
        String sql = "INSERT INTO equipo(nombre, ciudad, fundacion, logo) VALUES(?, ?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, objeto.getNombre());
            ps.setString(2, objeto.getCiudad());
            ps.setInt(3, objeto.getFundacion());
            ps.setString(4, objeto.getLogo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    objeto.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error insertando equipo: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Equipo objeto) {
        String sql = "UPDATE equipo SET nombre=?, ciudad=?, fundacion=?, logo=? WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, objeto.getNombre());
            ps.setString(2, objeto.getCiudad());
            ps.setInt(3, objeto.getFundacion());
            ps.setString(4, objeto.getLogo());
            ps.setInt(5, objeto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizando equipo: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM equipo WHERE id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminando equipo: " + e.getMessage());
            return false;
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM equipo";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error contando equipos: " + e.getMessage());
        }
        return 0;
    }

    private Equipo mapear(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setCiudad(rs.getString("ciudad"));
        e.setFundacion(rs.getInt("fundacion"));
        e.setLogo(rs.getString("logo"));
        return e;
    }

}
