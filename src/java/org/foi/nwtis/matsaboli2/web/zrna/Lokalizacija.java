/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import org.foi.nwtis.matsaboli2.web.kontrole.Izbornik;

/**
 *
 * @author Matija Sabolić
 */
@Named(value = "lokalizator")
@SessionScoped
public class Lokalizacija implements Serializable {

    final static ArrayList<Izbornik> izbornikJezika = new ArrayList<>();
    String odabraniJezik;

    /**
     * Metoda koja vraća odabrani jezik aplikacije.
     *
     * @return
     */
    public String getOdabraniJezik() {
        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();

        if (view != null) {
            Locale lokalniJezik = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            odabraniJezik = lokalniJezik.getLanguage();
        }

        return odabraniJezik;
    }

    /**
     * Metoda u kojoj se postavlja zadani jezik aplikacije.
     *
     * @param odabraniJezik
     */
    public void setOdabraniJezik(String odabraniJezik) {
        this.odabraniJezik = odabraniJezik;
        Locale lokalniJezik = new Locale(odabraniJezik);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(lokalniJezik);
    }

    public ArrayList<Izbornik> getIzbornikJezika() {
        return izbornikJezika;
    }

    /**
     * Dodavanje dostupnih jezika u aplikaciju
     */
    static {
        izbornikJezika.add(new Izbornik("Hrvatski", "hr"));
        izbornikJezika.add(new Izbornik("Engleski", "en"));
        izbornikJezika.add(new Izbornik("Njemački", "de"));
    }

    /**
     * Creates a new instance of Lokalizacija
     */
    public Lokalizacija() {
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gumb promjeni jezik
     *
     * @return
     */
    public Object odaberiJezik() {
        setOdabraniJezik(odabraniJezik);
        return "PromjenaJezika";
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gumb šalji poruku
     *
     * @return
     */
    public String saljiPoruku() {
        return "saljiPoruku";
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gubm pregled poruka.
     * Postavlja se odabrana mapa na INBOX.
     *
     * @return
     * @throws MessagingException
     */
    public String pregledPoruka() throws MessagingException {
        PregledPoruka.odabranaMapa = "INBOX";

        return "pregledPoruka";
    }

}
