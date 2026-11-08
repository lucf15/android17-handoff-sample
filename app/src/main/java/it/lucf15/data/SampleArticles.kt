package it.lucf15.data

import it.lucf15.domain.model.Article
import it.lucf15.domain.model.Section

internal object SampleArticles {
    val all =
        listOf(
            Article(
                "quiet-design",
                "Form",
                "Design",
                "Design that makes room for what matters",
                "Less noise, more intention. Small choices that change the way we inhabit our digital spaces.",
                5,
                0,
                listOf(
                    Section(
                        "A little breathing room",
                        "Some objects demand attention. Others give it back. A table beside a window, a carefully positioned lamp, a page that takes its time. Good design often begins here: deciding what to leave out.",
                    ),
                    Section(
                        "The value of a pause",
                        "Imagine a studio rethinking its workspace. Its first decision is not about the colour of the walls. It is about quiet: fewer crowded surfaces, fewer interruptions, more time to notice things. Every object needs a reason to be there.",
                    ),
                    Section(
                        "A gentle hierarchy",
                        "An interface can make room too. A clear heading, one primary action and a predictable rhythm help us find what we need. Simplicity is not an empty screen. It is knowing where to look and what to do next.",
                    ),
                    Section(
                        "Materials that last",
                        "Wood, paper and metal age differently. Some marks tell a story of use; others reveal a careless choice. In digital spaces, the materials are proportions, words and behaviours. Their quality becomes clear after a hundred visits, when novelty gives way to familiarity.",
                    ),
                    Section(
                        "Details with a purpose",
                        "A short transition can explain where something went. A generous margin can make a page easier to read. A quiet confirmation can tell us our work is safe. These details help because they answer a question, even one we had not thought to ask.",
                    ),
                    Section(
                        "Take your place with you",
                        "Changing screens does not change our intention. We were reading, thinking, choosing. A continuous experience keeps that thread without asking us to begin again. The device becomes another place to carry on with the same story.",
                    ),
                    Section(
                        "Finding the right balance",
                        "Our imaginary studio chooses fewer pieces of furniture and softer light. Not because minimalism is a rule, but because the room needs to support the people who use it. The right balance always starts with them.",
                    ),
                    Section(
                        "Leave a door open",
                        "Perhaps the best design is the one we did not notice immediately. It let us finish a sentence, find an idea and return to where we left off. A thoughtful space does not fill every pause. It makes one possible.",
                    ),
                ),
            ),
            Article(
                "slow-city",
                "Elsewhere",
                "Travel",
                "Discover a city, one quiet street at a time",
                "Side streets, small cafés and the pleasure of having nowhere to be.",
                4,
                1,
                story(
                    "The longer way round",
                    "The journey begins when we put away the map. In the imaginary city of this story, arcades change colour at every corner and the smell of fresh bread follows us down the first street.",
                ),
            ),
            Article(
                "everyday-tech",
                "Current",
                "Technology",
                "The best technology gets out of your way",
                "When our tools stop interrupting and start helping us carry on.",
                6,
                2,
                story(
                    "One less step",
                    "The scene is simple: you close one device and open another. Your work continues where you left it. There is no new routine to learn, only an intention that carries on.",
                ),
            ),
            Article(
                "morning-light",
                "Form",
                "Design",
                "A familiar room in a different light",
                "Long shadows, natural materials and the quiet possibilities of morning.",
                3,
                3,
                story(
                    "First light",
                    "Early in the morning, a familiar room feels different. Slanting light reveals the edge of a shelf, the weave of a curtain and a detail that went unnoticed the night before.",
                ),
            ),
            Article(
                "small-garden",
                "Roots",
                "Living",
                "A small garden with room to grow",
                "A daily practice of slowing down and finding a rhythm of your own.",
                4,
                4,
                story(
                    "Begin with a leaf",
                    "A pot, a little light and a willingness to pay attention are enough. The small garden in our story begins with a plant received as a gift, rather than a perfect plan.",
                ),
            ),
            Article(
                "weekend-offline",
                "Elsewhere",
                "Travel",
                "Two days beyond the notifications",
                "An imaginary escape through hills, books and unhurried breakfasts.",
                5,
                5,
                story(
                    "Travel light",
                    "There is a book in the bag, a notebook and not much else. You do not need to go far to change your pace. Sometimes you only need to leave a few hours of the weekend unplanned.",
                ),
            ),
        )

    fun find(id: String?) = all.find { it.id == id }

    private fun story(first: String, opening: String) =
        listOf(
            Section(first, opening),
            Section(
                "Notice before you choose",
                "Take time to look at the details. The rhythm of a street, the way someone uses an object, the small habits that make a place familiar. A story that stays with us often begins with these ordinary clues.",
            ),
            Section(
                "A different pace",
                "Not everything needs to happen immediately. We can leave a question open, return to a page and follow a thought without turning it into a decision. Slowing down means choosing what deserves our attention.",
            ),
            Section(
                "What stays with us",
                "At the end of the day, we remember a colour, a conversation and the moment we stopped checking the time. Simple experiences can leave lasting impressions because we gave ourselves time to enjoy them.",
            ),
            Section(
                "Pick up the thread",
                "We return home with a few ideas and a slightly different perspective. The story continues in tomorrow's small choices: make room, look closer and keep what matters.",
            ),
        )
}
