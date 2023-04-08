package service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import repository.LanguageRepository;
import service.database.DatabaseService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class NotifierService implements Runnable {
    private static final int DEFAULT_TIME_HOUR = 11; // 11
    private static final int DEFAULT_TIME_MINUTE = 0; // 00
    private static final long FORMULA_HOUR_IN_MILLIS = 60 * 60 * 1000;
    private static final long FORMULA_MINUTE_IN_MILLIS = 60 * 1000;
    private static NotifierService notifierService;
    private final LanguageRepository languageRepository;
    private final AbsSender sender;

    private final Integer hour;
    private final Integer minute;


    private NotifierService(AbsSender sender, LanguageRepository languageRepository,
                            Properties applicationProperties) {
        this.languageRepository = languageRepository;
        this.sender = sender;
        String sHour = applicationProperties.getProperty("notifier.hour");
        String sMinute = applicationProperties.getProperty("notifier.minute");
        if (sHour == null || sMinute == null
                || sHour.isBlank() || sMinute.isBlank()) {
            this.hour = DEFAULT_TIME_HOUR;
            this.minute = DEFAULT_TIME_MINUTE;
        } else {
            this.hour = Integer.parseInt(applicationProperties.getProperty("notifier.hour"));
            this.minute = Integer.parseInt(applicationProperties.getProperty("notifier.minute"));
        }
    }

    public static NotifierService getInstance(AbsSender sender,
                                              DatabaseService databaseService,
                                              Properties applicationProperties) {
        return Objects.requireNonNullElseGet(notifierService,
                () -> notifierService = new NotifierService(
                        sender, databaseService, applicationProperties));
    }


    public void start() {
        ScheduledExecutorService executorService = Executors.
                newSingleThreadScheduledExecutor();
        LocalDateTime now = LocalDateTime.now();
        final long dayInMillis = 24 * FORMULA_HOUR_IN_MILLIS;

        long needMILLIS = (hour * FORMULA_HOUR_IN_MILLIS)
                + (minute * FORMULA_MINUTE_IN_MILLIS);

        long currentDayInMillis = now.getLong(ChronoField.MILLI_OF_DAY);

        //todo need check case when time will be before and after current date
        long diff = Math.abs(needMILLIS - currentDayInMillis);

        long initDelayInMillis = currentDayInMillis > needMILLIS ?
                dayInMillis - diff : diff;

        executorService.scheduleAtFixedRate(
                this,
                initDelayInMillis,
                dayInMillis,
                TimeUnit.MILLISECONDS
        );
    }

    @SneakyThrows
    @Override
    public void run() {
        List<Map<String, Object>> userList = languageRepository.getAllUsersWithDayAndWord();
        for (Map<String, Object> user : userList) {
            int day = (Integer) (user.get("day"));
            int userId = (Integer) (user.get("userId"));
            String text = String.format("%s - %s", user.get("english"), user.get("translation"));

            sender.executeAsync(new SendMessage(String.valueOf(userId), text));
            languageRepository.updateUsersDay(userId, day + 1);
        }
    }
}
