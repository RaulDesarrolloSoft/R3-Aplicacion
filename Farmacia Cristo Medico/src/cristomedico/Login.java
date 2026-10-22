package cristomedico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {

    JTextField txtUsuario;
    JPasswordField txtPassword;
    JButton btnAceptar, btnFirmarse, btnModulos, btnSalir;

    public Login() {
        setTitle("Cristo Medico - Iniciar Sesión");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 245, 255));

        JLabel titulo = new JLabel("FARMACIA CRISTO MEDICO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBounds(30, 15, 350, 30);
        add(titulo);

        JLabel lblU = new JLabel("Usuario:");
        lblU.setBounds(40, 70, 100, 25);
        add(lblU);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(140, 70, 220, 25);
        add(txtUsuario);

        JLabel lblP = new JLabel("Contraseña:");
        lblP.setBounds(40, 110, 100, 25);
        add(lblP);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 110, 220, 25);
        add(txtPassword);

        btnAceptar  = new JButton("Aceptar");
        btnFirmarse = new JButton("Firmarse");
        btnModulos  = new JButton("Módulos");
        btnSalir    = new JButton("Salir");

        btnAceptar.setBounds(40, 170, 150, 35);
        btnFirmarse.setBounds(210, 170, 150, 35);
        btnModulos.setBounds(40, 220, 150, 35);
        btnSalir.setBounds(210, 220, 150, 35);

        add(btnAceptar); add(btnFirmarse); add(btnModulos); add(btnSalir);

        // 🔐 Aceptar = valida credenciales
        btnAceptar.addActionListener(e -> iniciarSesion());

        // 🖊️ Firmarse = limpia formulario
        btnFirmarse.addActionListener(e -> {
            txtUsuario.setText("");
            txtPassword.setText("");
            txtUsuario.requestFocus();
        });

        // 📋 Módulos = muestra los módulos del sistema
        btnModulos.addActionListener(e -> {
            String modulos =
                "MÓDULOS DEL SISTEMA CRISTO MEDICO\n\n" +
                "1. Usuarios (solo Administrador)\n" +
                "2. Productos (Insertar / Eliminar)\n" +
                "3. Inventario de productos\n" +
                "4. Punto de Venta\n" +
                "5. Proveedores\n" +
                "6. Reportes\n";
            JOptionPane.showMessageDialog(this, modulos, "Módulos", JOptionPane.INFORMATION_MESSAGE);
        });

        // ❌ Salir
        btnSalir.addActionListener(e -> System.exit(0));

        // Enter dispara login
        getRootPane().setDefaultButton(btnAceptar);
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT id, usuario, nombre, rol FROM usuarios WHERE usuario=? AND password=?")) {

            ps.setString(1, usuario);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Sesion.idUsuario = rs.getInt("id");
                Sesion.usuario   = rs.getString("usuario");
                Sesion.nombre    = rs.getString("nombre");
                Sesion.rol       = rs.getString("rol");

                JOptionPane.showMessageDialog(this, "Bienvenido " + Sesion.nombre);
                new MenuPrincipal().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error BD: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}