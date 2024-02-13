import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Odczyt_Danych {
    private static final Logger log = LogManager.getLogger(Ceny.class);
    public List<Alkohol> odczyt(String link, String rodzaj_alkoholu){
        try {
            List<Alkohol> AlkoholList = new ArrayList<Alkohol>();

            Thread.currentThread().setName("Thread: Website Reading");

            Configurator.initialize(new DefaultConfiguration());
            Configurator.setRootLevel(Level.TRACE);

            log.trace("Connecting to website");
            String url = new String(link);
            Document doc = Jsoup.connect(url).get();
            Elements media = doc.select("table[id=\"benchmarkTable\"][class=\"sortable\"]");
            media = media.select("tbody");
            Elements trs = media.first().select("tr");


            for (Element el : trs) {

                String name = el.children().get(0).text();
                Double cena = Double.valueOf(el.children().get(1).text());
                int lp = Integer.valueOf(el.children().get(2).text());

                Alkohol alk = new Alkohol(rodzaj_alkoholu, name, cena, lp);
                AlkoholList.add(alk);

            }
            return AlkoholList;
        } catch (IOException e) {
            log.error("Connecting Error");
            System.exit(0);
            throw new RuntimeException(e);
        }

    }
}
