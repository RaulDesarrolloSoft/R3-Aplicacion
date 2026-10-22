package cristomedico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ModuloReportes extends JFrame {

    DefaultTableModel modelo;

    public ModuloReportes() {
        setTitle("Reportes - Cristo Medico");
        setSize(760, 500);
        setLocationRelativeTo(null);
        setLayout(null);

        modelo = new DefaultTableModel(new Object[]{
            "ID Venta", "Fecha", "Usuario", "Total"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBounds(10, 10, 725, 400);
        add(sp);

        JButton btnVentas = new JButton("Ventas del día");
        btnVentas.setBounds(10, 420, 180, 30);
        add(btnVentas);

        JButton btnTodas = new JButton("Todas las ventas");
        btnTodas.setBounds(200, 420, 180, 30);
        add(btnTodas);

        JButton btnTotal = new JButton("Total recaudado");
        btnTotal.setBounds(390, 420, 180, 30);
        add(btnTotal);

        btnVentas.addActionListener(e -> cargar(
            "SELECT v.id, v.fecha, u.nombre, v.total FROM ventas v " +
            "JOIN usuarios u ON u.id = v.usuario_id " +
            "WHERE DATE(v.fecha) = CURDATE() ORDER BY v.id DESC"));

        btnTodas.addActionListener(e -> cargar(
            "SELECT v.id, v.fecha, u.nombre, v.total FROM ventas v " +
            "JOIN usuarios u ON u.id = v.usuario_id ORDER BY v.id DESC"));

        btnTotal.addActionListener(e -> {
            try (Connection con = Conexion.getConexion();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT IFNULL(SUM(total),0) FROM ventas")) {
                if (rs.next())
                    JOptionPane.showMessageDialog(this,
                        "Total recaudado: $ " + rs.getDouble(1));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        cargar("SELECT v.id, v.fecha, u.nombre, v.total FROM ventas v " +
               "JOIN usuarios u ON u.id = v.usuario_id ORDER BY v.id DESC");
    }

    private void cargar(String sql) {
        modelo.setRowCount(0);
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt(1), rs.getTimestamp(2),
                    rs.getString(3), rs.getDouble(4)
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}