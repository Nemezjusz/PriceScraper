import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;

public class GUI {
    final private static JComboBox<String> jlist;

    private final static String[] drinks = { "Whiskey" , "Beer"};
    private final static String[] links = {
            "https://www.globalproductprices.com/rankings/johnnie_walker_whiskey_price/",
            "https://www.globalproductprices.com/rankings/heineken_price/",};

    static {
        jlist = new JComboBox<String>(drinks);

    }
    private static final Logger log = LogManager.getLogger(GUI.class);


    public static void createAndShowGUI() {
        Thread.currentThread().setName("Thread: GUI");

        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.TRACE);
        Odczyt_Danych obj = new Odczyt_Danych();
        String data[][][] = new String[4][75][3];

        log.trace("Reading data");
        for (int j=0; j<2;j++) {
            List<Alkohol> lista_alk = obj.odczyt(links[j], drinks[j]);
            for (int i = 0; i < lista_alk.toArray().length; i++) {
                data[j][i][0] =String.valueOf(lista_alk.get(i).lp);
                data[j][i][1] =String.valueOf(lista_alk.get(i).kraj);
                data[j][i][2] =String.valueOf(lista_alk.get(i).cena);
            }
        }
        String [][] data2 = new String[75][3];
        List<Alkohol> lista_alk = obj.odczyt(links[0], drinks[0]);
        for (int i = 0; i < lista_alk.toArray().length; i++) {
            data2[i][0] = String.valueOf(lista_alk.get(i).lp);
            data2[i][1] = String.valueOf(lista_alk.get(i).kraj);
            data2[i][2] = String.valueOf(lista_alk.get(i).cena);
        }

        Thread.currentThread().setName("Thread: GUI");
        JFrame f = new JFrame("Prices of alcoholic beverages around the world");

        final JPanel control = new JPanel(new GridLayout());
        final JButton jbuttonNext = new JButton("Next");


        final JPanel dwa_kraje = new JPanel(new GridLayout());
        final JPanel kalkulator = new JPanel(new GridLayout(3,2));

        final JPanel cards = new JPanel(new CardLayout());


        final JPanel jpTabela = new JPanel();
        final JPanel jpPorownywarka = new JPanel();
        final JPanel jpWykres = new JPanel();


        HistogramPanel panel = new HistogramPanel();
        for(int k=0;k<10;k++){
            panel.addHistogramColumn(data[0][k][1],Float.valueOf(data[0][k][2]),Color.ORANGE);
        }

        panel.layoutHistogram();
        jpWykres.add(panel);


        String column[]={"RANK","COUNTRY","PRICE ($)"};
        JTable jt=new JTable(data2,column);

        JScrollPane sp=new JScrollPane(jt);
        jpTabela.add(sp);
        //porownywarka

        String [] kraje= new String[data[0].length];
        for (int k = 0;k<data[0].length;k++){
            kraje[k] = data[0][k][1];
        }

        String [] kraje1=new String[data[1].length];
        for (int k = 0;k<data[1].length;k++){
            kraje1[k] = data[1][k][1];
        }
        String [] kraje2= new String[data[2].length];
        for (int k = 0;k<data[2].length;k++){
            kraje2[k] = data[2][k][1];
        }
        String [] kraje3= new String[data[3].length];
        for (int k = 0;k<data[3].length;k++){
            kraje3[k] = data[3][k][1];
        }
        JComboBox<String> a = new JComboBox<String>(kraje);
        JComboBox<String> b = new JComboBox<String>(kraje);

        JComboBox<String> a1 = new JComboBox<String>(kraje1);
        JComboBox<String> b1 = new JComboBox<String>(kraje1);

        JComboBox<String> a2 = new JComboBox<String>(kraje2);
        JComboBox<String> b2 = new JComboBox<String>(kraje2);

        JComboBox<String> a3 = new JComboBox<String>(kraje3);
        JComboBox<String> b3 = new JComboBox<String>(kraje3);

        JTextField c = new JTextField("                      ");
        JTextField d = new JTextField("                      ");
        JTextField g = new JTextField("                      ");

        dwa_kraje.add(a);
        dwa_kraje.add(b);
        final JPanel brderPorownywarka = new JPanel(new BorderLayout());

        dwa_kraje.setBackground(Color.WHITE);

        JTextField dif = new JTextField("Price diffrence:            ");
        JTextField chp = new JTextField("Cheaper price is the:       ");
        JTextField per = new JTextField("Percentage of countries with better prices:  ");
        kalkulator.add(dif);
        kalkulator.add(c);
        kalkulator.add(chp);
        kalkulator.add(g);
        kalkulator.add(per);
        kalkulator.add(d);
        kalkulator.setBackground(Color.WHITE);

        brderPorownywarka.add(dwa_kraje, BorderLayout.NORTH);
        brderPorownywarka.add(kalkulator, BorderLayout.CENTER);

        jpPorownywarka.add(brderPorownywarka);

        ItemListener myItemListener2 = new ItemListener() {
            int i;
            int k;
            int j;
            //ustawianie po zmienieniu kraju

            public synchronized void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    i = jlist.getSelectedIndex();
                    if (i==0){
                        k = a.getSelectedIndex();
                        j = b.getSelectedIndex();
                    }
                    else if(i==1){
                        k = a1.getSelectedIndex();
                        j = b1.getSelectedIndex();
                    }
                    else if(i==2){
                        k = a2.getSelectedIndex();
                        j = b2.getSelectedIndex();
                    }
                    else {
                        k = a3.getSelectedIndex();
                        j = b3.getSelectedIndex();
                    }
                    log.trace("Selected "+data[i][k][1]+ " and "+data[i][j][1]);
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    float price = Math.abs(Float.valueOf(data[i][k][2])-Float.valueOf(data[i][j][2]));
                    c.setText(String.valueOf(df.format(price))+"$");
                    int chp;
                    if (Float.valueOf(data[i][k][2])>Float.valueOf(data[i][j][2])){
                        g.setText(data[i][j][1]);
                        chp = j;
                    }
                    else {
                        g.setText(data[i][k][1]);
                        chp = k;
                    }
                    float procent = Float.valueOf(data[i][chp][0])/(data[i].length)*100;
                    d.setText(String.valueOf(df.format(100 - procent))+"%");
                }
            }
        };

        ActionListener myActionListener = new ActionListener() {

            int i;
            public synchronized void actionPerformed(ActionEvent e) {
                i = jlist.getSelectedIndex();

                if (e.getSource().equals(jbuttonNext)) {
                    i++;
                    if (i == drinks.length)
                        i = 0;
                    jlist.setSelectedIndex(i);
                    log.trace("Next Button Clicked");
                    return;
                }

            }
        };

        MouseListener myMouseListener = new MouseListener() {

            CardLayout cl = (CardLayout) cards.getLayout();
            int i;

            public void mouseClicked(MouseEvent e) {}
            //ustawianie tablei, porownywarki oraz wykresu po kliknieciu na karte
            public synchronized void mousePressed(MouseEvent e) {
                i = jlist.getSelectedIndex();
                cl.next(cards);
                if (jpWykres.isVisible()) {
                    log.trace("Selected Table Panel for \"" + drinks[i] + "\"");
                }

                for (int k=0;k<75;k++){
                    for (int j=0;j<3;j++) {
                        jt.getModel().setValueAt(data[i][k][j],k,j);
                    }
                }

                if (jpWykres.isVisible()) {
                    log.trace("Selected Chart Panel for \""+drinks[i]+"\"");

                    panel.removeHistogram();
                    for(int k=0;k<10;k++){
                        panel.addHistogramColumn(data[i][k][1], Float.valueOf(data[i][k][2]),Color.ORANGE);
                    }
                    panel.layoutHistogram();
                    jpWykres.add(panel);

                }
                else if (jpPorownywarka.isVisible()) {
                    log.trace("Selected Comperer Panel for \"" + drinks[i] + "\"");

                }

            }

            public void mouseReleased(MouseEvent e) {}

            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}

        };

        ItemListener myItemListener = new ItemListener() {
            CardLayout cl = (CardLayout) cards.getLayout();
            int i;
            //ustawianie po zmienieniu napoju
            public synchronized void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    i = jlist.getSelectedIndex();
                    log.trace("Selected \""+drinks[i]+"\"");
                    for (int k=0;k<75;k++){
                        for (int j=0;j<3;j++) {
                            jt.getModel().setValueAt(data[i][k][j],k,j);
                        }
                    }

                    dwa_kraje.remove(a);
                    dwa_kraje.remove(b);
                    dwa_kraje.remove(a1);
                    dwa_kraje.remove(b1);
                    dwa_kraje.remove(a2);
                    dwa_kraje.remove(b2);
                    dwa_kraje.remove(a3);
                    dwa_kraje.remove(b3);
                    c.setText("");
                    d.setText("");
                    g.setText("");
                    if (i==0){
                        dwa_kraje.add(a);
                        dwa_kraje.add(b);
                    }
                    else if(i==1){
                        dwa_kraje.add(a1);
                        dwa_kraje.add(b1);
                    }
                    else if(i==2){
                        dwa_kraje.add(a2);
                        dwa_kraje.add(b2);
                    }
                    else {
                        dwa_kraje.add(a3);
                        dwa_kraje.add(b3);
                    }


                    cl.first(cards);

                }
            }
        };

        jbuttonNext.addActionListener(myActionListener);
        a.addItemListener(myItemListener2);
        b.addItemListener(myItemListener2);
        a1.addItemListener(myItemListener2);
        b1.addItemListener(myItemListener2);
        a2.addItemListener(myItemListener2);
        b2.addItemListener(myItemListener2);
        a3.addItemListener(myItemListener2);
        b3.addItemListener(myItemListener2);

        jlist.setBackground(Color.WHITE);
        jlist.setForeground(Color.BLACK);
        jlist.setFont(new Font("Verdana", Font.BOLD, 12));

        // JPanel - sterujący
        control.setBackground(Color.WHITE);
        control.add(jlist);
        control.add(jbuttonNext);

        // JPanel - składniki głównego okienko
        jpTabela.setBackground(Color.WHITE);
        jpTabela.addMouseListener(myMouseListener);
        jpTabela.setName("TABELA");


        jpWykres.setBackground(Color.WHITE);
        jpWykres.addMouseListener(myMouseListener);
        jpWykres.setName("WYKRES");

        jpPorownywarka.setBackground(Color.WHITE);
        jpPorownywarka.addMouseListener(myMouseListener);
        jpPorownywarka.setName("POROWNYWARKA");


        UIManager.put("ScrollBar.width", 14);
        UIManager.put("ScrollBar.thumb", Color.GRAY);
        UIManager.put("ScrollBar.thumbDarkShadow", Color.LIGHT_GRAY);
        UIManager.put("ScrollBar.thumbHighlight", Color.DARK_GRAY);
        UIManager.put("ScrollBar.thumbShadow", Color.DARK_GRAY);
        UIManager.put("ScrollBar.background", Color.DARK_GRAY);
        UIManager.put("ScrollBar.track", Color.DARK_GRAY);


        jpWykres.setLayout(new BoxLayout(jpWykres, BoxLayout.PAGE_AXIS));
        jpWykres.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Price chart", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.BLACK),
                BorderFactory.createEmptyBorder(5,5,5,1)));

        jpTabela.setLayout(new BoxLayout(jpTabela, BoxLayout.PAGE_AXIS));
        jpTabela.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Table of prices", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.BLACK),
                BorderFactory.createEmptyBorder(5,5,5,1)));

        jpPorownywarka.setLayout(new BoxLayout(jpPorownywarka, BoxLayout.PAGE_AXIS));
        jpPorownywarka.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Price comparison", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.BLACK),
                BorderFactory.createEmptyBorder(5,5,5,1)));

        jlist.addItemListener(myItemListener);

        cards.add(jpTabela);
        cards.add(jpWykres);
        cards.add(jpPorownywarka);

        f.setPreferredSize(new Dimension(820, 500));
        f.add(cards, BorderLayout.CENTER);
        f.add(control, BorderLayout.NORTH);

        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

    }


}
