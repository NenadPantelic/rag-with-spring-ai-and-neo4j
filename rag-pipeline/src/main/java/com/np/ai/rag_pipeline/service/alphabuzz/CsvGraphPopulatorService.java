package com.np.ai.rag_pipeline.service.alphabuzz;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.*;
import com.np.ai.rag_pipeline.repository.alphabuzz.*;
import org.apache.commons.csv.CSVFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CsvGraphPopulatorService {

    private static final Logger LOG = LoggerFactory.getLogger(CsvGraphPopulatorService.class);

    enum UserCsvHeaders {
        user_id, display_name, username, registered_at
    }

    enum BuzzCsvHeaders {
        buzz_id, published_by, content, published_at
    }

    enum RelationshipFollowCsvHeaders {
        user_id, follow_user_id, since
    }

    enum RelationshipLikeCsvHeaders {
        user_id, buzz_id, like_at
    }

    enum RelationshipRepublishCsvHeaders {
        user_id, buzz_id, republish_at
    }

    private static final String USER_CSV_FILE = "src/main/resources/alphabuzz/alphabuzz-user.csv";
    private static final String BUZZ_CSV_FILE = "src/main/resources/alphabuzz/alphabuzz-buzz.csv";
    private static final String RELATIONSHIP_FOLLOW_CSV_FILE = "src/main/resources/alphabuzz/alphabuzz-relationship-follow.csv";
    private static final String RELATIONSHIP_LIKE_CSV_FILE = "src/main/resources/alphabuzz/alphabuzz-relationship-like.csv";
    private static final String RELATIONSHIP_REPUBLISH_CSV_FILE = "src/main/resources/alphabuzz/alphabuzz-relationship-republish.csv";

    private static final Pattern REGEX_PATTERN_TAG = Pattern.compile("#(\\w+)");

    private final Neo4jUserRepository userRepository;

    private final Neo4jBuzzRepository buzzRepository;

    private final Neo4jImageRepository imageRepository;
    private final Neo4jLinkRepository linkRepository;
    private final Neo4jTagRepository tagRepository;

    public CsvGraphPopulatorService(Neo4jUserRepository userRepository,
                                    Neo4jBuzzRepository buzzRepository,
                                    Neo4jImageRepository imageRepository,
                                    Neo4jLinkRepository linkRepository,
                                    Neo4jTagRepository tagRepository) {
        this.userRepository = userRepository;
        this.buzzRepository = buzzRepository;
        this.imageRepository = imageRepository;
        this.linkRepository = linkRepository;
        this.tagRepository = tagRepository;
    }

    public int populateCsvUser() throws IOException {
        var userCsvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(UserCsvHeaders.class)
                .setSkipHeaderRecord(true)
                .build();

        var userCsvRecords = userCsvFormat.parse(new FileReader(USER_CSV_FILE));
        var processedRecords = 0;

        for (var record : userCsvRecords) {
            try {
                var uuid = UUID.fromString(record.get(UserCsvHeaders.user_id));
                var userNode = userRepository.findById(uuid);

                if (userNode.isEmpty()) {
                    var user = new User(
                            uuid,
                            record.get(UserCsvHeaders.username),
                            record.get(UserCsvHeaders.display_name),
                            LocalDate.parse(record.get(UserCsvHeaders.registered_at))
                    );

                    userRepository.save(user);
                }
            } catch (Exception e) {
                LOG.error("Error processing the user record {} on line {}", record, processedRecords);
            } finally {
                processedRecords++;
            }
        }

        return processedRecords;
    }

    public int populateCsvBuzz() throws FileNotFoundException, IOException {
        var buzzCsvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(BuzzCsvHeaders.class)
                .setSkipHeaderRecord(true)
                .build();
        var buzzCsvRecords = buzzCsvFormat.parse(new FileReader(BUZZ_CSV_FILE));
        var processedRecords = 0;

        for (var record : buzzCsvRecords) {
            try {
                var buzzNode = buzzRepository.findById(UUID.fromString(record.get(BuzzCsvHeaders.buzz_id)));

                if (buzzNode.isEmpty()) {
                    var buzz = new Buzz(
                            UUID.fromString(record.get(BuzzCsvHeaders.buzz_id)),
                            record.get(BuzzCsvHeaders.content),
                            OffsetDateTime.parse(record.get(BuzzCsvHeaders.published_at)));

                    buzzRepository.save(buzz);
                }
            } catch (Exception e) {
                LOG.error("Error processing buzz record: {} on line {}", record, processedRecords, e);
            } finally {
                processedRecords++;
            }
        }

        return processedRecords;
    }

    public int populateCsvRelationship_Follow() throws FileNotFoundException, IOException {
        var relationshipFollowCsvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(RelationshipFollowCsvHeaders.class)
                .setSkipHeaderRecord(true)
                .build();
        var relationshipFollowCsvRecords = relationshipFollowCsvFormat
                .parse(new FileReader(RELATIONSHIP_FOLLOW_CSV_FILE));
        var processedRecords = 0;

        for (var record : relationshipFollowCsvRecords) {
            try {
                userRepository.upsertFollowRelationship(
                        UUID.fromString(record.get(RelationshipFollowCsvHeaders.user_id)),
                        UUID.fromString(record.get(RelationshipFollowCsvHeaders.follow_user_id)),
                        LocalDate.parse(record.get(RelationshipFollowCsvHeaders.since)));
            } catch (Exception e) {
                LOG.error("Error processing relationship-follow record: {} on line {}", record, processedRecords, e);
            } finally {
                processedRecords++;
            }
        }

        return processedRecords;
    }

    public int populateCsvRelationship_Publish() throws FileNotFoundException, IOException {
        var buzzCsvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(BuzzCsvHeaders.class)
                .setSkipHeaderRecord(true)
                .build();
        var buzzCsvRecords = buzzCsvFormat.parse(new FileReader(BUZZ_CSV_FILE));
        var processedRecords = 0;

        for (var record : buzzCsvRecords) {
            try {
                userRepository.upsertPublishRelationship(
                        UUID.fromString(record.get(BuzzCsvHeaders.published_by)),
                        UUID.fromString(record.get(BuzzCsvHeaders.buzz_id)));
            } catch (Exception e) {
                LOG.error("Error processing buzz record: {} on line {}", record, processedRecords, e);
            } finally {
                processedRecords++;
            }
        }

        return processedRecords;
    }

    public int populateCsvRelationship_Like() throws FileNotFoundException, IOException {
        var relationshipLikeCsvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(RelationshipLikeCsvHeaders.class)
                .setSkipHeaderRecord(true)
                .build();
        var relationshipLikeCsvRecords = relationshipLikeCsvFormat
                .parse(new FileReader(RELATIONSHIP_LIKE_CSV_FILE));
        var processedRecords = 0;

        for (var record : relationshipLikeCsvRecords) {
            try {
                userRepository.upsertLikeRelationship(
                        UUID.fromString(record.get(RelationshipLikeCsvHeaders.user_id)),
                        UUID.fromString(record.get(RelationshipLikeCsvHeaders.buzz_id)),
                        OffsetDateTime.parse(record.get(RelationshipLikeCsvHeaders.like_at)));
            } catch (Exception e) {
                LOG.error("Error processing relationship-like record: {} on line {}", record, processedRecords, e);
            } finally {
                processedRecords++;
            }
        }

        return processedRecords;
    }

    public int populateCsvRelationship_Republish() throws FileNotFoundException, IOException {
        var relationshipRepublishCsvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(RelationshipRepublishCsvHeaders.class)
                .setSkipHeaderRecord(true)
                .build();
        var relationshipRepublishCsvRecords = relationshipRepublishCsvFormat
                .parse(new FileReader(RELATIONSHIP_REPUBLISH_CSV_FILE));
        var processedRecords = 0;

        for (var record : relationshipRepublishCsvRecords) {
            try {
                userRepository.upsertRepublishRelationship(
                        UUID.fromString(record.get(RelationshipRepublishCsvHeaders.user_id)),
                        UUID.fromString(record.get(RelationshipRepublishCsvHeaders.buzz_id)),
                        OffsetDateTime.parse(record.get(RelationshipRepublishCsvHeaders.republish_at)));
            } catch (Exception e) {
                LOG.error("Error processing relationship-republish record: {} on line {}", record, processedRecords, e);
            } finally {
                processedRecords++;
            }
        }

        return processedRecords;
    }

    private void populateBuzzChildNodes_Image(UUID buzzId, Document parsedHtmlContent) {
        var images = parsedHtmlContent.select("img");

        for (var image : images) {
            var imageUrl = image.attr("src");
            var imageNode = imageRepository.findByUrl(imageUrl);

            if (imageNode.isEmpty()) {
                imageRepository.save(
                        new Image(UUID.randomUUID(), imageUrl));
            }

            buzzRepository.upsertImageRelationship(buzzId, imageUrl);
        }
    }

    private void populateBuzzChildNodes_Link(UUID buzzId, Document parsedHtmlContent) {
        var links = parsedHtmlContent.select("a[href]:not([href^=https://alphabuzz.com/user/])");

        for (var link : links) {
            var url = link.attr("href");
            var linkNode = linkRepository.findByUrl(url);

            if (linkNode.isEmpty()) {
                linkRepository.save(new Link(UUID.randomUUID(), url));
            }

            buzzRepository.upsertLinkRelationship(buzzId, url);
        }
    }

    private void populateBuzzChildNodes_Mention(UUID buzzId, Document parsedHtmlContent) {
        var userLinks = parsedHtmlContent.select("a[href^=https://alphabuzz.com/user/]");

        for (var link : userLinks) {
            var url = link.attr("href");
            var username = url.substring(url.lastIndexOf('/') + 1);
            var userNode = userRepository.findByUsername(username);

            if (userNode.isPresent()) {
                var user = userNode.get();
                buzzRepository.upsertMentionRelationship(buzzId, user.getUserId());
            }
        }
    }

    private void populateBuzzChildNodes_Tag(UUID buzzId, Document parsedHtmlContent) {
        var textContent = parsedHtmlContent.select("p").first().text();
        var matcher = REGEX_PATTERN_TAG.matcher(textContent);

        while (matcher.find()) {
            var tagText = matcher.group(1).toLowerCase();
            var tagNode = tagRepository.findByText(tagText);

            if (tagNode.isEmpty()) {
                tagRepository.save(new Tag(UUID.randomUUID(), tagText));
            }

            buzzRepository.upsertTagRelationship(buzzId, tagText);
        }
    }

    public int populateBuzzChildNodes() {
        var processedRecords = 0;
        var allBuzzes = buzzRepository.findAll();

        for (var buzz : allBuzzes) {
            var parsedHtmlContent = Jsoup.parse(buzz.getHtmlContent());

            populateBuzzChildNodes_Image(buzz.getBuzzId(), parsedHtmlContent);
            populateBuzzChildNodes_Link(buzz.getBuzzId(), parsedHtmlContent);
            populateBuzzChildNodes_Tag(buzz.getBuzzId(), parsedHtmlContent);
            populateBuzzChildNodes_Mention(buzz.getBuzzId(), parsedHtmlContent);

            processedRecords++;
        }

        return processedRecords;
    }
}
