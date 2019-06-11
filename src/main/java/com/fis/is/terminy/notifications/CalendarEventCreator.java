package com.fis.is.terminy.notifications;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.Reservations;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class CalendarEventCreator {

    private static final String APPLICATION_NAME = "Terminy";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "src/main/resources/tokens";

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = CalendarEventCreator.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static String createEventHtmlLink(Reservations newReservation, @SessionAttribute("company") Company companyInSession) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Event event = getSuppliedEvent(newReservation, companyInSession);
            String calendarId = "primary";
            event = service.events().insert(calendarId, event).execute();

            return prepareHtmlLink(event);
        } catch(IOException | GeneralSecurityException exception) {
            System.out.println(exception.getMessage());
        }
        return "";
    }

    private static String prepareHtmlLink(Event event) {
        String generalPrefix = "https://www.google.com/calendar/event?eid=";
        String eventId =  event.getHtmlLink().substring(generalPrefix.length());

        String resultPrefix = "https://calendar.google.com/calendar/event?action=TEMPLATE&tmeid=";
        String resultSuffix = "&tmsrc=terminy.io@gmail.com&catt=false&pprop=HowCreated:DUPLICATE&hl=pl&scp=ONE";
        return resultPrefix + eventId + resultSuffix;
    }

    private static Event getSuppliedEvent(Reservations newReservation,Company companyInSession) {
        String summary = newReservation.getService().getName();
        String description = String.format("%s - %s", companyInSession.getName(), summary);
        String startDateTimeStr = String.format("%sT%s:00+02:00", newReservation.getDate(), newReservation.getStart_hour());
        String endDateTimeStr = String.format("%sT%s:00+02:00", newReservation.getDate(), newReservation.getEnd_hour());

        EventDateTime start = getEventDateTime(startDateTimeStr);
        EventDateTime end = getEventDateTime(endDateTimeStr);

        return new Event().setSummary(summary).setDescription(description).setStart(start).setEnd(end);
    }

    private static EventDateTime getEventDateTime(String time) {
        DateTime dateTime = new DateTime(time);
        return new EventDateTime()
                .setDateTime(dateTime)
                .setTimeZone("Europe/Warsaw");
    }
}
