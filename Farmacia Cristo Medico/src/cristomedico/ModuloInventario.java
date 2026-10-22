package cristomedico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ModuloInventario extends JFrame {

    DefaultTableModel modelo;

    public ModuloInventario() {
        setTitle("Inventario - Cristo Medico");
        setSize(760, 480);
        setLocationRelativeTo(null);
        setLayout(null);

        modelo = new DefaultTableModel(new Object[]{
            "ID", "Código", "Nombre", "Descripción", "Precio", "Stock", "Fecha cad."
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBounds(10, 10, 725, 400);
        add(sp);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBounds(10, 415, 150, 30);
        add(btnRefrescar);

        JButton btnBajo = new JButton("Ver stock bajo (<10)");
        btnBajo.setBounds(170, 415, 200, 30);
        add(btnBajo);

        btnRefrescar.addActionListener(e -> cargar("SELECT * FROM productos ORDER BY nombre"));
        btnBajo.addActionListener(e ->
            cargar("SELECT * FROM productos WHERE stock < 10 ORDER BY stock"));

        cargar("SELECT * FROM productos ORDER BY nombre");
    }

    private void cargar(String sql) {
        modelo.setRowCount(0);
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getDate("fecha_caducidad")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}