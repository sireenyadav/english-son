package com.example.data

import com.example.data.model.EnglishTest
import com.example.data.model.Question

object MockData {
    val readingPassageIelts = """
        The Impact of Language Learning on Brain Structure
        Recent neuroimaging studies have provided compelling evidence that learning a second language alters the physical structure of the brain. Historically, researchers believed that the human brain was relatively static after childhood. However, modern neuroscience has shown that the brain remains plastic throughout life, adapting to new cognitive demands.
        
        When a person learns a second language, specific areas of the brain undergo growth. Specifically, the left inferior parietal cortex, an area responsible for integrating sensory inputs and managing language processing, shows an increase in gray matter density. This increase is more pronounced in individuals who begin learning at an earlier age, although late-stage bilinguals also display significant structural changes.
        
        Additionally, the white matter tracts that connect different brain regions—especially the corpus callosum and the superior longitudinal fasciculus—show increased integrity and myelination. Myelin is a fatty sheath that insulates nerve fibers, enabling electrical signals to travel faster. The reinforcement of these connections suggests that bilingualism enhances not only language processing but also general executive control functions, such as task-switching and selective attention.
    """.trimIndent()

    val testsList = listOf(
        EnglishTest(
            id = "reading_ielts_04",
            title = "IELTS Academic Reading Mock 04",
            category = "Reading",
            durationMinutes = 60,
            questionsCount = 5,
            iconEmoji = "📖",
            description = "Test your academic reading skills with this comprehension quiz on language learning and brain neurology.",
            readingPassage = readingPassageIelts,
            questions = listOf(
                Question(
                    id = 1,
                    questionText = "According to the passage, what did researchers historically believe about the human brain?",
                    options = listOf(
                        "It is continuously adaptable throughout life.",
                        "It remains largely unchanged after childhood.",
                        "It does not require cognitive stimulation.",
                        "Language processing is located solely in the right hemisphere."
                    ),
                    correctOptionIndex = 1,
                    explanation = "The text states: 'Historically, researchers believed that the human brain was relatively static after childhood.'"
                ),
                Question(
                    id = 2,
                    questionText = "Which area of the brain shows an increase in gray matter density upon learning a second language?",
                    options = listOf(
                        "The occipital lobe",
                        "The left inferior parietal cortex",
                        "The prefrontal cortex",
                        "The cerebellum"
                    ),
                    correctOptionIndex = 1,
                    explanation = "The passage mentions: 'Specifically, the left inferior parietal cortex... shows an increase in gray matter density.'"
                ),
                Question(
                    id = 3,
                    questionText = "What is the primary function of myelin described in the text?",
                    options = listOf(
                        "To generate new neurons in the cortex.",
                        "To absorb sensory information.",
                        "To insulate nerve fibers for faster electrical signaling.",
                        "To reduce cognitive fatigue during speech."
                    ),
                    correctOptionIndex = 2,
                    explanation = "The text explains: 'Myelin is a fatty sheath that insulates nerve fibers, enabling electrical signals to travel faster.'"
                ),
                Question(
                    id = 4,
                    questionText = "The reinforcement of white matter tracts is suggested to enhance which of the following?",
                    options = listOf(
                        "Auditory acuity and musical talent.",
                        "Mathematical problem-solving.",
                        "Bilingualism and executive control functions like task-switching.",
                        "Short-term visual recognition."
                    ),
                    correctOptionIndex = 2,
                    explanation = "The text says: 'The reinforcement of these connections suggests that bilingualism enhances not only language processing but also general executive control functions...'"
                ),
                Question(
                    id = 5,
                    questionText = "Are late-stage bilinguals (those who learn language later in life) completely excluded from structural changes?",
                    options = listOf(
                        "Yes, changes only happen if you learn as a child.",
                        "No, but they only experience white matter decay.",
                        "No, they also display significant structural changes.",
                        "Yes, because their brains are no longer plastic."
                    ),
                    correctOptionIndex = 2,
                    explanation = "The passage states: '...although late-stage bilinguals also display significant structural changes.'"
                )
            )
        ),
        EnglishTest(
            id = "grammar_tense_quiz",
            title = "Tense & Aspect Quiz",
            category = "Grammar",
            durationMinutes = 15,
            questionsCount = 5,
            iconEmoji = "📝",
            description = "Master the subtle differences between Present Perfect Simple, Past Simple, and Future Continuous.",
            questions = listOf(
                Question(
                    id = 1,
                    questionText = "By this time tomorrow, they _______________ to London for their annual conference.",
                    options = listOf(
                        "will fly",
                        "will be flying",
                        "have flown",
                        "are flying"
                    ),
                    correctOptionIndex = 1,
                    explanation = "Future Continuous ('will be flying') is used to describe an action that will be in progress at a specific point in the future."
                ),
                Question(
                    id = 2,
                    questionText = "Since she started her job last year, Emily _______________ three major project milestones.",
                    options = listOf(
                        "completed",
                        "has completed",
                        "is completing",
                        "had completed"
                    ),
                    correctOptionIndex = 1,
                    explanation = "The present perfect simple ('has completed') connects a past action (starting her job) with the present time."
                ),
                Question(
                    id = 3,
                    questionText = "While the lecturer was speaking, the students _______________ diligent notes.",
                    options = listOf(
                        "were taking",
                        "took",
                        "had taken",
                        "take"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Past Continuous ('were taking') represents a continuous action occurring simultaneously with another past continuous action ('was speaking')."
                ),
                Question(
                    id = 4,
                    questionText = "If I _______________ about the schedule conflict earlier, I would have rescheduled.",
                    options = listOf(
                        "knew",
                        "would know",
                        "had known",
                        "have known"
                    ),
                    correctOptionIndex = 2,
                    explanation = "This is a Third Conditional sentence. The past perfect ('had known') is used in the 'if' clause to represent hypothetical past conditions."
                ),
                Question(
                    id = 5,
                    questionText = "The brand-new high-speed rail line _______________ next Monday at 8:00 AM.",
                    options = listOf(
                        "opens",
                        "is going to open",
                        "will have opened",
                        "is opening"
                    ),
                    correctOptionIndex = 0,
                    explanation = "We use the Present Simple ('opens') for official, scheduled future timetables."
                )
            )
        ),
        EnglishTest(
            id = "vocab_booster",
            title = "Vocabulary Booster",
            category = "Vocabulary",
            durationMinutes = 12,
            questionsCount = 5,
            iconEmoji = "📚",
            description = "Expand your academic and advanced lexical range with synonym challenges and context questions.",
            questions = listOf(
                Question(
                    id = 1,
                    questionText = "Choose the word closest in meaning to 'EPHEMERAL':",
                    options = listOf(
                        "Enduring and permanent",
                        "Short-lived and fleeting",
                        "Intricate and complicated",
                        "Loud and chaotic"
                    ),
                    correctOptionIndex = 1,
                    explanation = "'Ephemeral' means lasting for a very short time; transient, fleeting, or short-lived."
                ),
                Question(
                    id = 2,
                    questionText = "Which word completes the blank: 'Her explanation was so ________ that everyone in the room immediately grasped the complex concept.'",
                    options = listOf(
                        "lucid",
                        "obscure",
                        "redundant",
                        "capricious"
                    ),
                    correctOptionIndex = 0,
                    explanation = "'Lucid' means expressed clearly, easy to understand, or intelligible."
                ),
                Question(
                    id = 3,
                    questionText = "What is the antonym of the word 'BENEVOLENT'?",
                    options = listOf(
                        "Altruistic",
                        "Malevolent",
                        "Magnanimous",
                        "Placid"
                    ),
                    correctOptionIndex = 1,
                    explanation = "'Benevolent' means well-meaning and kindly. 'Malevolent' means having or showing a wish to do evil to others, hence its direct antonym."
                ),
                Question(
                    id = 4,
                    questionText = "Complete: 'Despite the criticism, he remained ________, refusing to alter his position.'",
                    options = listOf(
                        "vacillating",
                        "obdurate",
                        "acquiescent",
                        "timorous"
                    ),
                    correctOptionIndex = 1,
                    explanation = "'Obdurate' means stubbornly refusing to change one's opinion or course of action."
                ),
                Question(
                    id = 5,
                    questionText = "Identify the option that is a synonym for 'PRAGMATIC':",
                    options = listOf(
                        "Idealistic",
                        "Unrealistic",
                        "Practical",
                        "Apathetic"
                    ),
                    correctOptionIndex = 2,
                    explanation = "'Pragmatic' means dealing with things sensibly and realistically in a way that is based on practical rather than theoretical considerations."
                )
            )
        ),
        EnglishTest(
            id = "speaking_practice_01",
            title = "Pronunciation & Accent",
            category = "Speaking",
            durationMinutes = 10,
            questionsCount = 5,
            iconEmoji = "🗣️",
            description = "Simulated verbal evaluation focusing on syllable stress, intonation, and response fluency.",
            questions = listOf(
                Question(
                    id = 1,
                    questionText = "Which syllable has the primary stress in the word 'DEMOCRACY'?",
                    options = listOf(
                        "First syllable (de-)",
                        "Second syllable (-moc-)",
                        "Third syllable (-ra-)",
                        "Fourth syllable (-cy)"
                    ),
                    correctOptionIndex = 1,
                    explanation = "In 'Democracy' (de-MOC-ra-cy), the primary stress falls on the second syllable."
                ),
                Question(
                    id = 2,
                    questionText = "When asking a Yes/No question (e.g., 'Are you coming?'), which intonation pattern is most common in English?",
                    options = listOf(
                        "Falling intonation",
                        "Rising intonation",
                        "Flat/monotone intonation",
                        "Steadily declining intonation"
                    ),
                    correctOptionIndex = 1,
                    explanation = "Yes/No questions typically end with a rising intonation to indicate a question requiring a binary response."
                ),
                Question(
                    id = 3,
                    questionText = "Identify the word where the letters 'ch' are pronounced with a /k/ sound rather than a /tʃ/ sound:",
                    options = listOf(
                        "Cherish",
                        "Chronology",
                        "Chamber",
                        "Charity"
                    ),
                    correctOptionIndex = 1,
                    explanation = "'Chronology' is pronounced with a /k/ sound (Krah-nahl-uh-jee), whereas the others use /tʃ/ (Chamber, Charity) or /ʃ/ (Cherish)."
                ),
                Question(
                    id = 4,
                    questionText = "In a standard presentation introduction (e.g., 'Today, I'd like to talk about...'), which word should be emphasized for clarity?",
                    options = listOf(
                        "The pronoun 'I'",
                        "The preposition 'about'",
                        "The content verb 'talk' or key noun topic",
                        "The contraction 'I'd'"
                    ),
                    correctOptionIndex = 2,
                    explanation = "Content words (verbs, nouns, adjectives) carry the key semantic meaning and should receive sentence stress rather than structure words."
                ),
                Question(
                    id = 5,
                    questionText = "Which word has a silent 'p'?",
                    options = listOf(
                        "Pneumonia",
                        "Pertinent",
                        "Perplex",
                        "Propensity"
                    ),
                    correctOptionIndex = 0,
                    explanation = "In 'Pneumonia' (noo-mohn-yah), the letter 'p' is silent."
                )
            )
        )
    )
}
