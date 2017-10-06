/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.io.IOException;
import java.util.ArrayList;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.matsaboli2.web.dretve.ObradaPoruka;
import org.foi.nwtis.matsaboli2.web.kontrole.Izbornik;
import org.foi.nwtis.matsaboli2.web.kontrole.Poruka;

/**
 *
 * @author Matija Sabolić
 */
@Named(value = "pregledPoruka")
@RequestScoped
public class PregledPoruka {

    String posluzitelj = ObradaPoruka.server;
    String kor_ime = ObradaPoruka.user_view;
    String pass = ObradaPoruka.pass_view;
    public static String odabranaMapa = "INBOX";
    String traziPoruku;

    Session session;
    Store store;
    Folder folder;
    Folder[] folders;

    Message[] messages;
    MimeMessage message;

    ArrayList<Izbornik> preuzeteMape = new ArrayList<Izbornik>();
    ArrayList<Poruka> preuzetePoruke = new ArrayList<Poruka>();

    int ukupnoPoruka;
    int brojPrikazanihPoruka = ObradaPoruka.broj_poruka_stranica;
    public static int pocetnaPozicija = 0;
    public static int zavrsnaPozicija = 0;

    /**
     * Creates a new instance of PregledPoruka
     */
    public PregledPoruka() throws MessagingException, IOException {
        preuzmiMape();
        preuzmiPoruke();
    }

    /**
     * Metoda koja preuzima sve mape koje postoje za zadanog korisnika.
     *
     * @throws MessagingException
     */
    public void preuzmiMape() throws MessagingException {
        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", posluzitelj);
        session = Session.getInstance(properties, null);

        store = session.getStore("imap");
        store.connect(posluzitelj, kor_ime, pass);

        folder = store.getDefaultFolder();
        folders = folder.list();

        for (Folder f : folders) {
            this.preuzeteMape.add(new Izbornik(f.getName() + " - " + f.getMessageCount(), f.getName()));
        }
    }

    /**
     * Metoda koja preuzima sve poruke u zadanoj mapi za određenog korisnika.
     * Nakon što se preuzmu sve potrebne poruke one se prikazuju korisniku na
     * ekran od najsvjžijih prema najstarijima.
     *
     * @throws NoSuchProviderException
     * @throws MessagingException
     * @throws IOException
     */
    public void preuzmiPoruke() throws NoSuchProviderException, MessagingException, IOException {
        this.preuzetePoruke.clear();
        messages = null;
        folder = null;

        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", posluzitelj);
        session = Session.getInstance(properties, null);

        store = session.getStore("imap");
        store.connect(posluzitelj, kor_ime, pass);

        folder = store.getFolder(this.odabranaMapa);
        folder.open(Folder.READ_WRITE);

        this.ukupnoPoruka = folder.getMessageCount();

        if (pocetnaPozicija == 0 && zavrsnaPozicija == 0) {
            pocetnaPozicija = this.ukupnoPoruka - this.brojPrikazanihPoruka + 1;
            zavrsnaPozicija = this.ukupnoPoruka;
        }

        if (this.ukupnoPoruka < this.brojPrikazanihPoruka) {
            messages = folder.getMessages();
        } else {
            messages = folder.getMessages(pocetnaPozicija, zavrsnaPozicija);
        }

        for (int i = messages.length; i > 0; i--) {
            message = (MimeMessage) messages[i - 1];

            if (this.traziPoruku == null || "".equals(this.traziPoruku)) {
                this.preuzetePoruke.add(new Poruka(String.valueOf(message.getMessageNumber()), message.getSentDate(), message.getSentDate(), String.valueOf(message.getFrom()[0]), message.getSubject(), message.getContent().toString(), "0"));
            } else {
                System.out.println(message.getContentType());
                if (message.getContent() instanceof String) {
                    if (message.getContent().toString().contains(this.traziPoruku)) {
                        this.preuzetePoruke.add(new Poruka(String.valueOf(message.getMessageNumber()), message.getSentDate(), message.getSentDate(), String.valueOf(message.getFrom()[0]), message.getSubject(), message.getContent().toString(), "0"));
                    }
                }
            }
        }

        folder.close(true);
        store.close();
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gumb promjena mape.
     * Postavljaja se novi ukupni broj poruka u mapi te indexi za početnu i
     * završnu poziciju poruka u mapi. Briše se sadržaj koji je unesen u
     * tražilicu te se poziva metoda za preuzimanje poruka.
     *
     * @param execute
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public String promjenaMape(Boolean execute) throws MessagingException, IOException {
        if (execute == true) {
            this.traziPoruku = "";

            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluzitelj);
            session = Session.getInstance(properties, null);

            store = session.getStore("imap");
            store.connect(posluzitelj, kor_ime, pass);

            folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_WRITE);

            this.ukupnoPoruka = folder.getMessageCount();

            pocetnaPozicija = this.ukupnoPoruka - this.brojPrikazanihPoruka + 1;
            zavrsnaPozicija = this.ukupnoPoruka;
        }

        preuzmiPoruke();
        return "PromjenaMape";
    }

    /**
     * Metoda koja se poziva kada korinsik klikne na gumb traži. Poziva se
     * metoda koja preuzima poruke sa zadanim sadržajem.
     *
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public String filtrirajPoruke() throws MessagingException, IOException {
        preuzmiPoruke();
        return "FiltrirajPoruke";
    }

    /**
     * Metoda koja se poziva kada korinsik klikne na gumb prethodne poruke.
     * Postavljaju se novi indexi za preuzimanje poruka. Nakon što se postave
     * novi indexi poziva se metoda za preuzimanje poruka.
     *
     * @param execute
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public String prethodnePoruke(Boolean execute) throws MessagingException, IOException {
        if (execute == true) {
            pocetnaPozicija = zavrsnaPozicija + 1;
            zavrsnaPozicija = pocetnaPozicija + this.brojPrikazanihPoruka - 1;
        }

        preuzmiPoruke();
        return "PrethodnePoruke";
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gumb sljedeće poruke.
     * Postavljaju se novi indexi za preuzimanje poruka. Nakon što se postave
     * novi indexi poziva se metoda za preuzimanje poruka.
     *
     * @param execute
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public String sljedecePoruke(Boolean execute) throws MessagingException, IOException {
        if (execute == true) {
            zavrsnaPozicija = pocetnaPozicija - 1;
            pocetnaPozicija = pocetnaPozicija - this.brojPrikazanihPoruka;

            if (pocetnaPozicija <= 0) {
                pocetnaPozicija = 1;
            }
        }

        preuzmiPoruke();
        return "SljedecePoruke";
    }

    public String promjenaJezika() {
        return "promjenaJezika";
    }

    public String saljiPoruku() {
        return "saljiPoruku";
    }

    public String getOdabranaMapa() {
        return odabranaMapa;
    }

    public void setOdabranaMapa(String odabranaMapa) {
        PregledPoruka.odabranaMapa = odabranaMapa;
    }

    public String getTraziPoruku() {
        return traziPoruku;
    }

    public void setTraziPoruku(String traziPoruku) {
        this.traziPoruku = traziPoruku;
    }

    public int getUkupnoPoruka() {
        return ukupnoPoruka;
    }

    public void setUkupnoPoruka(int ukupnoPoruka) {
        this.ukupnoPoruka = ukupnoPoruka;
    }

    public ArrayList<Izbornik> getPreuzeteMape() {
        return preuzeteMape;
    }

    public ArrayList<Poruka> getPreuzetePoruke() {
        return preuzetePoruke;
    }

    public int getPocetnaPozicija() {
        return pocetnaPozicija;
    }

    public void setPocetnaPozicija(int pocetnaPozicija) {
        PregledPoruka.pocetnaPozicija = pocetnaPozicija;
    }

    public int getZavrsnaPozicija() {
        return zavrsnaPozicija;
    }

    public void setZavrsnaPozicija(int zavrsnaPozicija) {
        PregledPoruka.zavrsnaPozicija = zavrsnaPozicija;
    }

    public int getBrojPrikazanihPoruka() {
        return brojPrikazanihPoruka;
    }

    public void setBrojPrikazanihPoruka(int brojPrikazanihPoruka) {
        this.brojPrikazanihPoruka = brojPrikazanihPoruka;
    }

}
