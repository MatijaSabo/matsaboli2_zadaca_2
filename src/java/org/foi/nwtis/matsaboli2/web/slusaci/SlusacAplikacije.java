/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.matsaboli2.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.web.dretve.ObradaPoruka;

/**
 * Web application lifecycle listener.
 *
 * @author Matija Sabolić
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    ObradaPoruka op = null;

    /**
     * Metoda koja uzima inicijlni parametar aplikacije pod nazivom
     * konfiguracija i sprema konfiguracije za bazu podataka i za e-mail poruke
     * u kontekst. Nakon toga se kreira i pokreče dretva koja radi obradu
     * primljenih poruka.
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String datoteka = sce.getServletContext().getRealPath("/WEB-INF")
                + File.separator + sce.getServletContext().getInitParameter("konfiguracija");

        Konfiguracija konf = null;

        BP_Konfiguracija bpkonf = new BP_Konfiguracija(datoteka);
        sce.getServletContext().setAttribute("BP_Konfig", bpkonf);

        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            sce.getServletContext().setAttribute("Mail_Konfig", konf);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

        op = new ObradaPoruka();
        op.setSc(sce.getServletContext());
        op.start();
    }

    /**
     * Metoda u kojoj se poziva gašenje dretve ukoliko je ona pokrenuta
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (op != null) {
            op.interrupt();
        }
    }
}
