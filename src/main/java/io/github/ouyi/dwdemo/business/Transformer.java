package io.github.ouyi.dwdemo.business;

import io.github.ouyi.dwdemo.io.PersonDAO;
import liquibase.util.csv.opencsv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Contains the logic of data cleansing and transformation.
 */
public class Transformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Transformer.class);

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int TIME_INDEX = 2;
    public static final int RECORD_DIMENSION = 3;

    private CSVParser parser;
    private DateTimeFormatter dateTimeFormat;
    private PersonDAO personDAO;

    public Transformer(CSVParser parser, DateTimeFormatter dateTimeFormat, PersonDAO personDAO) {
        this.parser = parser;
        this.dateTimeFormat = dateTimeFormat;
        this.personDAO = personDAO;
    }

    /**
     * Transform the input lines based on the business logic.
     * Note the use of the blocking operator <code>distinct</code> here to remove duplicates from the lines
     * (at the file level). The duplicate removal here can be omitted safely, if it is also ensured at
     * the database level. See also {@link PersonDAO#insertAll(Iterator)}.
     *
     * @param lines
     * @throws Exception
     */
    public void transform(Stream<String> lines) throws Exception {
        Iterator<Person> iterator = lines
                .map(parseLine)
                .filter(validRecord)
                .map(nameToLowercase.andThen(toPersonUTC))
                .filter(p -> (p != null))
                .distinct()
                .iterator();
        personDAO.insertAll(iterator);
    }

    protected Predicate<String[]> validRecord = record -> {
        if (record.length < RECORD_DIMENSION) {
            return false;
        }
        String id = record[0];
        String name = record[1];
        String timeOfStart = record[2];

        if (isNullOrEmpty(id) || !id.matches("^\\d+$") || isNullOrEmpty(name) || isNullOrEmpty(timeOfStart)) {
            return false;
        }
        return true;
    };

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected Function<String, String[]> parseLine = l -> {
        try {
            return parser.parseLine(l);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    };

    protected Function<String[], String[]> nameToLowercase = s -> {
        s[NAME_INDEX] = s[NAME_INDEX].toLowerCase();
        return s;
    };

    protected Function<String[], Person> toPersonUTC = s -> {
        // Other fields of the record are implicitly dropped
        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(s[TIME_INDEX], dateTimeFormat);
            return new Person(Integer.parseInt(s[ID_INDEX]), s[NAME_INDEX], dateTime.withZoneSameInstant(ZoneOffset.UTC));
        } catch (Throwable t) {
            LOGGER.error("Person record: {} could not be parsed due to error: {}", Arrays.toString(s), t.getMessage());
        }
        return null;
    };
}
