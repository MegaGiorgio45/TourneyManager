/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.izquierdofp.tourneymanager.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author 2DAM
 */
public class EstadisticasDAO {

    public int equiposInscritos(int torneoId) {
        String sql = "SELECT COUNT(*) FROM inscripcion WHERE torneo_id=?";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, torneoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error contando equipos inscritos: " + e.getMessage());
        }
        return 0;
    }

    public double premioTotal() {
        String sql = "SELECT COALESCE(SUM(premio), 0) FROM torneo";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error calculando premio total: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Integer> ranking() {
        Map<String, Integer> ranking = new LinkedHashMap<>();
        String sql = "SELECT e.nombre, "
                + "SUM(CASE WHEN i.posicion = 1 THEN 10 "
                + "WHEN i.posicion = 2 THEN 6 "
                + "WHEN i.posicion = 3 THEN 4 "
                + "ELSE 1 END) AS puntos "
                + "FROM equipo e JOIN inscripcion i ON i.equipo_id = e.id "
                + "WHERE i.posicion IS NOT NULL "
                + "GROUP BY e.id, e.nombre "
                + "ORDER BY puntos DESC, e.nombre";
        try (Connection con = DatabaseManager.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ranking.put(rs.getString("nombre"), rs.getInt("puntos"));
            }
        } catch (SQLException e) {
            System.out.println("Error calculando ranking: " + e.getMessage());
        }
        return ranking;
    }
}
