/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.dretve;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.ReadOnlyFolderException;
import javax.mail.StoreClosedException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Matija Sabolić
 */
public class ObradaPoruka extends Thread {

    String defaultFrom;
    Session session;
    Store store;
    Folder folder;
    Message[] messages;
    MimeMessage message;

    public static String server;
    public static String port;
    public static String user;
    public static String pass;
    public static int broj_poruka_stranica;
    String ispravniFolder;
    String ostaloFolder;
    String nwtis_poruka;
    String statistics_user;
    String statistics_subject;
    public static String user_view;
    public static String pass_view;

    Date pocetak_obrade;
    Date zavrsetak_obrade;
    int broj_poruka;
    int broj_add;
    int broj_temp;
    int broj_event;
    int broj_pogresaka;
    long trajanje_obrade;
    int ciklus;

    private ServletContext sc = null;
    private boolean flag = false;

    String bp_server;
    String bp_baza;
    String bp_korisnik;
    String bp_lozinka;
    String bp_driver;

    public static int redniBrojObrade = 0;

    public void setSc(ServletContext sc) {
        this.sc = sc;
    }

    /**
     * Metoda u kojoj se postavlja zastavica na true ukoliko je prekinuti rad
     * dretve
     */
    @Override
    public void interrupt() {
        flag = true;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja preuzima iz konfiguracijske datoteke sve potrebne
     * vrijednosti. Nakon što se vrijednosti dohvate u pravilnom vremenskim
     * ciklusima se obavlja obrada poruka i slanje statistike.
     */
    @Override
    public void run() {
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Mail_Konfig");
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        bp_server = bp_konf.getServerDatabase();
        bp_baza = bp_server + bp_konf.getUserDatabase();
        bp_korisnik = bp_konf.getUserUsername();
        bp_lozinka = bp_konf.getUserPassword();
        bp_driver = bp_konf.getDriverDatabase();

        server = konf.dajPostavku("mail.server");
        port = konf.dajPostavku("mail.port");
        user = konf.dajPostavku("mail.usernameThread");
        pass = konf.dajPostavku("mail.passwordThread");
        ciklus = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));
        ispravniFolder = konf.dajPostavku("mail.folderNWTiS");
        ostaloFolder = konf.dajPostavku("mail.folderOther");
        nwtis_poruka = konf.dajPostavku("mail.subject");
        statistics_subject = konf.dajPostavku("mail.subjectStatistics");
        statistics_user = konf.dajPostavku("mail.usernameStatistics");
        broj_poruka_stranica = Integer.parseInt(konf.dajPostavku("mail.numMessages"));
        user_view = konf.dajPostavku("mail.usernameView");
        pass_view = konf.dajPostavku("mail.passwordView");

        while (!flag) {
            try {
                redniBrojObrade++;
                pocetak_obrade = new Date();
                long pocetak = System.currentTimeMillis();
                broj_poruka = 0;
                broj_add = 0;
                broj_temp = 0;
                broj_event = 0;
                broj_pogresaka = 0;

                obradiPoruke();

                long kraj = System.currentTimeMillis();
                zavrsetak_obrade = new Date();
                trajanje_obrade = kraj - pocetak;

                posaljiStatistiku();

                sleep((ciklus * 1000) - trajanje_obrade);

            } catch (InterruptedException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * Metoda koja uzima sve poruke koje su u folderu INDEX te provjerava njezin
     * naslov. Ovisno o naslovu poruke radi se obrada njezinog sadržaja i
     * premještanje u određenu mapu.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void obradiPoruke() throws IOException, ClassNotFoundException, SQLException {
        try {
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);
            session = Session.getInstance(properties, null);

            store = session.getStore("imap");
            store.connect(server, user, pass);

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            messages = folder.getMessages();

            for (int i = 0; i < messages.length; i++) {
                message = (MimeMessage) messages[i];

                this.broj_poruka = this.broj_poruka + 1;
                
                if (message.getSubject().equals(nwtis_poruka)) {
                    obradiPoruku(message);
                    premjestiPoruku(ispravniFolder, store, message, folder);
                } else {
                    premjestiPoruku(ostaloFolder, store, message, folder);

                    this.broj_pogresaka = this.broj_pogresaka + 1;
                }
            }

            folder.close(true);
            store.close();

        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
        } catch (FolderClosedException | FolderNotFoundException | NoSuchProviderException | ReadOnlyFolderException | StoreClosedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda u kojoj se radi premještanje poruke u zadani folder. Ukoliko
     * folder ne postoji isti se kreira.
     *
     * @param folderName
     * @param store
     * @param message
     * @param folder
     * @throws MessagingException
     */
    private void premjestiPoruku(String folderName, Store store, MimeMessage message, Folder folder) throws MessagingException {
        Folder newFolder = store.getFolder(folderName);

        if (!newFolder.exists()) {
            newFolder.create(Folder.HOLDS_MESSAGES);
        }

        newFolder.open(Folder.READ_WRITE);

        Message[] poruka = new Message[1];
        poruka[0] = message;

        folder.copyMessages(poruka, newFolder);

        folder.setFlags(poruka, new Flags(Flags.Flag.DELETED), true);
        folder.expunge();
    }

    /**
     * Metoda koja provjerava sadržaj poruke poruke. Ukoliko sadržaj odgovara
     * zadanoj sintaksi poziva se određena metoda za rad s bazom podataka
     *
     * @param message
     * @throws MessagingException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void obradiPoruku(MimeMessage message) throws MessagingException, IOException, ClassNotFoundException, SQLException {
        if (message.getContent() instanceof String) {
            String poruka = message.getContent().toString().trim();
            String[] parts = poruka.split(" ");

            String sintaksa1 = "ADD\\sIoT\\s(\\d{1,6})\\s\"([A-Za-zžđšćč\\s]{1,30})\"\\sGPS:\\s(\\d{1,3}.\\d{6}),(\\d{1,3}.\\d{6});";
            String sintaksa2 = "TEMP\\sIoT\\s(\\d{1,6})\\sT:\\s([0-9]{4}.[0-9]{2}.[0-9]{2})\\s([0-9]{2}.[0-9]{2}.[0-9]{2})\\sC:\\s(\\d{1,3}.\\d);";
            String sintaksa3 = "EVENT\\sIoT\\s(\\d{1,6})\\sT:\\s([0-9]{4}.[0-9]{2}.[0-9]{2})\\s([0-9]{2}.[0-9]{2}.[0-9]{2})\\sF:\\s(\\d{1,2});";

            if ("ADD".equals(parts[0])) {
                Pattern pattern = Pattern.compile(sintaksa1);
                Matcher m = pattern.matcher(poruka);
                boolean valid = m.matches();

                if (valid == true) {
                    dodajUredaj(m.group(1), m.group(2), m.group(3), m.group(4));
                } else {
                    this.broj_pogresaka = this.broj_pogresaka + 1;
                }
            } else if ("TEMP".equals(parts[0])) {
                Pattern pattern = Pattern.compile(sintaksa2);
                Matcher m = pattern.matcher(poruka);
                boolean valid = m.matches();

                if (valid == true) {
                    dodajTemperaturu(m.group(1), m.group(2), m.group(3), m.group(4));
                } else {
                    this.broj_pogresaka = this.broj_pogresaka + 1;
                }
            } else if ("EVENT".equals(parts[0])) {
                Pattern pattern = Pattern.compile(sintaksa3);
                Matcher m = pattern.matcher(poruka);
                boolean valid = m.matches();

                if (valid == true) {
                    dodajDogadaj(m.group(1), m.group(2), m.group(3), m.group(4));
                } else {
                    this.broj_pogresaka = this.broj_pogresaka + 1;
                }
            } else {
                this.broj_pogresaka = this.broj_pogresaka + 1;
            }
        } else {
            this.broj_pogresaka = this.broj_pogresaka + 1;
        }
    }

    /**
     * Metoda koja provjerava zapis u tablici uredaji te ukoliko zapis ne
     * postoji u tablicu se sprema novi uređaj
     *
     * @param id
     * @param name
     * @param latitude
     * @param longitude
     * @throws ClassNotFoundException
     */
    private void dodajUredaj(String id, String name, String latitude, String longitude) throws ClassNotFoundException {
        Class.forName(bp_driver);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT COUNT(*) FROM uredaji WHERE id = " + id;
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();
            odgovor.getString("count(*)");

            if (Integer.parseInt(odgovor.getString("count(*)")) == 0) {
                sql = "INSERT INTO uredaji (id, naziv, latitude, longitude) VALUES (" + id + ", '" + name + "', " + latitude + ", " + longitude + ")";
                int insert = naredba.executeUpdate(sql);

                if (insert == 1) {
                    this.broj_add = this.broj_add + 1;
                } else {
                    this.broj_pogresaka = this.broj_pogresaka + 1;
                }

            } else {
                this.broj_pogresaka = this.broj_pogresaka + 1;
            }
        } catch (SQLException ex) {
            this.broj_pogresaka = this.broj_pogresaka + 1;
        }
    }

    /**
     * Metoda koja provjerava zapis u tablici uredaji te ukoliko zapis postoji
     * sprema se događaj u tablicu temperature
     *
     * @param id
     * @param date
     * @param time
     * @param temp
     * @throws ClassNotFoundException
     */
    private void dodajTemperaturu(String id, String date, String time, String temp) throws ClassNotFoundException {
        Class.forName(bp_driver);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT COUNT(*) FROM uredaji WHERE id = " + id;
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();
            odgovor.getString("count(*)");

            if (Integer.parseInt(odgovor.getString("count(*)")) == 1) {
                date = date.replace(".", "-");
                sql = "INSERT INTO temperature (id, temp, vrijeme_mjerenja) VALUES (" + id + ", " + temp + ", '" + date + " " + time + "')";
                int insert = naredba.executeUpdate(sql);

                if (insert == 1) {
                    this.broj_temp = this.broj_temp + 1;
                } else {
                    this.broj_pogresaka = this.broj_pogresaka + 1;
                }

            } else {
                this.broj_pogresaka = this.broj_pogresaka + 1;
            }
        } catch (SQLException ex) {
            this.broj_pogresaka = this.broj_pogresaka + 1;
        }
    }

    /**
     * Metoda koja provjerava zapis u tablici uredaji te ukoliko zapis postoji
     * sprema se događaj u tablicu dogadaji
     *
     * @param id
     * @param date
     * @param time
     * @param event
     * @throws ClassNotFoundException
     */
    private void dodajDogadaj(String id, String date, String time, String event) throws ClassNotFoundException {
        Class.forName(bp_driver);

        try (Connection veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);) {
            String sql = "SELECT COUNT(*) FROM uredaji WHERE id = " + id;
            Statement naredba = veza.createStatement();
            ResultSet odgovor = naredba.executeQuery(sql);
            odgovor.next();
            odgovor.getString("count(*)");

            if (Integer.parseInt(odgovor.getString("count(*)")) == 1) {
                date = date.replace(".", "-");
                sql = "INSERT INTO dogadaji (id, vrsta, vrijeme_izvrsavanja) VALUES (" + id + ", " + event + ", '" + date + " " + time + "')";
                int insert = naredba.executeUpdate(sql);

                if (insert == 1) {
                    this.broj_event = this.broj_event + 1;
                } else {
                    this.broj_pogresaka = this.broj_pogresaka + 1;
                }

            } else {
                this.broj_pogresaka = this.broj_pogresaka + 1;
            }
        } catch (SQLException ex) {
            this.broj_pogresaka = this.broj_pogresaka + 1;
        }
    }

    /**
     * Metoda koja generira naslov e-mail poruke na temelju statičkog naziva
     * poruke i rednog broja obrade
     *
     * @param name
     * @param broj
     * @return
     */
    public String generateStatisticName(String name, int broj) {
        String rezultat;

        if (broj < 1000) {
            if (broj >= 100) {
                rezultat = " " + String.valueOf(broj);
            } else if (broj >= 10) {
                rezultat = "  " + String.valueOf(broj);
            } else {
                rezultat = "   " + String.valueOf(broj);
            }
        } else {
            rezultat = new DecimalFormat("###,###").format(broj);
        }

        String subject = name + " " + rezultat;

        return subject;
    }

    /**
     * Metoda koja služi za slanje statistike na kraju svakog ciklusa
     * provjeravanja poruka
     *
     * @throws MessagingException
     */
    private void posaljiStatistiku() throws MessagingException {
        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", server);

        Session session = Session.getInstance(properties, null);

        MimeMessage message = new MimeMessage(session);

        Address fromAddress = new InternetAddress("matsaboli2@nwtis.nastava.foi.hr");
        message.setFrom(fromAddress);

        Address[] toAddresses = InternetAddress.parse(this.statistics_user);
        message.setRecipients(Message.RecipientType.TO, toAddresses);

        String pocetak = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.zzz").format(this.pocetak_obrade);
        String kraj = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.zzz").format(this.zavrsetak_obrade);

        String sadrzaj = "";
        sadrzaj = sadrzaj + "Obrada započela u: " + pocetak + " /n";
        sadrzaj = sadrzaj + "Obrada završila u: " + kraj + " /n/n";
        sadrzaj = sadrzaj + "Trajanje obrade u ms: " + this.trajanje_obrade + " /n";
        sadrzaj = sadrzaj + "Broj poruka: " + this.broj_poruka + " /n";
        sadrzaj = sadrzaj + "Broj dodanih IOT: " + this.broj_add + " /n";
        sadrzaj = sadrzaj + "Broj mjerenih TEMP: " + this.broj_temp + " /n";
        sadrzaj = sadrzaj + "Broj izvršenih EVENT: " + this.broj_event + " /n";
        sadrzaj = sadrzaj + "Broj pogrešaka: " + this.broj_pogresaka;

        String subject = generateStatisticName(this.statistics_subject, redniBrojObrade);

        message.setSubject(subject);
        message.setText(sadrzaj);
        message.setSentDate(new Date());

        Transport.send(message);
    }
}
