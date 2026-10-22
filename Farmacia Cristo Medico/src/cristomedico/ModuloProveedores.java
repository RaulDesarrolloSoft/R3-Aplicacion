package cristomedico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ModuloProveedores extends JFrame {

    JTextField txtNombre, txtTel, txtCorreo, txtDir;
    DefaultTableModel modelo;

    public ModuloProveedores() {
        setTitle("Proveedores - Cristo Medico");
        setSize(720, 500);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel l1 = new JLabel("Nombre:");    l1.setBounds(20, 20, 100, 25);
        JLabel l2 = new JLabel("Teléfono:");  l2.setBounds(20, 55, 100, 25);
        JLabel l3 = new JLabel("Correo:");    l3.setBounds(20, 90, 100, 25);
        JLabel l4 = new JLabel("Dirección:"); l4.setBounds(20, 125, 100, 25);

        txtNombre = new JTextField(); txtNombre.setBounds(130, 20, 300, 25);
        txtTel    = new JTextField(); txtTel.setBounds(130, 55, 300, 25);
        txtCorreo = new JTextField(); txtCorreo.setBounds(130, 90, 300, 25);
        txtDir    = new JTextField(); txtDir.setBounds(130, 125, 300, 25);

        add(l1); add(l2); add(l3); add(l4);
        add(txtNombre); add(txtTel); add(txtCorreo); add(txtDir);

        JButton btnAgregar = new JButton("Agregar Proveedor");
        btnAgregar.setBounds(460, 20, 200, 35);
        add(btnAgregar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(460, 65, 200, 35);
        add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBounds(460, 110, 200, 35);
        add(btnRefrescar);

        modelo = new DefaultTableModel(new Object[]{
            "ID", "Nombre", "Teléfono", "Correo", "Dirección"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBounds(20, 170, 660, 270);
        add(sp);

        btnAgregar.addActionListener(e -> {
            if (txtNombre.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre.");
                return;
            }
            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO proveedores (nombre,telefono,correo,direccion) VALUES (?,?,?,?)")) {
                ps.setString(1, txtNombre.getText().trim());
                ps.setString(2, txtTel.getText().trim());
                ps.setString(3, txtCorreo.getText().trim());
                ps.setString(4, txtDir.getText().trim());
                ps.executeUpdate();
                txtNombre.setText(""); txtTel.setText("");
                txtCorreo.setText(""); txtDir.setText("");
                cargar();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) return;
            int id = (int) modelo.getValueAt(fila, 0);
            try (Connection con = Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM proveedores WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                cargar();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnRefrescar.addActionListener(e -> cargar());
        cargar();
    }

    private void cargar() {
        modelo.setRowCount(0);
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM proveedores ORDER BY nombre")) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("nombre"),
                    rs.getString("telefono"), rs.getString("correo"),
                    rs.getString("direccion")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}