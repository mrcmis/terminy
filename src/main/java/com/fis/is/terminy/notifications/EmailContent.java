package com.fis.is.terminy.notifications;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.CompanyService;
import com.fis.is.terminy.models.Reservations;

public class EmailContent {

    private String subject;
    private StringBuilder text = new StringBuilder();

    public EmailContent addCompanyReservationBasicContent(Reservations reservation) {
        Client client = reservation.getClient();
        text.append(String.format("Nowa rezerwacja: %s - %s %s, %s\nTermin: %s\n",
                reservation.getService().getName(), client.getName(), client.getSurname(),client.getPhone(),
                reservationDateTime(reservation)));
        return this;
    }

    public EmailContent addGCalendar(String eventHtmlLink) {
        text.append(String.format("\nChcesz otrzymać dodatkowe przypomnienie?\n" +
                "Skorzystaj z kalendarza Google i dodaj odpowiednie dla ciebie powiadomienia o wizycie.\n" +
                "Dodaj do kalendarza Google: %s", eventHtmlLink));
        return this;
    }

    public EmailContent addClientReservationBasicContent(Reservations reservation) {
        CompanyService companyService = reservation.getService();
        text.append(String.format("Dokonano poprawnej rezerwacji: %s - %s\nTermin: %s\n" +
                "Czas trwania: %s min, cena: %d zł", reservation.getCompany().getName(), companyService.getName(),
                reservationDateTime(reservation), companyService.getDuration(), companyService.getPrice()));
        return this;
    }

    public EmailContent addReservationCancelling(Reservations reservation) {
        text.append(String.format("Rezerwacja usługi %s z dnia: %s została odwołana.", reservation.getService().getName(),
                reservationDateTime(reservation)));
        return this;
    }

    private String reservationDateTime(Reservations reservation) {
        return String.format("%s, %s", reservation.getDate(), reservation.getStart_hour());
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
