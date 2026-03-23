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
    private JButton btnRenombrar;
    private JButton btnEliminar;
    private JButton btnSolicitarProceso;
    private JComboBox<PoliticaPlanificacion> comboPolitica;
    private JSpinner spinnerCabezalInicial;
    private JCheckBox chkDireccionAscendente;
    private JButton btnAplicarPlanificador;
    private JTextField txtUsuario;
    private JButton btnAplicarSesion;
    private JButton btnGuardarEstado;
    private JButton btnCargarEstado;
    private JButton btnSimularFallo;
    private JButton btnRecuperarJournal;
    private JButton btnActualizarMonitoreo;
    private JButton btnValidarPruebas;
    private JLabel lblPoliticaActiva;
    private JLabel lblCabezalActual;
    private JLabel lblDesplazamiento;
    private JTextArea areaCola;
    private JTextArea areaHistorial;
    private JTextArea areaLocks;
    private JTextArea areaJournal;
    private JTextArea areaPruebas;
    private JTextArea areaChecklist;
    private JTextArea areaSeleccion;
    private JLabel lblUltimaActualizacion;

    public VentanaPrincipal() {
        gestor = new GestorSistemaArchivos(100); 
        gestor.configurarPlanificador(PoliticaPlanificacion.FIFO, 50, true);

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

        JTabbedPane panelMonitoreo = crearPanelMonitoreo();
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCentral, panelMonitoreo);
        splitPrincipal.setResizeWeight(0.72);
        splitPrincipal.setDividerLocation(720);
        add(splitPrincipal, BorderLayout.CENTER);

        JPanel panelControles = new JPanel(new GridLayout(2, 1, 0, 4));
        JPanel filaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JPanel filaInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        comboModoUsuario = new JComboBox<>(new String[]{"Modo Administrador", "Modo Usuario"});
        btnCrear = new JButton("Crear Archivo/Directorio");
        btnRenombrar = new JButton("Renombrar");
        btnEliminar = new JButton("Eliminar");
        btnSolicitarProceso = new JButton("Solicitar E/S");
        comboPolitica = new JComboBox<>(PoliticaPlanificacion.values());
        spinnerCabezalInicial = new JSpinner(new SpinnerNumberModel(50, 0, gestor.obtenerDisco().obtenerCantidadBloques() - 1, 1));
        chkDireccionAscendente = new JCheckBox("Dirección ↑", true);
        btnAplicarPlanificador = new JButton("Aplicar Planificador");
        txtUsuario = new JTextField("admin", 8);
        btnAplicarSesion = new JButton("Aplicar Sesión");
        btnGuardarEstado = new JButton("Guardar JSON");
        btnCargarEstado = new JButton("Cargar JSON");
        btnSimularFallo = new JButton("Simular Fallo: OFF");
        btnRecuperarJournal = new JButton("Recuperar Journal");
        btnActualizarMonitoreo = new JButton("Actualizar Monitoreo");
        btnValidarPruebas = new JButton("Validar Pruebas");
        lblPoliticaActiva = new JLabel();
        lblCabezalActual = new JLabel();
        lblDesplazamiento = new JLabel();
        lblUltimaActualizacion = new JLabel();

        filaSuperior.add(new JLabel("Modo:"));
        filaSuperior.add(comboModoUsuario);
        filaSuperior.add(new JLabel("Usuario:"));
        filaSuperior.add(txtUsuario);
        filaSuperior.add(btnAplicarSesion);
        filaSuperior.add(btnCrear);
        filaSuperior.add(btnRenombrar);
        filaSuperior.add(btnEliminar);
        filaSuperior.add(btnSolicitarProceso);
        filaSuperior.add(btnGuardarEstado);
        filaSuperior.add(btnCargarEstado);

        filaInferior.add(new JLabel("Política:"));
        filaInferior.add(comboPolitica);
        filaInferior.add(new JLabel("Cabezal inicial:"));
        filaInferior.add(spinnerCabezalInicial);
        filaInferior.add(chkDireccionAscendente);
        filaInferior.add(btnAplicarPlanificador);
        filaInferior.add(btnSimularFallo);
        filaInferior.add(btnRecuperarJournal);
        filaInferior.add(btnActualizarMonitoreo);
        filaInferior.add(btnValidarPruebas);
        filaInferior.add(lblPoliticaActiva);
        filaInferior.add(lblCabezalActual);
        filaInferior.add(lblDesplazamiento);
        filaInferior.add(lblUltimaActualizacion);

        panelControles.add(filaSuperior);
        panelControles.add(filaInferior);

        add(panelControles, BorderLayout.SOUTH);

        btnCrear.addActionListener(e -> accionCrearElemento());
        btnRenombrar.addActionListener(e -> accionRenombrarElemento());
        btnEliminar.addActionListener(e -> accionEliminarElemento());
        btnSolicitarProceso.addActionListener(e -> accionSolicitarProceso());
        btnAplicarPlanificador.addActionListener(e -> accionAplicarPlanificador());
        btnAplicarSesion.addActionListener(e -> aplicarSesionActual());
        comboModoUsuario.addActionListener(e -> aplicarSesionActual());
        btnGuardarEstado.addActionListener(e -> accionGuardarEstado());
        btnCargarEstado.addActionListener(e -> accionCargarEstado());
        btnSimularFallo.addActionListener(e -> accionToggleFallo());
        btnRecuperarJournal.addActionListener(e -> accionRecuperarJournal());
        btnActualizarMonitoreo.addActionListener(e -> refrescarVistaCompleta(false));
        btnValidarPruebas.addActionListener(e -> accionValidarPruebas());
        arbolSistema.addTreeSelectionListener(e -> actualizarInfoSeleccionado());

        aplicarSesionActual();
        refrescarVistaCompleta(true);
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
        if (nombre == null) return;

        String error;
        if (seleccion == 0) {
            String tamanoStr = JOptionPane.showInputDialog(this, "Tamaño en bloques:");
            try {
                int tamano = Integer.parseInt(tamanoStr);
                int visibilidad = JOptionPane.showConfirmDialog(this, "¿Archivo público?", "Visibilidad", JOptionPane.YES_NO_OPTION);
                boolean publico = visibilidad == JOptionPane.YES_OPTION;
                error = gestor.crearArchivo(nombre, txtUsuario.getText(), directorioPadre, tamano, publico);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El tamaño debe ser un entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            error = gestor.crearDirectorio(nombre, txtUsuario.getText(), directorioPadre);
        }

        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarVistaCompleta(true);
    }

    private void accionRenombrarElemento() {
        if (comboModoUsuario.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden renombrar elementos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un elemento para renombrar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ElementoSistema elemento = (ElementoSistema) nodoSeleccionado.getUserObject();
        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", elemento.obtenerNombre());
        if (nuevoNombre == null) {
            return;
        }

        String error = gestor.renombrarElemento(elemento, nuevoNombre);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarVistaCompleta(true);
    }

    private void accionAplicarPlanificador() {
        PoliticaPlanificacion politica = (PoliticaPlanificacion) comboPolitica.getSelectedItem();
        int posicionInicial = (Integer) spinnerCabezalInicial.getValue();
        boolean direccionAsc = chkDireccionAscendente.isSelected();

        gestor.configurarPlanificador(politica, posicionInicial, direccionAsc);
        refrescarVistaCompleta(false);
    }

    private void accionToggleFallo() {
        boolean activar = !gestor.estaSimulacionFalloActiva();
        gestor.configurarSimulacionFallo(activar);
        refrescarVistaCompleta(false);
    }

    private void accionRecuperarJournal() {
        int recuperadas = gestor.ejecutarRecuperacionJournalPendientes();
        refrescarVistaCompleta(true);
        JOptionPane.showMessageDialog(this, "Recuperación finalizada. Entradas procesadas: " + recuperadas, "Journal", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionValidarPruebas() {
        estructuras.Lista<String> reporte = gestor.generarReportePruebasRecomendadas();
        areaPruebas.setText(textoDesdeLista(reporte, "Sin resultados de pruebas."));
        JOptionPane.showMessageDialog(this, "Validación de pruebas recomendadas completada.", "Paso 11", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionGuardarEstado() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar estado en JSON");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String error = gestor.guardarEstadoEnJson(chooser.getSelectedFile().getAbsolutePath());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Estado guardado correctamente.", "Guardar JSON", JOptionPane.INFORMATION_MESSAGE);
        refrescarVistaCompleta(false);
    }

    private void accionCargarEstado() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar estado desde JSON");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String error = gestor.cargarEstadoDesdeJson(chooser.getSelectedFile().getAbsolutePath());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        txtUsuario.setText(gestor.obtenerUsuarioActual());
        comboModoUsuario.setSelectedIndex(gestor.esModoAdministrador() ? 0 : 1);
        comboPolitica.setSelectedItem(gestor.obtenerPoliticaActiva());
        spinnerCabezalInicial.setValue(gestor.obtenerPosicionCabezal());
        chkDireccionAscendente.setSelected(gestor.esDireccionAscendente());

        refrescarVistaCompleta(true);

        int recuperadas = gestor.ejecutarRecuperacionJournalPendientes();
        if (recuperadas > 0) {
            refrescarVistaCompleta(true);
            JOptionPane.showMessageDialog(this, "Se recuperaron " + recuperadas + " transacciones pendientes del journal.", "Recuperación automática", JOptionPane.INFORMATION_MESSAGE);
        }

        JOptionPane.showMessageDialog(this, "Estado cargado correctamente.", "Cargar JSON", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionEliminarElemento() {
        if (comboModoUsuario.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden eliminar elementos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un elemento para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ElementoSistema elemento = (ElementoSistema) nodoSeleccionado.getUserObject();

        if (elemento.obtenerPadre() == null) {
            JOptionPane.showMessageDialog(this, "No puedes eliminar el directorio Raíz del sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar '" + elemento.obtenerNombre() + "'?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String error = gestor.eliminarElementoSeguro(elemento);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            refrescarVistaCompleta(true);
        }
    }

    private void accionSolicitarProceso() {
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un archivo para crear la solicitud de E/S.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object userObject = nodoSeleccionado.getUserObject();
        if (!(userObject instanceof Archivo)) {
            JOptionPane.showMessageDialog(this, "La solicitud de E/S debe apuntar a un archivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean modoAdmin = comboModoUsuario.getSelectedIndex() == 0;
        TipoOperacion operacion;
        if (modoAdmin) {
            TipoOperacion[] opciones = {TipoOperacion.LEER, TipoOperacion.ACTUALIZAR, TipoOperacion.ELIMINAR};
            operacion = (TipoOperacion) JOptionPane.showInputDialog(
                    this,
                    "Selecciona la operación de E/S:",
                    "Solicitud de proceso",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    TipoOperacion.LEER
            );
            if (operacion == null) {
                return;
            }
        } else {
            operacion = TipoOperacion.LEER;
        }

        Archivo archivo = (Archivo) userObject;
        String error = gestor.solicitarOperacionArchivo(archivo.obtenerNombre(), operacion);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Solicitud rechazada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarVistaCompleta(true);
        JOptionPane.showMessageDialog(this,
                "Solicitud creada y procesada: " + operacion + " sobre " + archivo.obtenerNombre() + ".",
                "Proceso E/S",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void aplicarSesionActual() {
        boolean modoAdmin = comboModoUsuario.getSelectedIndex() == 0;
        String usuario = txtUsuario.getText();
        gestor.configurarSesion(usuario, modoAdmin);
        txtUsuario.setText(gestor.obtenerUsuarioActual());
    }

    private void actualizarEstadoPlanificador() {
        lblPoliticaActiva.setText("Política activa: " + gestor.obtenerPoliticaActiva());
        lblCabezalActual.setText("Cabezal actual: " + gestor.obtenerPosicionCabezal());
        lblDesplazamiento.setText("Desplazamiento: " + gestor.obtenerDesplazamientoCabezal());
        btnSimularFallo.setText(gestor.estaSimulacionFalloActiva() ? "Simular Fallo: ON" : "Simular Fallo: OFF");
        lblUltimaActualizacion.setText("Última actualización: " + java.time.LocalTime.now().withNano(0));
    }

    private JTabbedPane crearPanelMonitoreo() {
        areaChecklist = crearAreaMonitoreo();
        areaSeleccion = crearAreaMonitoreo();
        areaCola = crearAreaMonitoreo();
        areaHistorial = crearAreaMonitoreo();
        areaLocks = crearAreaMonitoreo();
        areaJournal = crearAreaMonitoreo();
        areaPruebas = crearAreaMonitoreo();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Checklist", new JScrollPane(areaChecklist));
        tabs.addTab("Seleccionado", new JScrollPane(areaSeleccion));
        tabs.addTab("Cola", new JScrollPane(areaCola));
        tabs.addTab("Historial", new JScrollPane(areaHistorial));
        tabs.addTab("Locks", new JScrollPane(areaLocks));
        tabs.addTab("Log", new JScrollPane(areaJournal));
        tabs.addTab("Pruebas", new JScrollPane(areaPruebas));
        tabs.setPreferredSize(new Dimension(300, 0));
        return tabs;
    }

    private JTextArea crearAreaMonitoreo() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    private void actualizarPanelMonitoreo() {
        areaChecklist.setText(construirChecklistVisual());
        areaCola.setText(textoDesdeLista(gestor.obtenerResumenColaProcesos(), "No hay procesos pendientes."));
        areaHistorial.setText(textoDesdeLista(gestor.obtenerResumenHistorialProcesos(30), "No hay historial."));
        areaLocks.setText(textoDesdeLista(gestor.obtenerResumenLocksActivos(), "No hay locks activos."));
        areaJournal.setText(construirLogConsolidado());
        areaPruebas.setText(textoDesdeLista(gestor.generarReportePruebasRecomendadas(), "Sin resultados de pruebas."));
        actualizarInfoSeleccionado();
    }

    private void actualizarInfoSeleccionado() {
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) {
            areaSeleccion.setText("Sin selección.\nSelecciona un archivo o directorio en el JTree.");
            return;
        }

        Object userObject = nodoSeleccionado.getUserObject();
        if (!(userObject instanceof ElementoSistema)) {
            areaSeleccion.setText("Elemento no válido.");
            return;
        }

        ElementoSistema elemento = (ElementoSistema) userObject;
        String tipo = (elemento instanceof Directorio) ? "Directorio" : "Archivo";
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo: ").append(tipo).append("\n");
        sb.append("Nombre: ").append(elemento.obtenerNombre()).append("\n");
        sb.append("Dueño: ").append(elemento.obtenerDueno()).append("\n");
        sb.append("Tamaño (bloques): ").append(elemento.obtenerTamano()).append("\n");
        sb.append("Público: ").append(elemento.esPublico() ? "Sí" : "No").append("\n");
        areaSeleccion.setText(sb.toString());
    }

    private String construirChecklistVisual() {
        String ok = "[OK] ";
        StringBuilder sb = new StringBuilder();
        sb.append(ok).append("JTree de directorios").append("\n");
        sb.append(ok).append("JTable FAT").append("\n");
        sb.append(ok).append("Vista de bloques de disco").append("\n");
        sb.append(ok).append("Cola de procesos").append("\n");
        sb.append(ok).append("Locks activos").append("\n");
        sb.append(ok).append("Log del sistema").append("\n");
        return sb.toString();
    }

    private String construirLogConsolidado() {
        StringBuilder sb = new StringBuilder();
        sb.append("== Últimas operaciones (historial) ==\n");
        sb.append(textoDesdeLista(gestor.obtenerResumenHistorialProcesos(15), "Sin operaciones.")).append("\n");
        sb.append("== Journal transaccional ==\n");
        sb.append(textoDesdeLista(gestor.obtenerResumenJournal(), "Sin journal."));
        return sb.toString();
    }

    private void refrescarVistaCompleta(boolean expandirArbol) {
        actualizarArbol();
        actualizarDisco();
        actualizarTabla();
        actualizarEstadoPlanificador();
        actualizarPanelMonitoreo();
        if (expandirArbol) {
            expandirTodoElArbol();
        }
    }

    private String textoDesdeLista(estructuras.Lista<String> lista, String vacio) {
        if (lista == null || lista.obtenerTamano() == 0) {
            return vacio;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.obtenerTamano(); i++) {
            sb.append(lista.obtener(i)).append("\n");
        }
        return sb.toString();
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