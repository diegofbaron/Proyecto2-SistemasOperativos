/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import modelo.*;

public class VentanaPrincipal extends JFrame {
    
    private GestorSistemaArchivos gestor;

    private JTree arbolSistema;
    private JTable tablaAsignacion;
    private JPanel panelDisco;
    private JComboBox<String> comboModoUsuario;
    private JButton btnCrear;
    private JButton btnEliminar;

    public VentanaPrincipal() {
        gestor = new GestorSistemaArchivos(100); 

        setTitle("Simulador de Sistema de Archivos - Proyecto 2");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        arbolSistema = new JTree();
        JScrollPane scrollArbol = new JScrollPane(arbolSistema);
        scrollArbol.setPreferredSize(new Dimension(250, 0));
        scrollArbol.setBorder(BorderFactory.createTitledBorder("Estructura de Directorios"));
        add(scrollArbol, BorderLayout.WEST);

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        
        panelDisco = new JPanel();
        panelDisco.setBackground(Color.DARK_GRAY);
        JScrollPane scrollDisco = new JScrollPane(panelDisco);
        scrollDisco.setBorder(BorderFactory.createTitledBorder("Simulación del SD (Bloques)"));
        panelCentral.add(scrollDisco);

        tablaAsignacion = new JTable();
        JScrollPane scrollTabla = new JScrollPane(tablaAsignacion);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Tabla de Asignación"));
        panelCentral.add(scrollTabla);

        add(panelCentral, BorderLayout.CENTER);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        comboModoUsuario = new JComboBox<>(new String[]{"Modo Administrador", "Modo Usuario"});
        btnCrear = new JButton("Crear Archivo/Directorio");
        btnEliminar = new JButton("Eliminar");

        panelControles.add(new JLabel("Modo:"));
        panelControles.add(comboModoUsuario);
        panelControles.add(btnCrear);
        panelControles.add(btnEliminar);

        add(panelControles, BorderLayout.SOUTH);

        btnCrear.addActionListener(e -> accionCrearElemento());

        actualizarArbol();
        actualizarDisco();
        actualizarTabla();
    }

    private void accionCrearElemento() {
        if (comboModoUsuario.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden crear elementos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un directorio en el árbol.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ElementoSistema elementoPadre = (ElementoSistema) nodoSeleccionado.getUserObject();
        if (!(elementoPadre instanceof Directorio)) {
            JOptionPane.showMessageDialog(this, "No puedes crear un elemento dentro de un archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Directorio directorioPadre = (Directorio) elementoPadre;

        String[] opciones = {"Archivo", "Directorio"};
        int seleccion = JOptionPane.showOptionDialog(this, "¿Qué deseas crear?", "Crear Elemento",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == -1) return; 

        String nombre = JOptionPane.showInputDialog(this, "Nombre del elemento:");
        if (nombre == null || nombre.trim().isEmpty()) return;

        if (seleccion == 0) { 
            String tamanoStr = JOptionPane.showInputDialog(this, "Tamaño en bloques:");
            try {
                int tamano = Integer.parseInt(tamanoStr);
                if (tamano <= 0) throw new NumberFormatException();
                
                Proceso p = gestor.solicitarCreacionArchivo(nombre, "admin", directorioPadre, tamano);
                gestor.ejecutarProceso(p); 
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El tamaño debe ser un entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else { 
            Directorio nuevoDir = new Directorio(nombre, "admin", directorioPadre);
            directorioPadre.agregarHijo(nuevoDir);
        }

        actualizarArbol(); 
        actualizarDisco();
        actualizarTabla();
        expandirTodoElArbol(); 
    }

    private void actualizarArbol() {
        Directorio raizModelo = gestor.obtenerRaiz();
        DefaultMutableTreeNode raizVisual = crearNodoVisual(raizModelo);
        arbolSistema.setModel(new DefaultTreeModel(raizVisual));
    }

    private DefaultMutableTreeNode crearNodoVisual(ElementoSistema elemento) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(elemento);
        if (elemento instanceof Directorio) {
            Directorio dir = (Directorio) elemento;
            for (int i = 0; i < dir.obtenerHijos().obtenerTamano(); i++) {
                ElementoSistema hijo = dir.obtenerHijos().obtener(i);
                nodo.add(crearNodoVisual(hijo));
            }
        }
        return nodo;
    }

    private void actualizarDisco() {
        panelDisco.removeAll();
        int totalBloques = gestor.obtenerDisco().obtenerCantidadBloques();
        panelDisco.setLayout(new GridLayout(10, 10, 2, 2));

        for (int i = 0; i < totalBloques; i++) {
            Bloque b = gestor.obtenerDisco().obtenerBloque(i);
            JPanel panelBloque = new JPanel(new BorderLayout());
            panelBloque.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            JLabel lblId = new JLabel(String.valueOf(b.obtenerId()), SwingConstants.CENTER);
            lblId.setForeground(Color.WHITE);
            panelBloque.add(lblId, BorderLayout.CENTER);

            if (b.estaOcupado()) {
                Archivo a = gestor.buscarArchivoPorNombre(b.obtenerNombreArchivo());
                if (a != null) {
                    panelBloque.setBackground(a.obtenerColor());
                    String txtSiguiente = b.obtenerSiguienteBloque() != -1 ? "->" + b.obtenerSiguienteBloque() : "Fin";
                    JLabel lblSig = new JLabel(txtSiguiente, SwingConstants.CENTER);
                    lblSig.setForeground(Color.WHITE);
                    lblSig.setFont(new Font("Arial", Font.BOLD, 10));
                    panelBloque.add(lblSig, BorderLayout.SOUTH);
                }
            } else {
                panelBloque.setBackground(Color.GRAY);
            }
            panelDisco.add(panelBloque);
        }
        panelDisco.revalidate();
        panelDisco.repaint();
    }

    private void actualizarTabla() {
        String[] columnas = {"Nombre Archivo", "Bloques Asignados", "Primer Bloque", "Color (RGB)"};
        estructuras.Lista<Archivo> archivos = gestor.obtenerTodosLosArchivos();
        Object[][] datos = new Object[archivos.obtenerTamano()][4];

        for (int i = 0; i < archivos.obtenerTamano(); i++) {
            Archivo a = archivos.obtener(i);
            datos[i][0] = a.obtenerNombre();
            datos[i][1] = a.obtenerTamano();
            datos[i][2] = a.obtenerBloqueInicial();
            Color c = a.obtenerColor();
            datos[i][3] = "RGB(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")";
        }

        tablaAsignacion.setModel(new DefaultTableModel(datos, columnas));
    }

    private void expandirTodoElArbol() {
        for (int i = 0; i < arbolSistema.getRowCount(); i++) {
            arbolSistema.expandRow(i);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}