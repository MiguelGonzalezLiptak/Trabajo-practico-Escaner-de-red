package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import controlador.EscanerRed;
import modelo.Equipo;

public class Ventana extends JFrame implements ActionListener {

    private JTextField txtIpInicio;
    private JTextField txtIpFin;
    private JTextField txtTiempoEspera;
    private JTextField txtReintentos;
    private DefaultTableModel modeloTabla;
    private JTable tablaResultados;
    private JLabel estado;
    private JLabel EquiposActivos;
    private JButton btnIniciar;
    private JButton btnDetener;
    private JButton btnLimpiar;
    private JButton btnGuardar;
    private JButton btnMostrarActivos;
    private EscanerRed escanerControlador;
    private volatile boolean cancelado = false;
    private Thread hiloEscaneo;
    private List<Equipo> ultimosResultados;
    private boolean mostrandoSoloActivos = false;

    public Ventana() {
        escanerControlador = new EscanerRed();
        setTitle("Escaner red");
        setSize(750, 550);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        JLabel IpInicio = new JLabel("IP de inicio:");
        txtIpInicio = new JTextField(); 

        JLabel IpFin = new JLabel("IP de fin:");
        txtIpFin = new JTextField(); 

        JLabel tiempoEspera = new JLabel("Tiempo de esepra (ms):");
        txtTiempoEspera = new JTextField("1000"); 

        JLabel reintentos = new JLabel("Numero de reintentos:");
        txtReintentos = new JTextField("1"); 

        panelFormulario.add(IpInicio);
        panelFormulario.add(txtIpInicio);
        panelFormulario.add(IpFin);
        panelFormulario.add(txtIpFin);
        panelFormulario.add(tiempoEspera);
        panelFormulario.add(txtTiempoEspera);
        panelFormulario.add(reintentos);
        panelFormulario.add(txtReintentos);

        add(panelFormulario, BorderLayout.NORTH);

        String[] columnas = {"IP", "Nombre equipo", "Activo", "Tiempo(ms)"}; 
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        tablaResultados = new JTable(modeloTabla);
        JScrollPane spTabla = new JScrollPane(tablaResultados);
        add(spTabla, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));

        estado = new JLabel("Listo para escanear", SwingConstants.CENTER);
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);
        estado.setHorizontalAlignment(SwingConstants.CENTER);
        estado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JPanel panelContador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        EquiposActivos = new JLabel("Equipos activos: 0");
        panelContador.add(EquiposActivos);

        JPanel panelBotones = new JPanel(new GridLayout(1, 5, 5, 5));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnIniciar = new JButton("Iniciar escaneo");
        btnDetener = new JButton("Detener escaneo");
        btnLimpiar = new JButton("Limpiar");
        btnGuardar = new JButton("Guardar resultados");
        btnMostrarActivos = new JButton("Mostrar solo activos");

        btnIniciar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        btnMostrarActivos.addActionListener(this);
        btnDetener.addActionListener(this);

        panelBotones.add(btnIniciar);
        panelBotones.add(btnDetener);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnMostrarActivos);

        panelInferior.add(estado);
        panelInferior.add(panelContador);
        panelInferior.add(panelBotones);

        add(panelInferior, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        if (e.getSource() == btnIniciar) {
            ejecutarEscaneo();
        } else if (e.getSource() == btnLimpiar) {
            modeloTabla.setRowCount(0); 
            if (ultimosResultados != null) ultimosResultados.clear();
            mostrandoSoloActivos = false;
            btnMostrarActivos.setText("Mostrar solo activos");
            EquiposActivos.setText("Equipos activos: 0");
            estado.setText("Listo para escanear");
        } else if (e.getSource() == btnMostrarActivos) {
            if (ultimosResultados == null || ultimosResultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Realiza un escaneo primero.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            mostrandoSoloActivos = !mostrandoSoloActivos;

            if (mostrandoSoloActivos) {
                actualizarTabla(true);
                btnMostrarActivos.setText("Mostrar todos");
                estado.setText("Filtro aplicado: Solo equipos activos");
            } else {
                actualizarTabla(false);
                btnMostrarActivos.setText("Mostrar solo activos");
                estado.setText("Mostrando todos los equipos");
            }
        } else if (e.getSource() == btnDetener) {
            cancelado = true;
            estado.setText("Deteniendo escaneo...");
        }
    }

    private void actualizarTabla(boolean soloActivos) {
        if (ultimosResultados == null) return;

        modeloTabla.setRowCount(0);

        for (Equipo equipo : ultimosResultados) {
            if (!soloActivos || equipo.isActivo()) {
                modeloTabla.addRow(new Object[]{
                    equipo.getIp(),
                    equipo.getNombre(),
                    equipo.isActivo(),
                    equipo.getTiempoRespuesta()
                });
            }
        }
    }

    private void ejecutarEscaneo() {
        String ipInicio = txtIpInicio.getText().trim();
        String ipFin = txtIpFin.getText().trim();

        if (!escanerControlador.esIpValida(ipInicio) || !escanerControlador.esIpValida(ipFin)) {
            JOptionPane.showMessageDialog(this, "Las direcciones IP ingresadas no son válidas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modeloTabla.setRowCount(0);
        estado.setText("Escaneando...");
        cancelado = false;
        btnIniciar.setEnabled(false);
        mostrandoSoloActivos = false;
        btnMostrarActivos.setText("Mostrar solo activos");

        int timeoutTemp = 1000;
        try {
            timeoutTemp = Integer.parseInt(txtTiempoEspera.getText().trim());
        } catch (NumberFormatException ex) {
            timeoutTemp = 1000;
        }
        final int timeout = timeoutTemp;

        hiloEscaneo = new Thread(() -> {
            List<String> listaIps = escanerControlador.generarRangoIps(ipInicio, ipFin);
            ultimosResultados = new ArrayList<>();
            int activosCount = 0;

            for (String ip : listaIps) {
                if (cancelado) {
                    break;
                }

                Equipo equipo = escanerControlador.escanearIp(ip, timeout);
                ultimosResultados.add(equipo);

                if (equipo.isActivo()) {
                    activosCount++;
                }

                final int activosActuales = activosCount;

                SwingUtilities.invokeLater(() -> {
                    modeloTabla.addRow(new Object[]{
                        equipo.getIp(),
                        equipo.getNombre(),
                        equipo.isActivo(),
                        equipo.getTiempoRespuesta()
                    });
                    EquiposActivos.setText("Equipos activos: " + activosActuales);
                });
            }

            final boolean seCancelo = cancelado;
            SwingUtilities.invokeLater(() -> {
                if (seCancelo) {
                    estado.setText("Escaneo detenido por el usuario");
                } else {
                    estado.setText("Escaneo finalizado");
                }
                btnIniciar.setEnabled(true);
            });
        });

        hiloEscaneo.start();
    }
}