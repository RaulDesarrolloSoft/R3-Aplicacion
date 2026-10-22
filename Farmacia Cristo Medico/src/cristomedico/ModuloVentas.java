package cristomedico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ModuloVentas extends JFrame {

    JComboBox<String> cmbProducto;
    JTextField txtCantidad;
    DefaultTableModel modelo;
    JTable tabla;
    JLabel lblTotal;
    double total = 0;

    public ModuloVentas() {
        setTitle("Punto de Venta - Cristo Medico");
        setSize(720, 560);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel l1 = new JLabel("Producto:"); l1.setBounds(20, 20, 80, 25);
        add(l1);

        cmbProducto = new JComboBox<>();
        cmbProducto.setBounds(100, 20, 350, 25);
        add(cmbProducto);

        JLabel l2 = new JLabel("Cantidad:"); l2.setBounds(470, 20, 80, 25);
        add(l2);

        txtCantidad = new JTextField("1");
        txtCantidad.setBounds(550, 20, 60, 25);
        add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(620, 20, 80, 25);
        add(btnAgregar);

        modelo = new DefaultTableModel(new Object[]{
            "ID", "Producto", "Precio", "Cantidad", "Subtotal"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBounds(20, 60, 680, 340);
        add(sp);

        lblTotal = new JLabel("TOTAL: $ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setBounds(20, 410, 300, 30);
        add(lblTotal);

        JButton btnFinalizar = new JButton("Finalizar Venta");
        btnFinalizar.setBounds(400, 410, 150, 40);
        add(btnFinalizar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(560, 410, 140, 40);
        add(btnCancelar);

        btnAgregar.addActionListener(e -> agregar());
        btnFinalizar.addActionListener(e -> finalizar());
        btnCancelar.addActionListener(e -> {
            modelo.setRowCount(0);
            total = 0;
            lblTotal.setText("TOTAL: $ 0.00");
        });

        cargarProductos();
    }

    private void cargarProductos() {
        cmbProducto.removeAllItems();
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT id, nombre, precio, stock FROM productos WHERE stock > 0 ORDER BY nombre")) {
            while (rs.next()) {
                cmbProducto.addItem(
                    rs.getInt("id") + " | " + rs.getString("nombre") +
                    " | $" + rs.getDouble("precio") +
                    " | Stock: " + rs.getInt("stock"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void agregar() {
        if (cmbProducto.getSelectedItem() == null) return;
        String item = cmbProducto.getSelectedItem().toString();
        String[] partes = item.split(" \\| ");
        int id = Integer.parseInt(partes[0]);
        String nombre = partes[1];
        double precio = Double.parseDouble(partes[2].replace("$", ""));
        int stock = Integer.parseInt(partes[3].replace("Stock: ", ""));

        int cant;
        try { cant = Integer.parseInt(txtCantidad.getText().trim()); }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            return;
        }
        if (cant <= 0 || cant > stock) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida o stock insuficiente.");
            return;
        }

        double subtotal = precio * cant;
        modelo.addRow(new Object[]{id, nombre, precio, cant, subtotal});
        total += subtotal;
        lblTotal.setText("TOTAL: $ " + String.format("%.2f", total));
        txtCantidad.setText("1");
    }

    private void finalizar() {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta.");
            return;
        }
        try (Connection con = Conexion.getConexion()) {
            con.setAutoCommit(false);

            // Insertar venta
            PreparedStatement psVenta = con.prepareStatement(
                "INSERT INTO ventas (total, usuario_id) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            psVenta.setDouble(1, total);
            psVenta.setInt(2, Sesion.idUsuario);
            psVenta.executeUpdate();

            ResultSet rs = psVenta.getGeneratedKeys();
            int ventaId = 0;
            if (rs.next()) ventaId = rs.getInt(1);

            // Insertar detalle y actualizar stock
            for (int i = 0; i < modelo.getRowCount(); i++) {
                int prodId = (int) modelo.getValueAt(i, 0);
                double precio = (double) modelo.getValueAt(i, 2);
                int cant = (int) modelo.getValueAt(i, 3);

                PreparedStatement psDet = con.prepareStatement(
                    "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio) VALUES (?,?,?,?)");
                psDet.setInt(1, ventaId);
                psDet.setInt(2, prodId);
                psDet.setInt(3, cant);
                psDet.setDouble(4, precio);
                psDet.executeUpdate();

                PreparedStatement psStock = con.prepareStatement(
                    "UPDATE productos SET stock = stock - ? WHERE id = ?");
                psStock.setInt(1, cant);
                psStock.setInt(2, prodId);
                psStock.executeUpdate();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "✅ Venta realizada. Total: $" + total);

            modelo.setRowCount(0);
            total = 0;
            lblTotal.setText("TOTAL: $ 0.00");
            cargarProductos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar venta: " + ex.getMessage());
        }
    }
}