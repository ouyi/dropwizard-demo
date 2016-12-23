package org.bitbucket.ouyi;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.bitbucket.ouyi.api.TransformResource;
import org.bitbucket.ouyi.api.UploadResource;
import org.bitbucket.ouyi.business.Transformer;
import org.bitbucket.ouyi.db.PersonDAO;
import org.skife.jdbi.v2.DBI;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by worker on 12/18/16.
 */
public class File2DbApplication extends Application<File2DbConfiguration>{
    public static void main(String[] args) throws Exception {
        new File2DbApplication().run(args);
    }
    @Override
    public void run(File2DbConfiguration configuration, Environment environment) throws Exception {

        final String uploadRootDir = configuration.getUploadRootDir();
        Files.createDirectories(Paths.get(uploadRootDir));

        final UploadResource uploadResource = new UploadResource(uploadRootDir);

        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");
        final PersonDAO personDAO = new PersonDAO(dbi);

        final ZoneId zoneId = ZoneId.of(configuration.getTimezone());
        final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(configuration.getDateTimePattern()).withZone(zoneId);
        final Transformer transformer = new Transformer(dateTimeFormat, personDAO);
        final TransformResource transformResource = new TransformResource(uploadRootDir, transformer);

        environment.jersey().register(uploadResource);
        environment.jersey().register(transformResource);
    }
}
