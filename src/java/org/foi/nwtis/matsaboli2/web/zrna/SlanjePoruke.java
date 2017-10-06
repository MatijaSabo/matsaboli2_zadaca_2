/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.zrna;

import java.util.Date;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.matsaboli2.web.dretve.ObradaPoruka;

/**
 *
 * @author Matija Sabolić
 */
@Named(value = "slanjePoruke")
@RequestScoped
public class SlanjePoruke {

    String posljuzitelj = ObradaPoruka.server;
    String salje;
    String prima;
    String predmet;
    String sadrzaj;

    Boolean uspjesno;
    Boolean neuspjesno;

    public Boolean getUspjesno() {
        return uspjesno;
    }

    public void setUspjesno(Boolean uspjesno) {
        this.uspjesno = uspjesno;
    }

    public Boolean getNeuspjesno() {
        return neuspjesno;
    }

    public void setNeuspjesno(Boolean neuspjesno) {
        this.neuspjesno = neuspjesno;
    }

    public String getSalje() {
        return salje;
    }

    public void setSalje(String salje) {
        this.salje = salje;
    }

    public String getPrima() {
        return prima;
    }

    public void setPrima(String prima) {
        this.prima = prima;
    }

    public String getPredmet() {
        return predmet;
    }

    public void setPredmet(String predmet) {
        this.predmet = predmet;
    }

    public String getSadrzaj() {
        return sadrzaj;
    }

    public void setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gumb salji. Preuzimaju se
     * podaci koji su unešeni u formu te se prema njima kreira e-mail poruka
     * koja se šalje zadanom korinsiku.
     *
     * @return
     */
    public String saljiPoruku() {
        try {
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", this.posljuzitelj);

            Session session = Session.getInstance(properties, null);

            MimeMessage message = new MimeMessage(session);

            Address fromAddress = new InternetAddress(this.salje);
            message.setFrom(fromAddress);

            Address[] toAddresses = InternetAddress.parse(this.prima);
            message.setRecipients(Message.RecipientType.TO, toAddresses);

            message.setSubject(this.predmet);
            message.setText(this.sadrzaj);
            message.setSentDate(new Date());

            Transport.send(message);

            this.uspjesno = true;

        } catch (AddressException e) {
            this.neuspjesno = true;
            this.uspjesno = false;
        } catch (SendFailedException e) {
            this.neuspjesno = true;
            this.uspjesno = false;
        } catch (MessagingException e) {
            this.neuspjesno = true;
            this.uspjesno = false;
        }

        this.sadrzaj = "";
        this.predmet = "";
        this.prima = "";

        return "Poslana poruka";
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gumb pormjena jezika.
     *
     * @return
     */
    public String promjenaJezika() {
        return "promjenaJezika";
    }

    /**
     * Metoda koja se poziva kada korisnik klikne na gumb pregled poruka.
     * Postavlja se odabrana mapa na INBOX.
     *
     * @return
     * @throws MessagingException
     */
    public String pregledPoruka() throws MessagingException {
        PregledPoruka.odabranaMapa = "INBOX";

        return "pregledPoruka";
    }

    /**
     * Creates a new instance of SlanjePoruke
     */
    public SlanjePoruke() {
    }

}
