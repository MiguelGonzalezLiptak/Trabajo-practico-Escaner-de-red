package vista;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Ventana extends JFrame implements ActionListener{
    
    public Ventana(){
        setSize(900, 900);
        setLayout(new BorderLayout());
        JLabel texto1 = new JLabel("IP de inicio:");
        add(texto1);

    }


}