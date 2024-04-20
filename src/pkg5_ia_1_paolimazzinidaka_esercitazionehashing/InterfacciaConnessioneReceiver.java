package pkg5_ia_1_paolimazzinidaka_esercitazionehashing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Integer.parseInt;
import javax.swing.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 *
 * @author Leonardo Mazzini Drovandi & Flavio Paoli
 */

public class InterfacciaConnessioneReceiver extends JFrame implements ActionListener
{    
    private JPanel p; //Creazione pannello
    private JLabel labelPorta = new JLabel("Inserisci la porta: ");
    private JTextField campoPorta = new JTextField();
    private JButton buttonConferma = new JButton("CONFERMA");
    private FlowLayout Flow = new FlowLayout();
    private String Porta = null;
    
    public InterfacciaConnessioneReceiver()
    {
        super("Interfaccia");
        p = new JPanel();
        
        p=(JPanel)this.getContentPane();
        p.setLayout(Flow);
        campoPorta.setColumns(6);
        
        p.add(labelPorta);
        p.add(campoPorta);
        p.add(buttonConferma);
        
        buttonConferma.addActionListener(this);
        
        this.setBounds(650, 300, 250, 150);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == buttonConferma && !campoPorta.getText().isEmpty())
        {   
            while(Porta==null || Porta=="" || campoPorta.getText().isEmpty())
            {
                Porta = campoPorta.getText();
            }
            
            new algoritmoHashingReceiver(parseInt(Porta)); 
            this.dispose();
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi correttamente");
        }
    }
    
    public String getPorta()
    {
        return this.Porta;
    }
}
