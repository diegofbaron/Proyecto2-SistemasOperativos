/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import java.awt.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import modelo.*;

public class VentanaPrincipal extends JFrame {
    
    // --- Lógica ---
    private GestorSistemaArchivos gestor;

    // --- Componentes Gráficos ---
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

        // --- 1. Panel Izquierdo: JTree ---
        arbolSistema = new JTree();
        JScrollPane scrollArbol = new JScrollPane(arbolSistema);
        scrollArbol.setPreferredSize(new Dimension(250, 0));
        scrollArbol.setBorder(BorderFactory.createTitledBorder("Estructura de Directorios"));
        add(scrollArbol, BorderLayout.WEST);

        // --- 2. Panel Central: Disco y Tabla ---
        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        
        panelDisco = new JPanel();
        panelDisco.setBackground(Color.DARK_GRAY);
        panelDisco.setBorder(BorderFactory.createTitledBorder("Simulación del SD (Bloques)"));
        panelCentral.add(new JScrollPane(panelDisco));

        String[] columnas = {"Nombre Archivo", "Bloques Asignados", "Primer Bloque"};
        Object[][] datosVacios = {};
        tablaAsignacion = new JTable(datosVacios, columnas);
        JScrollPane scrollTabla = new JScrollPane(tablaAsignacion);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Tabla de Asignación"));
        panelCentral.add(scrollTabla);

        add(panelCentral, BorderLayout.CENTER);

        // --- 3. Panel Inferior: Controles ---
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        comboModoUsuario = new JComboBox<>(new String[]{"Modo Administrador", "Modo Usuario"});
        btnCrear = new JButton("Crear Archivo/Directorio");
        btnEliminar = new JButton("Eliminar");

        panelControles.add(new JLabel("Modo:"));
        panelControles.add(comboModoUsuario);
        panelControles.add(btnCrear);
        panelControles.add(btnEliminar);

        add(panelControles, BorderLayout.SOUTH);

        // --- 4. EVENTOS DE LOS BOTONES ---
        btnCrear.addActionListener(e -> accionCrearElemento());

        actualizarArbol();
    }

    private void accionCrearElemento() {
        // Verificar que estemos en Modo Administrador
        if (comboModoUsuario.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden crear elementos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener la carpeta seleccionada en el JTree
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un directorio en el árbol donde crear el elemento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ElementoSistema elementoPadre = (ElementoSistema) nodoSeleccionado.getUserObject();
        if (!(elementoPadre instanceof Directorio)) {
            JOptionPane.showMessageDialog(this, "No puedes crear un elemento dentro de un archivo. Selecciona una carpeta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Directorio directorioPadre = (Directorio) elementoPadre;

        // Preguntar qué quiere crear
        String[] opciones = {"Archivo", "Directorio"};
        int seleccion = JOptionPane.showOptionDialog(this, "¿Qué deseas crear?", "Crear Elemento",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == -1) return; // Canceló

        String nombre = JOptionPane.showInputDialog(this, "Nombre del elemento:");
        if (nombre == null || nombre.trim().isEmpty()) return;

        if (seleccion == 0) { // Eligió crear Archivo
            String tamanoStr = JOptionPane.showInputDialog(this, "Tamaño en bloques (ej. 5):");
            try {
                int tamano = Integer.parseInt(tamanoStr);
                if (tamano <= 0) throw new NumberFormatException();
                
                // Pedimos al gestor que cree el proceso
                Proceso p = gestor.solicitarCreacionArchivo(nombre, "admin", directorioPadre, tamano);
                gestor.ejecutarProceso(p); // Por ahora lo ejecutamos instantáneamente
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El tamaño debe ser un número entero positivo.", "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else { // Eligió crear Directorio
            Directorio nuevoDir = new Directorio(nombre, "admin", directorioPadre);
            directorioPadre.agregarHijo(nuevoDir);
        }

        actualizarArbol(); // Refrescamos la vista
        expandirTodoElArbol(); // Para que no se cierre la carpeta al actualizar
    }

    private void actualizarArbol() {
        Directorio raizModelo = gestor.obtenerRaiz();
        DefaultMutableTreeNode raizVisual = crearNodoVisual(raizModelo);
        arbolSistema.setModel(new DefaultTreeModel(raizVisual));
    }

    private DefaultMutableTreeNode crearNodoVisual(ElementoSistema elemento) {
        // Ahora guardamos el OBJETO, no solo el String
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