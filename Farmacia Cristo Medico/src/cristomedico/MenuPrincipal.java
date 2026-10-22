package cristomedico;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Cristo Medico - Menú Principal");
        setSize(520, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(220, 240, 255));

        JLabel titulo = new JLabel("SISTEMA FARMACIA CRISTO MEDICO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBounds(20, 10, 470, 30);
        add(titulo);

        JLabel info = new JLabel("Usuario: " + Sesion.nombre + "  |  Rol: " + Sesion.rol);
        info.setBounds(20, 45, 470, 20);
        info.setForeground(Color.DARK_GRAY);
        add(info);

        // Botones
        JButton btnProductos   = new JButton("Productos (Insertar/Eliminar)");
        JButton btnInventario  = new JButton("Inventario");
        JButton btnVentas      = new JButton("Punto de Venta");
        JButton btnProveedores = new JButton("Proveedores");
        JButton btnReportes    = new JButton("Reportes");
        JButton btnUsuarios    = new JButton("Usuarios (Admin)");
        JButton btnSalir       = new JButton("Cerrar Sesión");

        btnProductos.setBounds(40, 90, 200, 40);
        btnInventario.setBounds(260, 90, 200, 40);
        btnVentas.setBounds(40, 145, 200, 40);
        btnProveedores.setBounds(260, 145, 200, 40);
        btnReportes.setBounds(40, 200, 200, 40);
        btnUsuarios.setBounds(260, 200, 200, 40);
        btnSalir.setBounds(150, 270, 200, 40);

        add(btnProductos); add(btnInventario); add(btnVentas);
        add(btnProveedores); add(btnReportes); add(btnUsuarios); add(btnSalir);

        // Permisos: solo admin ve el módulo Usuarios
        if (!"admin".equalsIgnoreCase(Sesion.rol)) {
            btnUsuarios.setEnabled(false);
            btnUsuarios.setToolTipText("Solo el Administrador puede registrar usuarios.");
        }

        btnProductos.addActionListener(e -> new ModuloProductos().setVisible(true));
        btnInventario.addActionListener(e -> new ModuloInventario().setVisible(true));
        btnVentas.addActionListener(e -> new ModuloVentas().setVisible(true));
        btnProveedores.addActionListener(e -> new ModuloProveedores().setVisible(true));
        btnReportes.addActionListener(e -> new ModuloReportes().setVisible(true));
        btnUsuarios.addActionListener(e -> new ModuloUsuarios().setVisible(true));

        btnSalir.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                dispose();
                new Login().setVisible(true);
            }
        });
    }
}