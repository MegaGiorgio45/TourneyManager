/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.izquierdofp.tourneymanager.datos;

/**
 *
 * @author 2DAM
 */
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static String rutaBD = System.getProperty("user.home")
            + File.separator + ".tourneymanager"
            + File.separator + "tourneymanager.db";

    public static void setRutaBD(String ruta) {
        rutaBD = ruta;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + rutaBD);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void inicializar() {
        File carpeta = new File(rutaBD).getAbsoluteFile().getParentFile();
        carpeta.mkdirs();

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            crearTablas(stmt);

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM juego");
            rs.next();
            int numJuegos = rs.getInt(1);
            rs.close();

            if (numJuegos == 0) {
                cargarDatosPrueba(stmt);
            }

        } catch (SQLException e) {
            System.err.println("Error al inicializar BD: " + e.getMessage());
        }
    }

    private static void crearTablas(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS juego (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE
            )""");

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS rol (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE
            )""");

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS equipo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                ciudad TEXT NOT NULL,
                fundacion INTEGER NOT NULL CHECK (fundacion BETWEEN 1990 AND 2100),
                logo TEXT
            )""");

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS torneo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                juego_id INTEGER NOT NULL REFERENCES juego(id) ON DELETE RESTRICT,
                fecha TEXT NOT NULL,
                premio REAL NOT NULL CHECK (premio >= 0),
                online INTEGER NOT NULL DEFAULT 0 CHECK (online IN (0,1))
            )""");

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS inscripcion (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                equipo_id INTEGER NOT NULL REFERENCES equipo(id) ON DELETE CASCADE,
                torneo_id INTEGER NOT NULL REFERENCES torneo(id) ON DELETE CASCADE,
                posicion INTEGER CHECK (posicion IS NULL OR posicion >= 1),
                UNIQUE (equipo_id, torneo_id)
            )""");

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS jugador (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                equipo_id INTEGER NOT NULL REFERENCES equipo(id) ON DELETE CASCADE,
                rol_id INTEGER NOT NULL REFERENCES rol(id) ON DELETE RESTRICT,
                nick TEXT NOT NULL UNIQUE,
                nombre TEXT NOT NULL,
                edad INTEGER NOT NULL CHECK (edad BETWEEN 14 AND 60)
            )""");
    }

    private static void cargarDatosPrueba(Statement stmt) throws SQLException {
        stmt.executeUpdate("""
            INSERT INTO juego (nombre) VALUES
            ('League of Legends'), ('Valorant'), ('Rocket League'), ('EA Sports FC'), ('Fortnite')
            """);

        stmt.executeUpdate("""
            INSERT INTO rol (nombre) VALUES
            ('Capitán'), ('Titular'), ('Suplente'), ('Entrenador')
            """);

        stmt.executeUpdate("""
            INSERT INTO equipo (nombre, ciudad, fundacion) VALUES
            ('Nébula Squad', 'Madrid', 2019),
            ('Faro Norte', 'A Coruña', 2018),
            ('Lince Rojo', 'Toledo', 2021),
            ('Marea Azul', 'Valencia', 2017),
            ('Volcán GG', 'Las Palmas', 2020),
            ('Duna Esports', 'Almería', 2022),
            ('Roble Team', 'Oviedo', 2016),
            ('Kraken Club', 'Cádiz', 2023)
            """);

        stmt.executeUpdate("""
            INSERT INTO jugador (equipo_id, rol_id, nick, nombre, edad) VALUES
            (1, 1, 'Orion', 'Carlos Medina', 23),
            (1, 2, 'Pulsar', 'Nuria Vidal', 20),
            (1, 2, 'Quasar', 'Tomás Rey', 22),
            (1, 4, 'ProfeVega', 'Luis Vega', 39),
            (2, 1, 'Bruma', 'Iria Castro', 24),
            (2, 2, 'Ancla', 'Martín Lago', 19),
            (2, 3, 'Gaviota', 'Uxía Rial', 16),
            (3, 1, 'Zarpa', 'Gonzalo Ruiz', 26),
            (3, 2, 'Sigilo', 'Clara Moreno', 21),
            (3, 2, 'Acecho', 'Samuel Prieto', 18),
            (3, 3, 'Bigote', 'Lola Caballero', 15),
            (3, 4, 'MisterLince', 'Rosa Delgado', 45),
            (4, 1, 'Oleaje', 'Pau Ferrando', 25),
            (4, 2, 'Salitre', 'Neus Ribes', 22),
            (4, 2, 'Coral', 'Vicent Mira', 20),
            (4, 3, 'Espuma', 'Aitana Llorens', 17),
            (5, 1, 'Magma', 'Yeray Santana', 27),
            (5, 2, 'Lava', 'Ayoze Cabrera', 23),
            (5, 2, 'Ceniza', 'Nayra Quintana', 19),
            (5, 3, 'Basalto', 'Echedey Pérez', 16),
            (5, 3, 'Teide', 'Saray Hernández', 18),
            (5, 4, 'CoachFuego', 'Elena Suárez', 33),
            (6, 1, 'Espejismo', 'Rocío Fuentes', 21),
            (6, 2, 'Siroco', 'Manuel Cortés', 24),
            (6, 2, 'Oasis', 'Alicia Ramos', 20),
            (7, 1, 'Raíz', 'Xuan Menéndez', 28),
            (7, 2, 'Bellota', 'Covadonga Álvarez', 22),
            (7, 2, 'Musgo', 'Pelayo Fernández', 19),
            (7, 4, 'Leñador', 'Jesús Iglesias', 50),
            (8, 1, 'Tentáculo', 'Lucas Benítez', 20),
            (8, 2, 'Abismo', 'Marina Domínguez', 18),
            (8, 3, 'Tinta', 'Rafael Moya', 14)
            """);

        stmt.executeUpdate("""
            INSERT INTO torneo (nombre, juego_id, fecha, premio, online) VALUES
            ('Open Nébula LoL',        1, date('now', '-35 days'), 400, 0),
            ('Valorant Spring Series', 2, date('now', '-15 days'), 250, 1),
            ('Turbo Cup',              3, date('now', '-5 days'),  180, 1),
            ('EA FC Derbi Night',      4, date('now', '+5 days'),  120, 0),
            ('Fortnite Storm Royale',  5, date('now', '+25 days'), 300, 1),
            ('Masters de Invierno',    2, date('now', '+60 days'), 800, 0)
            """);

        stmt.executeUpdate("""
            INSERT INTO inscripcion (equipo_id, torneo_id, posicion) VALUES
            (4, 1, 1), (7, 1, 2), (1, 1, 3), (8, 1, 4), (3, 1, 5),
            (5, 2, 1), (6, 2, 2), (1, 2, 3), (3, 2, 4), (7, 2, 5), (2, 2, 6),
            (2, 3, 1), (4, 3, 2), (5, 3, 3), (8, 3, 4),
            (1, 4, NULL), (6, 4, NULL), (8, 4, NULL),
            (3, 5, NULL), (5, 5, NULL), (7, 5, NULL),
            (2, 6, NULL), (4, 6, NULL)
            """);
    }
}
