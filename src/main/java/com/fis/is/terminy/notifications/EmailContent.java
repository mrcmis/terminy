package com.fis.is.terminy.notifications;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Reservations;

public class EmailContent {

    private String subject;
    private StringBuilder text = new StringBuilder();

    public EmailContent addCompanyReservationBasicContent(Reservations reservation, Client client) {
        text.append(String.format("Nowa rezerwacja: %s - %s %s, %s\nTermin: %s, %s\n",
                reservation.getService().getName(), client.getName(), client.getSurname(),
                client.getPhone(), reservation.getDate(), reservation.getStart_hour()));
        return this;
    }

    public EmailContent addGCalendar(String eventHtmlLink) {
        text.append(String.format("\nChcesz otrzymać dodatkowe przypomnienie?\n" +
                "Skorzystaj z kalendarza Google i dodaj odpowiednie dla ciebie powiadomienia o wizycie.\n" +
                "Dodaj do kalendarza Google: %s", eventHtmlLink));
        return this;
    }

    public EmailContent addClientReservationBasicContent(Reservations reservation, String companyName) {
        text.append(String.format("Dokonano poprawnej rezerwacji: %s - %s\nTermin: %s, %s\n" +
                "Czas trwania: %s min, cena: %d zł", companyName, reservation.getService().getName(), reservation.getDate(),
                reservation.getStart_hour(), reservation.getService().getDuration(), reservation.getService().getPrice()));
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailContent setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getText() {
        return text.toString();
    }
}
