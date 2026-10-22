package cristomedico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ModuloUsuarios extends JFrame {

    JTextField txtUsuario, txtNombre, txtPassword;
    JComboBox<String> cmbRol;
    DefaultTableModel modelo;

    public ModuloUsuarios() {
        if (!"admin".equalsIgnoreCase(Sesion.rol)) {
            JOptionPane.showMessageDialog(null, "Solo el administrador puede acceder.");
            dispose();
            return;
        }

        setTitle("Usuarios (Administrador) - Cristo Medico");
        setSize(700, 520);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel l1 = new JLabel("Usuario:"); l1.setBounds(20, 20, 100, 25);
        JLabel l2 = new JLabel("Nombre:"); l2.setBounds(20, 55, 100, 25);
        JLabel l3 = new JLabel("Contraseña:"); l3.setBounds(20, 90, 100, 25);
        JLabel l4 = new JLabel("Rol:"); l4.setBounds(20, 125, 100, 25);

        txtUsuario  = new JTextField(); txtUsuario.setBounds(130, 20, 200, 25);
        txtNombre   = new JTextField(); txtNombre.setBounds(130, 55, 200, 25);
        txtPassword = new JTextField(); txtPassword.setBounds(130, 90, 200, 25);
        cmbRol = new JComboBox<>(new String[]{"empleado", "admin"});
        cmbRol.setBounds(130, 125, 200, 25);

        add(l1); add(l2); add(l3); add(l4);
        add(txtUsuario); add(txtNombre); add(txtPassword); add(cmbRol);

        JButton btnCrear = new JButton("Registrar Usuario");
        btnCrear.setBounds(350, 20, 180, 35);
        add(btnCrear);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBounds(350, 65, 180, 35);
        add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBounds(350, 110, 180, 35);
        add(btnRefrescar);

        modelo = new DefaultTableModel(new Object[]{"ID", "Usuario", "Nombre", "Rol"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBounds(20, 170, 640, 290);
        add(sp);

        btnCrear.addActionListener(e -> crear());
        btnEliminar.addActionListener(e -> eliminar(tabla));
        btnRefrescar.addActionListener(e -> cargar());

        cargar();
    }

    private void crear() {
        if (txtUsuario.getText().isEmpty() || txtNombre.getText().isEmpty() ||
            txtPassword.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO usuarios (usuario, password, nombre, rol) VALUES (?,?,?,?)")) {
            ps.setString(1, txtUsuario.getText().trim());
            ps.setString(2, txtPassword.getText().trim());
            ps.setString(3, txtNombre.getText().trim());
            ps.setString(4, cmbRol.getSelectedItem().toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Usuario creado.");
            txtUsuario.setText(""); txtNombre.setText(""); txtPassword.setText("");
            cargar();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "El usuario ya existe.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminar(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        if (id == Sesion.idUsuario) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propio usuario.");
            return;
        }
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM usuarios WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "🗑️ Usuario eliminado.");
            cargar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void cargar() {
        modelo.setRowCount(0);
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT id, usuario, nombre, rol FROM usuarios ORDER BY id")) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("usuario"),
                    rs.getString("nombre"), rs.getString("rol")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}