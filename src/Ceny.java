/*  -Logowanie log4j
    -Pobieranie danych z sieci (różne źrodła)
    -Grafika Swing (Ładna prezentacja danych, Jtable)
    -Jsoup
    -Plik properties
    -Wykresy, Java2D

    CEBY ALKOHOLI W POLSCE I NA ŚWIECIE
    MOZNA WYBRAC ALKOHOL I KRAJ, POROWNAC
    https://www.globalproductprices.com/rankings/vodka_smirnoff_price/
    https://graphics.wsj.com/table/BEER_062415
 */

import java.net.URL;
import java.util.Date;
import java.io.*;
import java.lang.*;
import java.util.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
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

public class Ceny {
    private static Logger log = LogManager.getLogger(Ceny.class);
    public static void main(String[] args) throws Exception {
        log.info("Starting app");

        Properties appProp = new Properties();
        appProp.setProperty("link1", "https://www.globalproductprices.com/rankings/vodka_smirnoff_price/");
        appProp.setProperty("link2", "https://www.globalproductprices.com/rankings/johnnie_walker_whiskey_price/");
        appProp.setProperty("link3", "https://www.globalproductprices.com/rankings/wine_prices/");
        appProp.setProperty("link4", "https://www.globalproductprices.com/rankings/heineken_price/");

        appProp.store(new FileOutputStream("config.properties"), null);


        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { GUI.createAndShowGUI(); }
        });


    }
}