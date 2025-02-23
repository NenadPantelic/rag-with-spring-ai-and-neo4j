package com.np.ai.rag_pipeline.service.alphabuzz;

import com.np.ai.rag_pipeline.entity.alphabuzz.node.Buzz;
import com.np.ai.rag_pipeline.entity.alphabuzz.node.User;
import com.np.ai.rag_pipeline.repository.alphabuzz.Neo4jBuzzRepository;
import com.np.ai.rag_pipeline.repository.alphabuzz.Neo4jUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class StaticGraphPopulatorService {

    private static final UUID USER_UUID_RYAN_LEE = UUID.fromString("716a9ce3-5d35-4a07-a90c-36bf658f2001");
    private static final UUID USER_UUID_MIA_PETERSON = UUID.fromString("82785b0b-e202-4f26-a19c-23dd64a76002");
    private static final UUID USER_UUID_GRACE_FEB = UUID.fromString("38d6df4e-fac7-4a87-a83e-a37422df3003");

    private static final UUID BUZZ_UUID_101 = UUID.fromString("31316f45-d5e0-4063-a7d5-e9e0e3677101");
    private static final UUID BUZZ_UUID_102 = UUID.fromString("e819e25e-1a8c-4e46-8d32-3ab13da9d102");
    private static final UUID BUZZ_UUID_103 = UUID.fromString("6f5d344c-7c23-4d8a-95ca-eafd514b2103");
    private final Neo4jUserRepository userRepository;

    private final Neo4jBuzzRepository buzzRepository;


    public StaticGraphPopulatorService(Neo4jUserRepository userRepository, Neo4jBuzzRepository buzzRepository) {
        this.userRepository = userRepository;
        this.buzzRepository = buzzRepository;
    }

    public void populateStaticUser() {
        var userRyanLee = userRepository.findById(USER_UUID_RYAN_LEE).orElse(new User(
                USER_UUID_RYAN_LEE,
                "ryanlee",
                "Ryan Lee",
                LocalDate.of(2024, 9, 18)));
        userRepository.save(userRyanLee);

        var userMiaPeterson = userRepository.findById(USER_UUID_MIA_PETERSON).orElse(new User(
                USER_UUID_MIA_PETERSON,
                "miapets",
                "Mia Peterson",
                LocalDate.of(2024, 12, 5)));
        userRepository.save(userMiaPeterson);

        var userGraceFeb = userRepository.findById(USER_UUID_GRACE_FEB).orElse(new User(
                USER_UUID_GRACE_FEB,
                "gracefeb",
                "Grace Febian",
                LocalDate.of(2025, 2, 12)));
        userRepository.save(userGraceFeb);
    }

    public void populateStaticBuzz() {
        var buzz_101 = buzzRepository.findById(BUZZ_UUID_101).orElse(new Buzz(
                BUZZ_UUID_101,
                """
                        <p>
                        Had a great brainstorming session this morning. Eager to see our ideas take shape. #innovation
                        </p>
                        """,
                OffsetDateTime.of(2024, 11, 24, 10, 21, 42, 0, ZoneOffset.UTC)));
        buzzRepository.save(buzz_101);

        var buzz_102 = buzzRepository.findById(BUZZ_UUID_102).orElse(new Buzz(
                BUZZ_UUID_102,
                """
                        <p>
                        Another milestone reached with the help of <a href=\"https://alphabuzz.com/user/gracefeb\">@gracefeb</a>
                        and <a href=\"https://alphabuzz.com/user/miapets\">@miapets</a>. Teamwork really makes the dream work!
                        #innovation #teamwork
                        <img src=\"https://storage.alphabuzz.com/images/5c363a6d-aec3-4f78-aff0-01061cdb1cdf.png\"/>
                        </p>""",
                OffsetDateTime.of(2025, 01, 25, 19, 04, 55, 0, ZoneOffset.UTC)));
        buzzRepository.save(buzz_102);

        var buzz_103 = buzzRepository.findById(BUZZ_UUID_103).orElse(new Buzz(
                BUZZ_UUID_103,
                """
                        <p>
                        Preparing our new product launch! Excited to see the reactions of our customers. #innovation
                        Thanks to the team for their hard work! <a href=\"https://alphabuzz.com/user/ryan.lee\">@ryan.lee</a>
                        <a href=\"https://alphabuzz.com/user/miapets\">@miapets</a>
                        <img src=\"https://storage.alphabuzz.com/images/8a381c1b-ba3b-495f-beeb-5ffffa33e807.png\"/>
                        </p>""",
                OffsetDateTime.of(2025, 02, 14, 8, 15, 25, 0, ZoneOffset.UTC)));
        buzzRepository.save(buzz_103);
    }

    public void populateStaticRelationship_Follow() {
        userRepository.upsertFollowRelationship(USER_UUID_RYAN_LEE, USER_UUID_MIA_PETERSON,
                LocalDate.of(2024, 6, 12));
        userRepository.upsertFollowRelationship(USER_UUID_RYAN_LEE, USER_UUID_GRACE_FEB,
                LocalDate.of(2025, 2, 12));
        userRepository.upsertFollowRelationship(USER_UUID_MIA_PETERSON, USER_UUID_RYAN_LEE,
                LocalDate.of(2024, 12, 7));
        userRepository.upsertFollowRelationship(USER_UUID_GRACE_FEB, USER_UUID_MIA_PETERSON,
                LocalDate.of(2025, 2, 17));
    }

    public void populateStaticRelationship_Publish() {
        userRepository.upsertPublishRelationship(USER_UUID_RYAN_LEE, BUZZ_UUID_101);
        userRepository.upsertPublishRelationship(USER_UUID_RYAN_LEE, BUZZ_UUID_102);
        userRepository.upsertPublishRelationship(USER_UUID_GRACE_FEB, BUZZ_UUID_103);
    }

    public void populateStaticRelationship_Like() {
        userRepository.upsertLikeRelationship(USER_UUID_RYAN_LEE, BUZZ_UUID_103,
                OffsetDateTime.of(2025, 2, 15, 19, 28, 17, 0, ZoneOffset.UTC));
        userRepository.upsertLikeRelationship(USER_UUID_MIA_PETERSON, BUZZ_UUID_101,
                OffsetDateTime.of(2024, 11, 24, 10, 21, 42, 0, ZoneOffset.UTC));
        userRepository.upsertLikeRelationship(USER_UUID_MIA_PETERSON, BUZZ_UUID_102,
                OffsetDateTime.of(2025, 1, 26, 14, 9, 52, 0, ZoneOffset.UTC));
    }

    public void populateStaticRelationship_Republish() {
        userRepository.upsertRepublishRelationship(USER_UUID_RYAN_LEE, BUZZ_UUID_103,
                OffsetDateTime.of(2025, 2, 14, 16, 48, 25, 0, ZoneOffset.UTC));
        userRepository.upsertRepublishRelationship(USER_UUID_MIA_PETERSON, BUZZ_UUID_101,
                OffsetDateTime.of(2024, 11, 24, 11, 37, 19, 0, ZoneOffset.UTC));
    }

}
