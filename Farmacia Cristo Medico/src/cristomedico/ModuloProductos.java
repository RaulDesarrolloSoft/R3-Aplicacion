package cristomedico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ModuloProductos extends JFrame {

    JTextField txtCodigo, txtNombre, txtDesc, txtPrecio, txtStock, txtFecha;
    DefaultTableModel modelo;
    JTable tabla;

    public ModuloProductos() {
        setTitle("Módulo Productos - Cristo Medico");
        setSize(760, 560);
        setLocationRelativeTo(null);
        setLayout(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBounds(10, 10, 725, 500);
        add(tabs);

        // ---------- TAB INSERTAR ----------
        JPanel pnlIns = new JPanel(null);
        tabs.add("Insertar", pnlIns);

        JLabel l1 = new JLabel("Código:"); l1.setBounds(40, 30, 100, 25);
        JLabel l2 = new JLabel("Nombre:"); l2.setBounds(40, 70, 100, 25);
        JLabel l3 = new JLabel("Descripción:"); l3.setBounds(40, 110, 100, 25);
        JLabel l4 = new JLabel("Precio:"); l4.setBounds(40, 150, 100, 25);
        JLabel l5 = new JLabel("Stock:"); l5.setBounds(40, 190, 100, 25);
        JLabel l6 = new JLabel("Fecha cad. (YYYY-MM-DD):"); l6.setBounds(40, 230, 200, 25);

        txtCodigo = new JTextField(); txtCodigo.setBounds(240, 30, 250, 25);
        txtNombre = new JTextField(); txtNombre.setBounds(240, 70, 250, 25);
        txtDesc   = new JTextField(); txtDesc.setBounds(240, 110, 250, 25);
        txtPrecio = new JTextField(); txtPrecio.setBounds(240, 150, 250, 25);
        txtStock  = new JTextField(); txtStock.setBounds(240, 190, 250, 25);
        txtFecha  = new JTextField(); txtFecha.setBounds(240, 230, 250, 25);

        pnlIns.add(l1); pnlIns.add(l2); pnlIns.add(l3); pnlIns.add(l4); pnlIns.add(l5); pnlIns.add(l6);
        pnlIns.add(txtCodigo); pnlIns.add(txtNombre); pnlIns.add(txtDesc);
        pnlIns.add(txtPrecio); pnlIns.add(txtStock); pnlIns.add(txtFecha);

        JButton btnGuardar = new JButton("Guardar Producto");
        btnGuardar.setBounds(240, 280, 180, 35);
        pnlIns.add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(430, 280, 100, 35);
        pnlIns.add(btnLimpiar);

        btnLimpiar.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> insertar());

        // ---------- TAB ELIMINAR ----------
        JPanel pnlEli = new JPanel(null);
        tabs.add("Eliminar", pnlEli);

        modelo = new DefaultTableModel(new Object[]{
            "ID", "Código", "Nombre", "Precio", "Stock", "Fecha cad."
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBounds(20, 20, 680, 340);
        pnlEli.add(sp);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBounds(20, 380, 200, 35);
        pnlEli.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBounds(240, 380, 150, 35);
        pnlEli.add(btnRefrescar);

        btnRefrescar.addActionListener(e -> cargarTabla());
        btnEliminar.addActionListener(e -> eliminar());

        cargarTabla();
    }

    private void limpiar() {
        txtCodigo.setText(""); txtNombre.setText(""); txtDesc.setText("");
        txtPrecio.setText(""); txtStock.setText(""); txtFecha.setText("");
    }

    private void insertar() {
        if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() ||
            txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los campos obligatorios.");
            return;
        }
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(
                "INSERT INTO productos (codigo,nombre,descripcion,precio,stock,fecha_caducidad) VALUES (?,?,?,?,?,?)")) {

            ps.setString(1, txtCodigo.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtDesc.getText().trim());
            ps.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            String fecha = txtFecha.getText().trim();
            if (fecha.isEmpty()) ps.setNull(6, java.sql.Types.DATE);
            else ps.setDate(6, java.sql.Date.valueOf(fecha));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Producto insertado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al insertar: " + ex.getMessage());
        }
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT id, codigo, nombre, precio, stock, fecha_caducidad FROM productos ORDER BY id DESC")) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getDate("fecha_caducidad")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }
        int id = (int) modelo.getValueAt(fila, 0);
        String nombre = (String) modelo.getValueAt(fila, 2);

        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto: " + nombre + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM productos WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "🗑️ Producto eliminado.");
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }
}