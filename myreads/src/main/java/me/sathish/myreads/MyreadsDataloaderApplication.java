package me.sathish.myreads;

import me.sathish.myreads.author.AuthorRepo;
import me.sathish.myreads.books.BookRepo;
import me.sathish.myreads.dataloader.connection.DataStaxAstraProperties;
import me.sathish.myreads.utility.DataLoadUtil;
import me.sathish.myreads.utility.TimeIt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class MyreadsDataloaderApplication {

    @Value("${datadump.location.author}")
    private String authorDumpLocation;
    @Value("${datadump.location.works}")
    private String worksDumpLocation;
    @Autowired
    private AuthorRepo authorRepo;
    @Autowired
    private BookRepo bookrRepo;

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProps) {
        Path bundle = astraProps.getSecureConnectBundle().toPath();
        return cqlSessionBuilder -> cqlSessionBuilder.withCloudSecureConnectBundle(bundle);
    }

    public static void main(String[] args) {
        SpringApplication.run(MyreadsDataloaderApplication.class, args);
    }

    @PostConstruct
    public void start() {
        TimeIt.code(() -> {
            DataLoadUtil dataLoadUtil= new DataLoadUtil();
//            dataLoadUtil.initAuthors(authorDumpLocation,authorRepo);
//            dataLoadUtil.initBooks(worksDumpLocation,bookrRepo,authorRepo);
            System.out.println("Authors loaded and application started");
        });
    }


}
