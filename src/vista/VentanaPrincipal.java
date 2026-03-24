/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    
    private StringBuilder logEventosSistema = new StringBuilder();

    public VentanaPrincipal() {
        gestor = new GestorSistemaArchivos(100); 
        gestor.configurarPlanificador(PoliticaPlanificacion.FIFO, 50, true);
        registrarEventoSistema("Sistema iniciado. Disco virtual de 100 bloques montado.");

        setTitle("Simulador de Sistema de Archivos - Proyecto 2");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 244, 248)); 

        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 14);
        Font fuenteTitulos = new Font("Segoe UI", Font.BOLD, 13);

        arbolSistema = new JTree();
        arbolSistema.setFont(fuenteGeneral);
        JScrollPane scrollArbol = new JScrollPane(arbolSistema);
        scrollArbol.setPreferredSize(new Dimension(250, 0));
        scrollArbol.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), 
            "Estructura de Directorios", 0, 0, fuenteTitulos, new Color(30, 30, 30)));
        add(scrollArbol, BorderLayout.WEST);

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentral.setBackground(new Color(240, 244, 248));
        
        panelDisco = new JPanel();
        panelDisco.setBackground(new Color(40, 44, 52)); 
        JScrollPane scrollDisco = new JScrollPane(panelDisco);
        scrollDisco.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), 
            "Mapa del Disco (Bloques)", 0, 0, fuenteTitulos, new Color(30, 30, 30)));
        panelCentral.add(scrollDisco);

        tablaAsignacion = new JTable();
        tablaAsignacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaAsignacion.setRowHeight(25);
        tablaAsignacion.setFillsViewportHeight(true);
        JScrollPane scrollTabla = new JScrollPane(tablaAsignacion);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), 
            "Tabla de Asignacion de Archivos", 0, 0, fuenteTitulos, new Color(30, 30, 30)));
        panelCentral.add(scrollTabla);

        JTabbedPane panelMonitoreo = crearPanelMonitoreo();
        panelMonitoreo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCentral, panelMonitoreo);
        splitPrincipal.setResizeWeight(0.70);
        splitPrincipal.setDividerLocation(720);
        splitPrincipal.setBorder(null);
        add(splitPrincipal, BorderLayout.CENTER);

        JPanel panelControles = new JPanel(new GridLayout(2, 1, 0, 8));
        panelControles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelControles.setBackground(Color.WHITE);
        
        JPanel filaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filaSuperior.setBackground(Color.WHITE);
        JPanel filaInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filaInferior.setBackground(Color.WHITE);
        
        comboModoUsuario = new JComboBox<>(new String[]{"Modo Administrador", "Modo Usuario"});
        btnCrear = new JButton("Crear");
        btnRenombrar = new JButton("Renombrar");
        btnEliminar = new JButton("Eliminar FS"); 
        btnSolicitarProceso = new JButton("Solicitar E/S");
        
        comboPolitica = new JComboBox<>(PoliticaPlanificacion.values());
        spinnerCabezalInicial = new JSpinner(new SpinnerNumberModel(50, 0, gestor.obtenerDisco().obtenerCantidadBloques() - 1, 1));
        chkDireccionAscendente = new JCheckBox("Direccion Asc.", true);
        chkDireccionAscendente.setBackground(Color.WHITE);
        
        btnAplicarPlanificador = new JButton("Ejecutar Planificador");
        btnAplicarPlanificador.setBackground(new Color(33, 87, 164));
        btnAplicarPlanificador.setForeground(Color.BLACK);
        btnAplicarPlanificador.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAplicarPlanificador.setFocusPainted(false);
        btnAplicarPlanificador.setContentAreaFilled(true);
        btnAplicarPlanificador.setOpaque(true); 
        
        txtUsuario = new JTextField("admin", 8);
        btnAplicarSesion = new JButton("Aplicar Sesion");
        btnGuardarEstado = new JButton("Guardar JSON");
        btnCargarEstado = new JButton("Cargar JSON");
        
        btnSimularFallo = new JButton("Simular Fallo: OFF");
        btnSimularFallo.setForeground(Color.RED);
        btnSimularFallo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        btnRecuperarJournal = new JButton("Recuperar Journal");
        btnActualizarMonitoreo = new JButton("Actualizar");
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
        filaSuperior.add(new JLabel(" | "));
        filaSuperior.add(btnCrear);
        filaSuperior.add(btnRenombrar);
        filaSuperior.add(btnEliminar);
        filaSuperior.add(btnSolicitarProceso);
        filaSuperior.add(new JLabel(" | "));
        filaSuperior.add(btnGuardarEstado);
        filaSuperior.add(btnCargarEstado);

        filaInferior.add(new JLabel("Politica E/S:"));
        filaInferior.add(comboPolitica);
        filaInferior.add(new JLabel("Cabezal:"));
        filaInferior.add(spinnerCabezalInicial);
        filaInferior.add(chkDireccionAscendente);
        filaInferior.add(btnAplicarPlanificador);
        filaInferior.add(new JLabel(" | "));
        filaInferior.add(btnSimularFallo);
        filaInferior.add(btnRecuperarJournal);
        filaInferior.add(btnActualizarMonitoreo);
        filaInferior.add(btnValidarPruebas);

        JPanel panelStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 8));
        panelStatus.setBackground(new Color(230, 235, 240));
        panelStatus.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        
        lblPoliticaActiva.setFont(fuenteTitulos);
        lblCabezalActual.setFont(fuenteTitulos);
        lblDesplazamiento.setFont(fuenteTitulos);
        lblUltimaActualizacion.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        
        panelStatus.add(lblPoliticaActiva);
        panelStatus.add(lblCabezalActual);
        panelStatus.add(lblDesplazamiento);
        panelStatus.add(lblUltimaActualizacion);

        JPanel panelSurCompleto = new JPanel(new BorderLayout());
        panelSurCompleto.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panelControles.add(filaSuperior);
        panelControles.add(filaInferior);
        panelSurCompleto.add(panelControles, BorderLayout.CENTER);
        panelSurCompleto.add(panelStatus, BorderLayout.SOUTH);

        add(panelSurCompleto, BorderLayout.SOUTH);

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
    
    private void registrarEventoSistema(String mensaje) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logEventosSistema.insert(0, "[" + hora + "] > " + mensaje + "\n");
    }

    private void accionCrearElemento() {
        if (comboModoUsuario.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden crear elementos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un directorio en el arbol.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ElementoSistema elementoPadre = (ElementoSistema) nodoSeleccionado.getUserObject();
        if (!(elementoPadre instanceof Directorio)) {
            JOptionPane.showMessageDialog(this, "No puedes crear un elemento dentro de un archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Directorio directorioPadre = (Directorio) elementoPadre;
        String[] opciones = {"Archivo", "Directorio"};
        int seleccion = JOptionPane.showOptionDialog(this, "Que deseas crear?", "Crear Elemento",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == -1) return; 

        String nombre = JOptionPane.showInputDialog(this, "Nombre del elemento:");
        if (nombre == null) return;

        String error;
        if (seleccion == 0) {
            String tamanoStr = JOptionPane.showInputDialog(this, "Tamano en bloques:");
            try {
                int tamano = Integer.parseInt(tamanoStr);
                int visibilidad = JOptionPane.showConfirmDialog(this, "Archivo publico?", "Visibilidad", JOptionPane.YES_NO_OPTION);
                boolean publico = visibilidad == JOptionPane.YES_OPTION;
                error = gestor.crearArchivo(nombre, txtUsuario.getText(), directorioPadre, tamano, publico);
                if(error == null) registrarEventoSistema("Archivo creado: " + nombre + " (" + tamano + " bloques)");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El tamano debe ser un entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            error = gestor.crearDirectorio(nombre, txtUsuario.getText(), directorioPadre);
            if(error == null) registrarEventoSistema("Directorio creado: " + nombre);
        }

        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        refrescarVistaCompleta(true);
    }

    private void accionRenombrarElemento() {
        if (comboModoUsuario.getSelectedIndex() != 0) return;
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) return;

        ElementoSistema elemento = (ElementoSistema) nodoSeleccionado.getUserObject();
        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", elemento.obtenerNombre());
        if (nuevoNombre == null) return;

        String error = gestor.renombrarElemento(elemento, nuevoNombre);
        if (error == null) {
            registrarEventoSistema("Elemento renombrado a: " + nuevoNombre);
            refrescarVistaCompleta(true);
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionAplicarPlanificador() {
        PoliticaPlanificacion politica = (PoliticaPlanificacion) comboPolitica.getSelectedItem();
        int posicionInicial = (Integer) spinnerCabezalInicial.getValue();
        boolean direccionAsc = chkDireccionAscendente.isSelected();

        gestor.configurarPlanificador(politica, posicionInicial, direccionAsc);
        registrarEventoSistema("Planificador configurado -> Politica: " + politica + " | Cabezal: " + posicionInicial);
        
        int procesosDespachados = gestor.despacharTodosLosProcesosPendientes();
        refrescarVistaCompleta(false);
        
        if(procesosDespachados > 0) {
            registrarEventoSistema("Planificador ejecuto " + procesosDespachados + " procesos con exito.");
            JOptionPane.showMessageDialog(this, "Se ejecutaron " + procesosDespachados + " procesos.\nRevisa la pestana 'Terminal / Journal' para el detalle.", "Planificador", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "El planificador esta listo, pero la Cola de E/S esta vacia.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionToggleFallo() {
        boolean activar = !gestor.estaSimulacionFalloActiva();
        gestor.configurarSimulacionFallo(activar);
        if(activar) {
            btnSimularFallo.setText("Simular Fallo: ON");
            btnSimularFallo.setForeground(Color.RED);
            registrarEventoSistema("[!] ATENCION: Simulacion de fallos ACTIVADA.");
        } else {
            btnSimularFallo.setText("Simular Fallo: OFF");
            btnSimularFallo.setForeground(Color.GRAY);
            registrarEventoSistema("Simulacion de fallos DESACTIVADA.");
        }
        refrescarVistaCompleta(false);
    }

    private void accionRecuperarJournal() {
        int recuperadas = gestor.ejecutarRecuperacionJournalPendientes();
        registrarEventoSistema("Recuperacion de Journal ejecutada. Entradas reparadas: " + recuperadas);
        refrescarVistaCompleta(true);
        JOptionPane.showMessageDialog(this, "Recuperacion finalizada. Transacciones restauradas: " + recuperadas, "Journal", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionValidarPruebas() {
        estructuras.Lista<String> reporte = gestor.generarReportePruebasRecomendadas();
        areaPruebas.setText(textoDesdeLista(reporte, "Sin resultados."));
        JOptionPane.showMessageDialog(this, "Pruebas recomendadas ejecutadas.", "Validacion", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionGuardarEstado() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar estado en JSON");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String error = gestor.guardarEstadoEnJson(chooser.getSelectedFile().getAbsolutePath());
            if (error != null) JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Guardado exitoso.");
        }
    }

    private void accionCargarEstado() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar estado desde JSON");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String error = gestor.cargarEstadoDesdeJson(chooser.getSelectedFile().getAbsolutePath());
            if (error != null) JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            else refrescarVistaCompleta(true);
        }
    }

    private void accionEliminarElemento() {
        if (comboModoUsuario.getSelectedIndex() != 0) return;
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodo == null) return;
        ElementoSistema elemento = (ElementoSistema) nodo.getUserObject();
        
        if (elemento.obtenerPadre() == null) return;
        
        int confirmacion = JOptionPane.showConfirmDialog(this, "Estas seguro de que deseas eliminar '" + elemento.obtenerNombre() + "' directamente del FS?", "Confirmar Eliminacion Directa", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            String error = gestor.eliminarElementoSeguro(elemento);
            if (error == null) {
                registrarEventoSistema("Elemento eliminado directamente (FS): " + elemento.obtenerNombre());
                refrescarVistaCompleta(true); 
            }
        }
    }

    private void accionSolicitarProceso() {
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null) return;

        Object userObject = nodoSeleccionado.getUserObject();
        if (!(userObject instanceof Archivo)) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Archivo para E/S.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean modoAdmin = comboModoUsuario.getSelectedIndex() == 0;
        TipoOperacion operacion;
        if (modoAdmin) {
            TipoOperacion[] opciones = {TipoOperacion.LEER, TipoOperacion.ACTUALIZAR, TipoOperacion.ELIMINAR};
            operacion = (TipoOperacion) JOptionPane.showInputDialog(this, "Selecciona la operacion E/S para enviar a la cola:", "Solicitud", JOptionPane.QUESTION_MESSAGE, null, opciones, TipoOperacion.LEER);
            if (operacion == null) return;
        } else {
            operacion = TipoOperacion.LEER;
        }

        Archivo archivo = (Archivo) userObject;
        String error = gestor.solicitarOperacionArchivo(archivo.obtenerNombre(), operacion);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Solicitud rechazada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        registrarEventoSistema("Nueva solicitud E/S encolada: [" + operacion + "] sobre " + archivo.obtenerNombre());
        refrescarVistaCompleta(true);
    }

    private void aplicarSesionActual() {
        boolean modoAdmin = comboModoUsuario.getSelectedIndex() == 0;
        String usuario = txtUsuario.getText();
        gestor.configurarSesion(usuario, modoAdmin);
        txtUsuario.setText(gestor.obtenerUsuarioActual());
        registrarEventoSistema("Cambio de sesion: " + usuario + " (" + (modoAdmin ? "Admin" : "Usuario") + ")");
    }

    private void actualizarEstadoPlanificador() {
        lblPoliticaActiva.setText("Politica activa: " + gestor.obtenerPoliticaActiva());
        lblCabezalActual.setText("Cabezal actual: " + gestor.obtenerPosicionCabezal());
        lblDesplazamiento.setText("Desplazamiento Total: " + gestor.obtenerDesplazamientoCabezal());
        lblUltimaActualizacion.setText("Actualizado: " + java.time.LocalTime.now().withNano(0));
    }

    private JTabbedPane crearPanelMonitoreo() {
        areaChecklist = crearAreaMonitoreo(Color.BLACK, Color.GREEN);
        areaSeleccion = crearAreaMonitoreo(Color.WHITE, Color.BLACK);
        areaCola = crearAreaMonitoreo(new Color(255, 250, 205), Color.BLACK); 
        areaHistorial = crearAreaMonitoreo(Color.WHITE, Color.DARK_GRAY);
        areaLocks = crearAreaMonitoreo(new Color(255, 228, 225), new Color(180, 0, 0));  
        areaJournal = crearAreaMonitoreo(new Color(15, 15, 15), new Color(0, 200, 255)); 
        areaPruebas = crearAreaMonitoreo(Color.WHITE, Color.BLACK);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Terminal / Journal", new JScrollPane(areaJournal));
        tabs.addTab("Cola Espera", new JScrollPane(areaCola));
        tabs.addTab("Historial E/S", new JScrollPane(areaHistorial));
        tabs.addTab("Detalles", new JScrollPane(areaSeleccion));
        tabs.addTab("Locks", new JScrollPane(areaLocks));
        tabs.addTab("Pruebas", new JScrollPane(areaPruebas));
        tabs.setPreferredSize(new Dimension(360, 0));
        return tabs;
    }

    private JTextArea crearAreaMonitoreo(Color fondo, Color texto) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        area.setBackground(fondo);
        area.setForeground(texto);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return area;
    }

    private void actualizarPanelMonitoreo() {
        areaCola.setText("=== PROCESOS ESPERANDO EN COLA ===\n\n" + textoDesdeLista(gestor.obtenerResumenColaProcesos(), ">>> La cola esta vacia."));
        areaHistorial.setText("=== ULTIMOS PROCESOS EJECUTADOS ===\n\n" + textoDesdeLista(gestor.obtenerResumenHistorialProcesos(30), ">>> Sin historial."));
        areaLocks.setText("=== ESTADO DE BLOQUEOS (LOCKS) ===\n\n" + textoDesdeLista(gestor.obtenerResumenLocksActivos(), "Ningun archivo bloqueado actualmente."));
        areaPruebas.setText(textoDesdeLista(gestor.generarReportePruebasRecomendadas(), "Sin resultados."));
        
        String journalReal = textoDesdeLista(gestor.obtenerResumenJournal(), ">>> Journal del sistema vacio.");
        areaJournal.setText(
            "============================================\n" +
            "      LOG DE EVENTOS Y JOURNAL SYSTEM       \n" +
            "============================================\n\n" +
            "--- EVENTOS DE INTERFAZ Y PLANIFICADOR ---\n" +
            logEventosSistema.toString() + "\n" +
            "--- TRANSACCIONES DE DISCO (JOURNAL) ---\n" +
            journalReal
        );
        actualizarInfoSeleccionado();
    }

    private void actualizarInfoSeleccionado() {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodo == null) {
            areaSeleccion.setText("Haz clic en un elemento del arbol para ver sus detalles.");
            return;
        }
        ElementoSistema elemento = (ElementoSistema) nodo.getUserObject();
        String tipo = (elemento instanceof Directorio) ? "Directorio" : "Archivo";
        areaSeleccion.setText(
            "=== DETALLES DEL ELEMENTO ===\n\n" +
            "Tipo: " + tipo + "\n" +
            "Nombre: " + elemento.obtenerNombre() + "\n" +
            "Propietario: " + elemento.obtenerDueno() + "\n" +
            "Tamano: " + elemento.obtenerTamano() + " bloques\n" +
            "Permisos: " + (elemento.esPublico() ? "Publico (Lectura)" : "Privado")
        );
    }

    private String textoDesdeLista(estructuras.Lista<String> lista, String vacio) {
        if (lista == null || lista.obtenerTamano() == 0) return vacio;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.obtenerTamano(); i++) {
            sb.append("> ").append(lista.obtener(i)).append("\n");
        }
        return sb.toString();
    }

    private void refrescarVistaCompleta(boolean expandirArbol) {
        actualizarArbol();
        actualizarDisco();
        actualizarTabla();
        actualizarEstadoPlanificador();
        actualizarPanelMonitoreo();
        if (expandirArbol) expandirTodoElArbol();
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
                nodo.add(crearNodoVisual(dir.obtenerHijos().obtener(i)));
            }
        }
        return nodo;
    }

    private void actualizarDisco() {
        panelDisco.removeAll();
        int totalBloques = gestor.obtenerDisco().obtenerCantidadBloques();
        panelDisco.setLayout(new GridLayout(10, 10, 4, 4));

        for (int i = 0; i < totalBloques; i++) {
            Bloque b = gestor.obtenerDisco().obtenerBloque(i);
            JPanel panelBloque = new JPanel(new BorderLayout());
            panelBloque.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20), 1));
            
            JLabel lblId = new JLabel(String.valueOf(b.obtenerId()), SwingConstants.CENTER);
            lblId.setForeground(new Color(200, 200, 200));
            lblId.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            panelBloque.add(lblId, BorderLayout.CENTER);

            if (b.estaOcupado()) {
                Archivo a = gestor.buscarArchivoPorNombre(b.obtenerNombreArchivo());
                if (a != null) {
                    panelBloque.setBackground(a.obtenerColor());
                    lblId.setForeground(Color.WHITE);
                    String txtProceso = b.obtenerProcesoOcupante() >= 0 ? "P" + b.obtenerProcesoOcupante() : "";
                    JLabel lblProceso = new JLabel(txtProceso, SwingConstants.CENTER);
                    lblProceso.setForeground(Color.WHITE);
                    lblProceso.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    panelBloque.add(lblProceso, BorderLayout.NORTH);
                }
            } else {
                panelBloque.setBackground(new Color(70, 75, 80)); 
            }
            panelDisco.add(panelBloque);
        }
        panelDisco.revalidate();
        panelDisco.repaint();
    }

    private void actualizarTabla() {
        String[] columnas = {"Archivo", "Bloques", "Inicio", "Color"};
        estructuras.Lista<Archivo> archivos = gestor.obtenerTodosLosArchivos();
        Object[][] datos = new Object[archivos.obtenerTamano()][4];

        for (int i = 0; i < archivos.obtenerTamano(); i++) {
            Archivo a = archivos.obtener(i);
            datos[i][0] = a.obtenerNombre();
            datos[i][1] = a.obtenerTamano();
            datos[i][2] = a.obtenerBloqueInicial();
            
            // VOLVEMOS AL FORMATO RGB SEGURO EN TEXTO:
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
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}