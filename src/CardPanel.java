

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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class CardPanel {

    private static final Logger log = LogManager.getLogger(CardPanel.class);

    private final static DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private final static int defaultWidth = 320;
    private final static int defaultHeight = 240;

    private final static String[] countries = { "Afghanistan", "Albania", "Argentina", "Austria", "Belgium", "Brazil",
            "Cambodia", "Cameroon", "Canada", "Chile", "China", "Colombia", "Costa Rica", "Croatia", "Czech Republic",
            "Denmark", "Egypt", "France", "Germany", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Ireland",
            "Israel", "Italy", "Jamaica", "Japan", "Kenya", "Mexico", "Morocco", "Nepal", "Netherlands", "New Zealand",
            "Nigeria", "Norway", "Peru", "Philippines", "Poland", "Portugal", "Romania", "Russia", "Saudi Arabia",
            "Serbia", "Slovakia", "South Africa", "South Korea", "Spain", "Sweden", "Switzerland", "Thailand",
            "Ukraine", "United States", "Uruguay", "No File", "Wrong Format", };

    private final static String DEFAULT_COUNTRY = "Poland";
    private final static int POLAND;

    final private static JLabel labelFlag = new JLabel();
    final private static JEditorPane editorPaneHTML = new JEditorPane();;

    final private static JComboBox<String> jlist;


    private final static Icon[] countriesIcon = new Icon[countries.length];
    private final static String[] countriesIconError = new String[countries.length];
    private final static String[] countriesHTML = new String[countries.length];
    private final static String[] countriesHTMLError = new String[countries.length];

    public final static String FLAG_NO_FILE = "<html><font color='red' size=+1><P>ERROR</P><P>File not found.</P></font></html>";
    public final static String FLAG_WRONG_FILE = "<html><font color='red' size=+1><P>ERROR</P><P>Wrong format.</P></font></html>";

    private final static Set<ParserTask> set = Collections.synchronizedSet(new HashSet<ParserTask>());
    private final static ExecutorService pool = Executors.newFixedThreadPool(5);

    static {
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.YELLOW));
        jlist = new JComboBox<String>(countries);

        int i = Arrays.binarySearch(countries, DEFAULT_COUNTRY);
        POLAND = (i<0 ? 0 : i);
    }


    private static void createAndShowGUI() {

        Thread.currentThread().setName("Thread: GUI");

        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.TRACE);

        JFrame f = new JFrame("CardLayout DEMO");

        final JPanel control = new JPanel(new GridLayout());
        final JButton jbuttonNext = new JButton();
        final JButton jbuttonPrevious = new JButton();

        final ImageIcon iiPrevious = new ImageIcon("ICON/arrow-left_red.png");
        final ImageIcon iiNext = new ImageIcon("ICON/arrow-right_red.png");

        final JPanel cards = new JPanel(new CardLayout());
        final String FLAG = "FLAG";
        final String HTML = "HTML";
        final JPanel jpFlag = new JPanel();
        final JPanel jpHTML = new JPanel();



        ActionListener myActionListener = new ActionListener() {

            int i;

            public synchronized void actionPerformed(ActionEvent e) {
                i = jlist.getSelectedIndex();

                if (e.getSource().equals(jbuttonNext)) {
                    i++;
                    if (i == countries.length)
                        i = 0;
                    jlist.setSelectedIndex(i);
                    log.trace("[ActionListener]: NEXT Button \""+countries[i]+"\" ("+i+")");
                    return;
                }
                if (e.getSource().equals(jbuttonPrevious)) {
                    i--;
                    if (i < 0)
                        i = countries.length - 1;
                    jlist.setSelectedIndex(i);
                    log.trace("[ActionListener]: PREVIOUS Button \""+countries[i]+"\" ("+i+")");
                    return;
                }
                log.error("[ActionListener] Unknown object...");
            }
        };

        MouseListener myMouseListener = new MouseListener() {

            CardLayout cl = (CardLayout) cards.getLayout();
            int i;

            public void mouseClicked(MouseEvent e) {}

            public synchronized void mousePressed(MouseEvent e) {
                i = jlist.getSelectedIndex();
                cl.next(cards);
                if (jpFlag.isVisible()) {
                    log.trace("[MouseListener]: "+ "Selected FLAG Panel for \""+countries[i]+"\" ("+i+") - now: \""+labelFlag.getName()+"\"");
                    // If previously set valid flag - skipping
                    if(countries[i]!=labelFlag.getName()) setLabelFlag(i);
                    return;
                }
                else if (jpHTML.isVisible()) {
                    log.trace("[MouseListener]: "+ "Selected HTML Panel for \""+countries[i]+"\" ("+i+") - now: \""+editorPaneHTML.getName()+"\"");
                    // If previously set valid HTML - skipping
                    if(countries[i]!=editorPaneHTML.getName()) setLabelHTML(i);
                }
                else {
                    log.error("[MouseListener]: WRONG PANEL... ");
                }
            }

            public void mouseReleased(MouseEvent e) {}

            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}

        };

        jbuttonNext.setIcon(iiNext);
        jbuttonNext.setBorderPainted(false);
        jbuttonNext.setContentAreaFilled(false);
        jbuttonNext.setBorder(null);
        jbuttonNext.setFocusPainted(false);
        jbuttonNext.setMargin(new Insets(0, 0, 0, 0));
        jbuttonNext.addActionListener(myActionListener);

        jbuttonPrevious.setIcon(iiPrevious);
        jbuttonPrevious.setBorderPainted(false);
        jbuttonPrevious.setContentAreaFilled(false);
        jbuttonPrevious.setBorder(null);
        jbuttonPrevious.setFocusPainted(false);
        jbuttonPrevious.setMargin(new Insets(0, 0, 0, 0));
        jbuttonPrevious.addActionListener(myActionListener);

        jlist.setBackground(Color.LIGHT_GRAY);
        jlist.setForeground(Color.BLUE);
        jlist.setFont(new Font("Verdana", Font.BOLD, 12));

        // JPanel - sterujący
        control.setBackground(Color.DARK_GRAY);

        control.add(jbuttonPrevious);
        control.add(jlist);
        control.add(jbuttonNext);

        // JPanel - składniki głównego okienko
        //jpFlag.setPreferredSize(new Dimension(defaultWidth, defaultHeight));
        jpFlag.setBackground(Color.DARK_GRAY);
        jpFlag.addMouseListener(myMouseListener);
        jpFlag.setName(FLAG);
        jpFlag.add(labelFlag);

        labelFlag.setPreferredSize(new Dimension(defaultWidth,defaultHeight));
        labelFlag.setHorizontalAlignment(JLabel.CENTER);
        labelFlag.setVerticalAlignment(JLabel.TOP);


        //jpHTML.setPreferredSize(new Dimension(defaultWidth, defaultHeight));
        jpHTML.setBackground(Color.DARK_GRAY);
        jpHTML.setForeground(Color.WHITE);
        jpHTML.addMouseListener(myMouseListener);
        jpHTML.setName(HTML);


        editorPaneHTML.setContentType("text/html");
        editorPaneHTML.setEditable(false);
        editorPaneHTML.addMouseListener(myMouseListener);
        editorPaneHTML.setOpaque(false);
        editorPaneHTML.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(editorPaneHTML, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        jpHTML.add(scrollPane);


        UIManager.put("ScrollBar.width", 14);
        UIManager.put("ScrollBar.thumb", Color.GRAY);
        UIManager.put("ScrollBar.thumbDarkShadow", Color.LIGHT_GRAY);
        UIManager.put("ScrollBar.thumbHighlight", Color.DARK_GRAY);
        UIManager.put("ScrollBar.thumbShadow", Color.DARK_GRAY);
        UIManager.put("ScrollBar.background", Color.DARK_GRAY);
        UIManager.put("ScrollBar.track", Color.DARK_GRAY);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() );
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() );

        // https://www.vogella.com/tutorials/EclipseWindowBuilder/article.html
        jpHTML.setLayout(new BoxLayout(jpHTML, BoxLayout.PAGE_AXIS));
        jpHTML.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Short information about the country", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, java.awt.Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5,5,5,1)));

        editorPaneHTML.setForeground(Color.WHITE);




        ItemListener myItemListener = new ItemListener() {

            int i;

            public synchronized void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange()==ItemEvent.SELECTED) {
                    i = jlist.getSelectedIndex();
                    if (jpFlag.isVisible()) {
                        log.trace("[ItemListener]: "+ "Setting FLAG Panel for \""+countries[i]+"\" ("+i+") - now: \""+labelFlag.getName()+"\"");
                        // This should always set new labelFlag
                        if (countries[i]==labelFlag.getName())
                            log.error("current must differ than new! \""+countries[i]+"\"==\""+labelFlag.getName()+"\"");
                        setLabelFlag(i);
                    }
                    else if (jpHTML.isVisible()) {
                        log.trace("[ItemListener]: "+ "Setting HTML Panel for \""+countries[i]+"\" ("+i+") - now: \""+editorPaneHTML.getName()+"\"");
                        // This should always set new labelHTML
                        if (countries[i]==editorPaneHTML.getName())
                            log.error("current must differ than new! \""+countries[i]+"\"==\""+editorPaneHTML.getName()+"\"");
                        setLabelHTML(i);
                    }
                    else {
                        log.error("[ItemListener]: WRONG PANEL... "+ ie.paramString());
                    }
                }
            }
        };

        jlist.addItemListener(myItemListener);
        jlist.setSelectedIndex(POLAND);

        cards.add(jpFlag);
        cards.add(jpHTML);

        f.add(cards, BorderLayout.CENTER);
        f.add(control, BorderLayout.NORTH);

        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        log.trace(jpFlag.getHeight());
        log.trace(jpFlag.getWidth());
    }


    private static Icon getIconCountry(int value) throws FileNotFoundException, ClassCastException {

        String tmp = "FLAGS/" + countries[value].toLowerCase().replaceAll(" ", "-") + "-flag.png";
        log.trace("[getIconCountry]: New Icon for \""+countries[value]+"\", file: \""+tmp+"\"");
        if (!Files.exists(Paths.get(tmp))) {
            throw new FileNotFoundException(tmp);
        }
        ImageIcon image = new ImageIcon(tmp);
        if (image.getIconHeight()<=0 || image.getIconWidth()<=0) {
            throw new ClassCastException(tmp);
        }
        log.debug("[getIconCountry]: Image size: "+image.getIconHeight()+" x "+image.getIconWidth()+". Default: "+defaultHeight+" x "+defaultWidth);
        float mod = Math.max(image.getIconHeight() / (float) defaultHeight,
                image.getIconWidth() / (float) defaultWidth);
        int newHeight = (int) (0.4999 + image.getIconHeight() / mod);
        int newWidth = (int) (0.4999 + image.getIconWidth() / mod);
        log.debug("[getIconCountry]: After modification: "+newHeight+" ("+(image.getIconHeight()/mod)+") x "+newWidth+" ("+(image.getIconWidth()/mod)+")");

        ImageIcon image2 = new ImageIcon(image.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));

        countriesIcon[value] = image2;
        return image2;
    }


    private static synchronized void setLabelHTML(int i) {

        log.trace("[setLabelHTML]: Setting HTML for \""+countries[i]+"\" ("+i+")");

        // sprawdzam czy już ustawione
        // pobieram to co trzeba i zapusuję do tablicy, lub do tablicy błędów...
        // jeśli znowu jestem na tym focusie to ustawiam Label i name dla Label.

        if (countriesHTML[i] != null) {
            log.trace("[setLabelHTML]: Use existing HTML for \""+countries[i]+"\"");
            editorPaneHTML.setText(countriesHTML[i]);
            editorPaneHTML.setName(countries[i]);
            return;
        }
        if (countriesHTMLError[i] != null) {
            log.trace("[setLabelHTML]: Use existing Error HTML for \""+countries[i]+"\"");
            editorPaneHTML.setText(countriesHTMLError[i]);
            editorPaneHTML.setName(countries[i]);
            return;
        }

        editorPaneHTML.setText("<HTML><P><font color=\"white\" face=\"Verdana, Geneva, sans-serif\" size=\"4\">loading...</font></P></HTML>");
        editorPaneHTML.setName("loading...");
        add(new ParserTask(i));
    }

    private static void add(ParserTask parserTask) {
        if (set.add(parserTask)) {
            // New TASK (new element in the set)
            parserTask.addFuture(pool.submit(parserTask));
            log.trace("[add] "+"New "+parserTask);
            return;
        }

        ParserTask tmp = null;
        try {
            tmp = set.stream().filter(parserTask::equals).findAny().orElse(null);
            log.trace("[add] "+"Found "+tmp+(tmp.isDone() ? " DONE" : " WAITING"));
        } catch(Exception e) {
            log.error("[add] Problem with "+tmp+", "+e.getMessage());
            return;
        }
    }


    private static void setLabelFlag(int i) {

        log.trace("[setLabelFlag]: Setting Icon for \""+countries[i]+"\" ("+i+")");
        labelFlag.setName(countries[i]);

        if (countriesIcon[i] != null) {
            log.info("[setLabelFlag]: Use existing Icon for \""+countries[i]+"\"");
            if (labelFlag.getText()!=null)  labelFlag.setText(null);
            labelFlag.setIcon(countriesIcon[i]);
            return;
        }
        if (countriesIconError[i] != null) {
            log.info("[setLabelFlag]: Use existing Error description for \""+countries[i]+"\"");
            if (labelFlag.getIcon()!=null)  labelFlag.setIcon(null);
            labelFlag.setText(countriesIconError[i]);
            return;
        }

        try {
            if (labelFlag.getText()!=null)  labelFlag.setText(null);
            labelFlag.setIcon(getIconCountry(i));
            return;  // finally will be also processed
        } catch (FileNotFoundException e) {
            log.warn("[setLabelFlag]: No Icon for \""+countries[i]+"\"");
            countriesIconError[i]=FLAG_NO_FILE;
        } catch (ClassCastException e) {
            log.warn("[setLabelFlag]: No Valid Icon for \""+countries[i]+"\"");
            countriesIconError[i]=FLAG_WRONG_FILE;
        } catch (Exception e) {
            log.warn("[setLabelFlag]: Problem with Icon for \""+countries[i]+"\"");
            countriesIconError[i]="<html><font color='red' size=+1><P>ERROR</P><P>Problems with the file.</P><P>"+e.getMessage()+"</P></font></html>";
        } finally {
            log.info("[setLabelFlag] "+labelFlag.getName()+" [ICON="+(labelFlag.getIcon()!=null ? "yes" : "no")+", ERROR="+(labelFlag.getText()!=null ? "yes" : "no")+"]");
        }

        if (labelFlag.getIcon()!=null)  labelFlag.setIcon(null);
        labelFlag.setText(countriesIconError[i]);
    }


    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }



    private static  class ParserTask implements Runnable {

        private static String prefix =  "https://www.cia.gov/the-world-factbook/countries/";

        private int country;
        private String address;
        private String tmpInfoCountry;

        private Future<?> future;
        private boolean success = false;

        private static final int MAX_TIMEOUT = 60000;	// 6000 12000 18000 24000 30000
        private int timeout = 600;
        private static final int MAX_ATTEMPTS = 5;

        private double step = (MAX_TIMEOUT-timeout)/MAX_ATTEMPTS;


        public ParserTask(int country) {
            this.country = country;
        }

        public void addFuture(Future<?> submit) {
            this.future=submit;
        }

        public String toString() {
            return "Task: \""+countries[country]+"\" "+(future!=null ? " (finished: "+future.isDone()+")" :"");
        }

        public boolean isDone() {
            return this.future.isDone();
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof ParserTask)) return false;
            return this.country==((ParserTask) obj).country;
        }

        public int hashCode() {
            return this.country;
        }

        public void run() {
            Thread.currentThread().setName("Thread: "+countries[this.country]);
            //try { Thread.sleep((long) (Math.random() * 50000)); } catch (InterruptedException e) {}

            log.trace("[RUN] BEGINS "+this.toString()+" ("+df.format(new Date())+")");

            int attempt=1;

            do {
                Response r = null;
                try {
                    this.address = prefix + countries[this.country].toLowerCase().replaceAll(" ", "-");
                    r = Jsoup.connect(address).header("Accept-Encoding", "gzip, deflate")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                            .header("Content-Type", "text/html; charset=ISO-8859-1").referrer("http://www.google.com")
                            .ignoreContentType(true).maxBodySize(0).timeout(timeout).ignoreHttpErrors(false).execute();
                    log.info("[RUN] Trying connect to: " + address);
                    tmpInfoCountry ="<html><FONT color=\"white\" face=\"Verdana, Geneva, sans-serif\">\n";
                    log.info("Parsing the content ");
                    Document doc = r.parse();
                    Elements tmp = doc.select("div[class][id=people-and-society],div[class][id=geography]").select("div").not("[class]");
                    //Elements tmp = doc.select("div[class][id=people-and-society]").select("div").not("[class]");
                    //Elements tmp = doc.select("div[class][id=geography]").select("div").not("[class]");
                    //System.out.println(tmp);
                    Element e2;

                    for (Element e: tmp) {
                        //System.out.println(e);
                        if ((e2=e.selectFirst("h3"))!=null) {
                            //System.out.println(e2.text().toUpperCase());
                            switch (e2.text().toUpperCase()) {
                                case "LOCATION":
                                case "CLIMATE":
                                case "AREA":
                                case "NATURAL RESOURCES":
                                case "NATURAL HAZARDS":
                                case "POPULATION":
                                case "MEDIAN AGE":
                                    //System.out.println(e2.text());
                                    //System.out.println(e.selectFirst("p").html());
                                    tmpInfoCountry+=("<P><B>"+e2.text()+":</B>&nbsp;&nbsp;<font size=-1>"+e.selectFirst("p").html().replaceAll("<br><br>", ", &nbsp;").split("note:",2)[0].split("note ",2)[0].replaceAll("\\s*\\([^\\)]*\\)\\s*", " ")+"</font></P>\n");
                            }
                        }
                    }

                    tmpInfoCountry+="</font></html>";
                    success=true;
                }
                catch (MalformedURLException e) {
                    log.error("[RUN] Problem with URL: " + address);
                    tmpInfoCountry="<html><P><font face=\"Calibri, Arial\" color='red' size=+1><B>ERROR</B></font><BR><font face=\"Calibri, Arial\" color='red'>Problem with the address:<BR>"+address+"</P></font><P><font face=\"Verdana, Geneva\" color='red'><B>The address is invalid.</B></font></P></html>";
                    success = false;
                    break;
                }
                catch (UnknownHostException e) {
                    log.error("[RUN] Cannot connect to: " + address + " ("+e.getLocalizedMessage()+")",e);
                    tmpInfoCountry="<html><P><font face=\"Calibri, Arial\" color='red' size=+1><B>ERROR</B></font><BR><font face=\"Calibri, Arial\" color='red'>Problem with the address:<BR>"+address+"</P></font><P><font face=\"Verdana, Geneva\" color='red'><B>NOTE:</B><BR><I>Host is unavailable. &nbsp;Check Internet connection... </I></font></P></html>";
                    success = false;
                }
                catch (SocketTimeoutException e) {
                    int old_timeout = timeout;
                    timeout+=step;
                    log.error("[RUN] Timeout Problem with URL: " + address + "\n\t - "+e.getLocalizedMessage()+", current timeout: "+old_timeout+"ms, new timeout: "+timeout+"ms");
                    tmpInfoCountry="<html><P><font face=\"Calibri, Arial\" color='red' size=+1><B>ERROR</B></font><BR><font face=\"Calibri, Arial\" color='red'>Cannot connect to:<BR>"+address+"</P></font><BR><I><P><font face=\"Calibri, Arial\" color='red'>NOTE: &nbsp;Timeout problem. &nbsp;"+(attempt<=MAX_ATTEMPTS ? " Current value: "+old_timeout+"ms</font></P></I><BR><font face=\"Cambria, Times\" color='green'><I>Attempt: "+attempt+"/"+MAX_ATTEMPTS+", trying again with new timeout: "+timeout+"ms</font></I></html>" : "Check Internet connection...<BR>(timeout: "+old_timeout+"ms)</font></P></I></html>");
                    success = false;
                }
                catch (HttpStatusException e) {
                    log.error("[RUN] Problem with connection to: " + address + " ("+e.getLocalizedMessage()+")",e);
                    tmpInfoCountry="<html><P><font face=\"Calibri, Arial\" color='red' size=+1><B>ERROR</B></font><BR><font face=\"Calibri, Arial\" color='red'>Cannot connect to:<BR>"+address+"</P></font><BR><I><P><font face=\"Calibri, Arial\" color='red'>NOTE: &nbsp;Address is possibly wrong... &nbsp;(Status code: "+e.getStatusCode()+")</P></I>"+(attempt<=MAX_ATTEMPTS ? "<BR> <font face=\"Cambria, Times\" color='green'><I>Attempt: "+attempt+"/"+MAX_ATTEMPTS+"</font></I></html>" : "</font></P></I></html>");
                    success = false;
                }
                catch (NoRouteToHostException e) {
                    log.error("[RUN] Problem with connection to: " + address + " ("+e.getLocalizedMessage()+")",e);
                    tmpInfoCountry="<html><P><font face=\"Calibri, Arial\" color='red' size=+1><B>ERROR</B></font><BR><font face=\"Calibri, Arial\" color='red'>No route to the host:<BR>"+address+"</P></font><BR><I><P><font face=\"Calibri, Arial\" color='red'>NOTE: &nbsp;Address is possibly wrong...</P></I>"+(attempt<=MAX_ATTEMPTS ? "<BR> <font face=\"Cambria, Times\" color='green'><I>Attempt: "+attempt+"/"+MAX_ATTEMPTS+"</font></I></html>" : "</font></P></I></html>");
                    success = false;
                }
                catch (Exception e) {
                    log.error("[RUN] Problem with URL: " + address + " ("+e.getLocalizedMessage()+")",e);
                    tmpInfoCountry="<html><P><font face=\"Calibri, Arial\" color='red' size=+1><B>ERROR</B></font><BR><font color='red'>Cannot connect to:<BR>"+address+"</P></font><BR><I><P>"+e.getLocalizedMessage()+"</P></I></html>";
                    success = false;
                }
                finally {
                    synchronized(this) {
                        int i = jlist.getSelectedIndex();
                        if (i==country) {
                            editorPaneHTML.setText(tmpInfoCountry);
                            editorPaneHTML.setName(countries[country]);
                        }
                        else
                            log.info("[RUN] Item has been changed: \""+countries[country]+"\"!=\""+countries[i]+"\" - skipping.");
                    }
                    if (success) {
                        countriesHTML[country]=tmpInfoCountry;
                        return;
                    }
                    countriesHTMLError[country]=tmpInfoCountry;
                }
                log.trace("[RUN] Trying to connect again in 7 seconds... (" + attempt+"/"+MAX_ATTEMPTS+")");
                try { Thread.sleep(7000); } catch (InterruptedException e1) {}
            } while (attempt++<=MAX_ATTEMPTS);
        }

    }


}
