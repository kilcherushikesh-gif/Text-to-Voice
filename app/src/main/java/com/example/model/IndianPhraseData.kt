package com.example.model

data class IndianPhrase(
    val id: String,
    val language: IndianLanguage,
    val category: PhraseCategory,
    val scriptText: String,
    val transliteration: String,
    val englishMeaning: String
)

enum class PhraseCategory(val displayName: String, val icon: String) {
    GREETINGS("Greetings & Respect", "🙏"),
    DAILY_LIFE("Chai & Daily Talks", "☕"),
    FESTIVALS("Festivals & Pride", "🪔"),
    BOLLYWOOD("Dialogues & Punchlines", "🎬"),
    WISDOM("Suvichar & Proverbs", "✨")
}

object IndianPhraseData {
    val phrases: List<IndianPhrase> = listOf(
        // MARATHI PHRASES
        IndianPhrase(
            id = "mr_1",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.GREETINGS,
            scriptText = "नमस्कार! आपण कसे आहात? तुमची भेट होऊन खूप आनंद झाला.",
            transliteration = "Namaskar! Aapan kase aahat? Tumchi bhet houn khoop aanand zhala.",
            englishMeaning = "Greetings! How are you? Very delighted to meet you."
        ),
        IndianPhrase(
            id = "mr_2",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.GREETINGS,
            scriptText = "शुभ सकाळ! आजचा दिवस तुमच्यासाठी आनंद आणि यश घेऊन येवो.",
            transliteration = "Shubh Sakaal! Aajcha diwas tumchyasathi aanand aani yash gheun yevo.",
            englishMeaning = "Good morning! May today bring happiness and success for you."
        ),
        IndianPhrase(
            id = "mr_3",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "चला, मस्त गरमागरम आलं घातलेला चहा पिऊया!",
            transliteration = "Chala, masta garamgaram aala ghaatlela chaha peeuya!",
            englishMeaning = "Come on, let's have some hot ginger cutting chai!"
        ),
        IndianPhrase(
            id = "mr_4",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "काय मग मंडळी, आजचा काय बेत आहे?",
            transliteration = "Kay mag mandali, aajcha kay bet aahe?",
            englishMeaning = "What's up folks, what's the plan for today?"
        ),
        IndianPhrase(
            id = "mr_5",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.FESTIVALS,
            scriptText = "गणपती बाप्पा मोरया, मंगलमूर्ती मोरया! बाप्पाच्या कृपेने सर्वांचे कल्याण होवो.",
            transliteration = "Ganpati Bappa Morya, Mangalmoorti Morya!",
            englishMeaning = "Ganpati Bappa Morya! May Lord Ganesha bless everyone."
        ),
        IndianPhrase(
            id = "mr_6",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.FESTIVALS,
            scriptText = "जय भवानी, जय शिवाजी! महाराष्ट्राची शान आणि आमचा अभिमान.",
            transliteration = "Jai Bhavani, Jai Shivaji!",
            englishMeaning = "Jai Bhavani, Jai Shivaji! The pride and honour of Maharashtra."
        ),
        IndianPhrase(
            id = "mr_7",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.FESTIVALS,
            scriptText = "दिवाळीच्या आणि नवीन वर्षाच्या तुम्हाला व तुमच्या परिवारास हार्दिक शुभेच्छा!",
            transliteration = "Diwalichya aani naveen varshachya hardik shubhechha!",
            englishMeaning = "Heartfelt greetings for Diwali and the New Year to you and your family!"
        ),
        IndianPhrase(
            id = "mr_8",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "अरे देवा! काय हे? एकदा ठरवलं की मागे हटायचं नाही!",
            transliteration = "Are Deva! Kay he? Ekda tharavla ki maage hataycha naahi!",
            englishMeaning = "Oh God! What is this? Once decided, there is no stepping back!"
        ),
        IndianPhrase(
            id = "mr_9",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.WISDOM,
            scriptText = "प्रयत्नांती परमेश्वर! कष्टाशिवाय फळ नाही, म्हणून सतत प्रयत्नशील राहा.",
            transliteration = "Prayatnaanti Parameshwar! Kashtaashivaay phal naahi.",
            englishMeaning = "God resides in sincere effort! There is no fruit without hard work."
        ),
        IndianPhrase(
            id = "mr_10",
            language = IndianLanguage.MARATHI,
            category = PhraseCategory.WISDOM,
            scriptText = "वाचाल तर वाचाल! पुस्तके ही माणसाची सर्वात चांगली मित्र असतात.",
            transliteration = "Waachaal tar waachaal! Pustake hi changli mitra astat.",
            englishMeaning = "If you read, you will survive and thrive! Books are humankind's best friends."
        ),

        // HINDI PHRASES
        IndianPhrase(
            id = "hi_1",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.GREETINGS,
            scriptText = "नमस्ते! आप कैसे हैं? आशा है आपका दिन बहुत अच्छा बीत रहा है।",
            transliteration = "Namaste! Aap kaise hain? Aasha hai din achha beet raha hai.",
            englishMeaning = "Hello! How are you? Hope your day is going wonderfully."
        ),
        IndianPhrase(
            id = "hi_2",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.GREETINGS,
            scriptText = "सुप्रभात! ईश्वर करे आज का दिन आपके जीवन में नई उमंग और खुशियां लाए।",
            transliteration = "Suprabhat! Ishwar kare aaj ka din khushiyan laaye.",
            englishMeaning = "Good morning! May God bring new zeal and joy into your life today."
        ),
        IndianPhrase(
            id = "hi_3",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "अरे भाई! क्या हाल-चाल है? बहुत दिन बाद मिलना हुआ, चलो चाय पीते हैं।",
            transliteration = "Are bhai! Kya haal chaal hai? Chalo chai peete hain.",
            englishMeaning = "Hey brother! How are things? Meeting after so long, let's have chai."
        ),
        IndianPhrase(
            id = "hi_4",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "चिंता मत करो यार, सब कुछ ठीक हो जाएगा। बस हिम्मत रखो।",
            transliteration = "Chinta mat karo yaar, sab theek ho jaayega.",
            englishMeaning = "Don't worry my friend, everything will be fine. Just stay strong."
        ),
        IndianPhrase(
            id = "hi_5",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.FESTIVALS,
            scriptText = "जय हिन्द, वन्दे मातरम्! हमारे देश भारत पर हमें गर्व है।",
            transliteration = "Jai Hind, Vande Mataram!",
            englishMeaning = "Victory to India, I bow to the motherland! Proud to be an Indian."
        ),
        IndianPhrase(
            id = "hi_6",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.FESTIVALS,
            scriptText = "आपको और आपके पूरे परिवार को होली और दीवाली की ढेरों मंगलकामनाएं!",
            transliteration = "Aapko Holi aur Diwali ki dheron mangal-kaamnaayein!",
            englishMeaning = "Heartiest warm festive greetings of Holi and Diwali to you and your family!"
        ),
        IndianPhrase(
            id = "hi_7",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "पिक्चर अभी बाकी है मेरे दोस्त! कभी हार मत मानना।",
            transliteration = "Picture abhi baaki hai mere dost! Kabhi haar mat maanna.",
            englishMeaning = "The movie isn't over yet my friend! Never give up."
        ),
        IndianPhrase(
            id = "hi_8",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "बाबूमोशाय, जिंदगी बड़ी होनी चाहिए, लंबी नहीं!",
            transliteration = "Babumoshai, zindagi badi honi chahiye, lambi nahi!",
            englishMeaning = "Life should be grand and meaningful, not just long!"
        ),
        IndianPhrase(
            id = "hi_9",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.WISDOM,
            scriptText = "कर्म ही पूजा है। फल की चिंता किए बिना अपना सर्वश्रेष्ठ योगदान दें।",
            transliteration = "Karm hi pooja hai.",
            englishMeaning = "Work is worship. Give your absolute best without undue anxiety over results."
        ),
        IndianPhrase(
            id = "hi_10",
            language = IndianLanguage.HINDI,
            category = PhraseCategory.WISDOM,
            scriptText = "मंजिलें उन्हीं को मिलती हैं जिनके सपनों में जान होती है, हौसलों से उड़ान होती है।",
            transliteration = "Manzilein unhi ko milti hain jinke sapno mein jaan hoti hai.",
            englishMeaning = "Destinations belong to those with living dreams; flights take wing on courage."
        ),

        // INDIAN ENGLISH PHRASES
        IndianPhrase(
            id = "en_1",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.GREETINGS,
            scriptText = "Namaste ji! Hearty welcome to you. Hope you are having a wonderful day.",
            transliteration = "Namaste ji! Hearty welcome to you.",
            englishMeaning = "Respectful Indian greeting welcoming the listener."
        ),
        IndianPhrase(
            id = "en_2",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.GREETINGS,
            scriptText = "Good morning everyone! Let's start this beautiful day on a positive note.",
            transliteration = "Good morning everyone!",
            englishMeaning = "Cheerful morning greeting with positive energy."
        ),
        IndianPhrase(
            id = "en_3",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "Please do one thing, check this message and revert back as soon as possible.",
            transliteration = "Please do one thing, revert back ASAP.",
            englishMeaning = "Classic polite Indian communicative phrasing."
        ),
        IndianPhrase(
            id = "en_4",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "Let's take a quick tea break, cutting chai and samosa are ready!",
            transliteration = "Cutting chai and samosa time!",
            englishMeaning = "Traditional Indian afternoon tea invitation."
        ),
        IndianPhrase(
            id = "en_5",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "Don't take any tension, everything is first class and sorted out.",
            transliteration = "No tension, everything is first class.",
            englishMeaning = "Reassuring Indian expression of confidence and calmness."
        ),
        IndianPhrase(
            id = "en_6",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.FESTIVALS,
            scriptText = "Wishing you and your family abundant peace, prosperity, and joyous celebrations!",
            transliteration = "Festive blessings for you and family.",
            englishMeaning = "Festive greeting celebrating Indian traditions."
        ),
        IndianPhrase(
            id = "en_7",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.FESTIVALS,
            scriptText = "Jai Hind! Celebrating the incredible unity in diversity of our motherland.",
            transliteration = "Jai Hind! Unity in diversity.",
            englishMeaning = "Patriotic salute honoring Indian heritage."
        ),
        IndianPhrase(
            id = "en_8",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "Picture is still not over, my friend! Keep walking with your head held high.",
            transliteration = "Picture abhi baaki hai, my friend!",
            englishMeaning = "English rendition of the iconic Bollywood sentiment."
        ),
        IndianPhrase(
            id = "en_9",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.WISDOM,
            scriptText = "Where there is a sincere will, a thousand doors open naturally.",
            transliteration = "Where there is a will, doors open.",
            englishMeaning = "Timeless Indian wisdom on determination and belief."
        ),
        IndianPhrase(
            id = "en_10",
            language = IndianLanguage.INDIAN_ENGLISH,
            category = PhraseCategory.WISDOM,
            scriptText = "Knowledge and humility together create true wisdom in life.",
            transliteration = "Vidya dadati vinayam.",
            englishMeaning = "Wisdom rooted in the ancient adage that education brings humility."
        ),

        // TAMIL PHRASES (தமிழ்)
        IndianPhrase(
            id = "ta_1",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.GREETINGS,
            scriptText = "வணக்கம்! நீங்கள் எப்படி இருக்கிறீர்கள்? உங்களை சந்தித்ததில் மிக்க மகிழ்ச்சி.",
            transliteration = "Vanakkam! Neengal eppadi irukkeergal? Ungalai santhithathil mikka magizhchi.",
            englishMeaning = "Greetings! How are you? Very happy to meet you."
        ),
        IndianPhrase(
            id = "ta_2",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.GREETINGS,
            scriptText = "காலை வணக்கம்! இந்த நாள் உங்களுக்கு இனிய நாளாகவும் வெற்றிகரமாகவும் அமையட்டும்.",
            transliteration = "Kaalai vanakkam! Intha naal ungalukku iniya naalaaga amaiyattum.",
            englishMeaning = "Good morning! May this day be pleasant and successful for you."
        ),
        IndianPhrase(
            id = "ta_3",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "வாங்க, சுடச்சுட ஒரு கப் பில்டர் காபி குடிக்கலாம்!",
            transliteration = "Vaanga, sudasuda oru cup filter coffee kudikkalaam!",
            englishMeaning = "Come on, let's have a piping hot cup of South Indian filter coffee!"
        ),
        IndianPhrase(
            id = "ta_4",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "கவலைப்படாதீங்க, எல்லாம் நல்லபடியாக நடக்கும். தைரியமாக இருங்கள்.",
            transliteration = "Kavalai padaatheenga, ellaam nallapadiyaaga nadakkum.",
            englishMeaning = "Don't worry, everything will turn out well. Stay courageous."
        ),
        IndianPhrase(
            id = "ta_5",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.FESTIVALS,
            scriptText = "பொங்கல் திருநாள் மற்றும் தமிழ் புத்தாண்டு நல்வாழ்த்துகள்! பொங்கலோ பொங்கல்!",
            transliteration = "Pongal thirunaal matrum Tamil Puthaandu nalvaazhthukkal! Pongalo Pongal!",
            englishMeaning = "Happy Pongal and Tamil New Year greetings! Pongalo Pongal!"
        ),
        IndianPhrase(
            id = "ta_6",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.FESTIVALS,
            scriptText = "தமிழ் வாழ்க! பாரத தாயின் பெருமை என்றும் நிலைக்கட்டும்.",
            transliteration = "Tamil vaazhga! Bharatha thaayin perumai endrum nilaikkattum.",
            englishMeaning = "Long live Tamil! May Mother India's glory endure forever."
        ),
        IndianPhrase(
            id = "ta_7",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "நான் ஒரு தடவ சொன்னா, நூறு தடவ சொன்ன மாதிரி!",
            transliteration = "Naan oru thadava sonna, nooru thadava sonna maathiri!",
            englishMeaning = "Iconic punchline: If I say it once, it's as good as having said it a hundred times!"
        ),
        IndianPhrase(
            id = "ta_8",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "சிங்கம் சிங்கிளா தான் வரும்! பயம் என்பது நமது அகராதியிலேயே இல்லை.",
            transliteration = "Singam singlaa thaan varum!",
            englishMeaning = "The lion always walks alone! Fear has no place in our dictionary."
        ),
        IndianPhrase(
            id = "ta_9",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.WISDOM,
            scriptText = "யாதும் ஊரே யாவரும் கேளிர்! உலகம் முழுவதையும் நம் குடும்பமாக கருதுவோம்.",
            transliteration = "Yaathum oore yaavarum keelir!",
            englishMeaning = "Every place is our home, and everyone our kin! Universal brotherhood."
        ),
        IndianPhrase(
            id = "ta_10",
            language = IndianLanguage.TAMIL,
            category = PhraseCategory.WISDOM,
            scriptText = "முயற்சி திருவினையாக்கும்! விடாமுயற்சியே மனிதனை வெற்றியாளனாக்கும்.",
            transliteration = "Muyarchi thiruvinaiyaakkum! Vidaamuyarchiye vettriyai tharum.",
            englishMeaning = "Sincere effort yields prosperity! Perseverance transforms a person into a winner."
        ),

        // TELUGU PHRASES (తెలుగు)
        IndianPhrase(
            id = "te_1",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.GREETINGS,
            scriptText = "నమస్కారం! మీరు ఎలా ఉన్నారు? బాగున్నారా? మిమ్మల్ని కలవడం చాలా సంతోషంగా ఉంది.",
            transliteration = "Namaskaram! Meeru ela unnaaru? Baagunnaaraa? Mimmilni kalavadam chala santhosham.",
            englishMeaning = "Greetings! How are you? Are you doing well? Very glad to meet you."
        ),
        IndianPhrase(
            id = "te_2",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.GREETINGS,
            scriptText = "శుభోదయం! ఈ రోజు మీకు అంతా మంచే జరగాలని, సంతోషంగా ఉండాలని కోరుకుంటున్నాను.",
            transliteration = "Shubhodhayam! Ee roju meeku anthaa manche jaragaalani korukuntunnaanu.",
            englishMeaning = "Good morning! Wishing you success, positivity and joy throughout today."
        ),
        IndianPhrase(
            id = "te_3",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "రండి, వేడి వేడి టీ లేదా ఫిల్టర్ కాఫీ తాగుదాం!",
            transliteration = "Randi, vedi vedi tea leda filter coffee thaagudaam!",
            englishMeaning = "Come on, let's have some hot tea or fresh filter coffee!"
        ),
        IndianPhrase(
            id = "te_4",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "ఏం పర్లేదు, అంతా మంచే జరుగుతుంది. ధైర్యంగా ముందుకు సాగండి.",
            transliteration = "Eam parledu, anthaa manche jaruguthundi. Dhairyangaa undandi.",
            englishMeaning = "No worries at all, everything will be fine. Move forward with confidence."
        ),
        IndianPhrase(
            id = "te_5",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.FESTIVALS,
            scriptText = "మీకు మరియు మీ కుటుంబ సభ్యులందరికీ సంక్రాంతి మరియు ఉగాది శుభాకాంక్షలు!",
            transliteration = "Meeku mariyu mee kutumba sabhyulandarikii Sankranti mariyu Ugadi shubhaakaankshalu!",
            englishMeaning = "Heartiest Sankranti and Ugadi festive greetings to you and all family members!"
        ),
        IndianPhrase(
            id = "te_6",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.FESTIVALS,
            scriptText = "జై హింద్, వందే మాతరం! మన సంస్కృతి, మన సంప్రదాయాలు మన గర్వం.",
            transliteration = "Jai Hind, Vande Mataram! Mana samskruthi mana garvam.",
            englishMeaning = "Jai Hind, Vande Mataram! Our culture and heritage are our pride."
        ),
        IndianPhrase(
            id = "te_7",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "ఒక్కసారి కమిట్ అయితే నా మాట నేనే వినను!",
            transliteration = "Okkasaari commit aithe naa maata nene vinanu!",
            englishMeaning = "Famous Tollywood punchline: Once I commit, I don't even listen to myself!"
        ),
        IndianPhrase(
            id = "te_8",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "చూడు ఒక వైపే చూడు, రెండో వైపు చూడకు!",
            transliteration = "Choodu oka vaipe choodu, rendo vaipu choodaku!",
            englishMeaning = "Mass dialogue: Look only at one side, don't try to look at the other side!"
        ),
        IndianPhrase(
            id = "te_9",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.WISDOM,
            scriptText = "కృషితో నాస్తి దుర్భిక్షం — కష్టపడితే సాధించలేనిది ఏదీ లేదు.",
            transliteration = "Krushitho naasti durbhiksham — kashtapadithe saadhinchalenidi edi ledu.",
            englishMeaning = "With hard work there is no famine — nothing is unattainable through dedication."
        ),
        IndianPhrase(
            id = "te_10",
            language = IndianLanguage.TELUGU,
            category = PhraseCategory.WISDOM,
            scriptText = "విద్యే నిజమైన ధనం, వినయమే మనిషికి నిజమైన ఆభరణం.",
            transliteration = "Vidhye nijamaina dhanam, vinayame nijamaina aabharanam.",
            englishMeaning = "Knowledge is the true wealth, and humility is the true ornament of humankind."
        ),

        // BENGALI PHRASES (বাংলা)
        IndianPhrase(
            id = "bn_1",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.GREETINGS,
            scriptText = "নমস্কার! আপনি কেমন আছেন? আপনার সাথে দেখা হয়ে খুব ভালো লাগলো।",
            transliteration = "Nomoshkar! Aapni kemon aachen? Aaponar sathe dekha hoye khub bhalo laaglo.",
            englishMeaning = "Greetings! How are you? Very pleased to meet you."
        ),
        IndianPhrase(
            id = "bn_2",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.GREETINGS,
            scriptText = "সুপ্রভাত! আশা করি আজকের দিনটি আপনার জীবনে নতুন আনন্দ ও শান্তি নিয়ে আসবে।",
            transliteration = "Suprabhat! Aasha kori aajker dinti anando o shaanti niye aashbe.",
            englishMeaning = "Good morning! Hope today brings renewed joy and peace into your life."
        ),
        IndianPhrase(
            id = "bn_3",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "চলুন, এক কাপ গরম চা আর মিষ্টি খেয়ে জমিয়ে আড্ডা দেওয়া যাক!",
            transliteration = "Cholun, ek cup gorom chaa aar mishti kheye adda dewa jaak!",
            englishMeaning = "Come, let's have a hot cup of tea with sweets and enjoy a lively chat!"
        ),
        IndianPhrase(
            id = "bn_4",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.DAILY_LIFE,
            scriptText = "কোনো চিন্তা করবেন না, সব ঠিক হয়ে যাবে। মনের জোর রাখুন।",
            transliteration = "Kono chinta korben na, shob theek hoye jaabe. Moner jor raakhun.",
            englishMeaning = "Don't worry at all, everything will be fine. Keep faith in your heart."
        ),
        IndianPhrase(
            id = "bn_5",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.FESTIVALS,
            scriptText = "শুভ দুর্গোৎসব ও শুভ নববর্ষের আন্তরিক প্রীতি ও শুভেচ্ছা!",
            transliteration = "Shubho Durgotsab o Shubho Nababarsher aontorik preeti o shubheccha!",
            englishMeaning = "Heartfelt greetings and love for Durga Puja and the Bengali New Year!"
        ),
        IndianPhrase(
            id = "bn_6",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.FESTIVALS,
            scriptText = "জয় হিন্দ, বন্দে মাতরম! ভারতের মহান ঐতিহ্য আমাদের গর্ব।",
            transliteration = "Joy Hind, Vande Mataram! Bharater mohan aitijhyo aamader gorbo.",
            englishMeaning = "Joy Hind, Vande Mataram! India's grand heritage is our pride."
        ),
        IndianPhrase(
            id = "bn_7",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "জীবনটা অনেক বড় হওয়া উচিত, শুধু দীর্ঘ নয়!",
            transliteration = "Jibon-ta onek boro howa uchit, shudhu deergho noy!",
            englishMeaning = "Life should be purposeful and grand, not merely long!"
        ),
        IndianPhrase(
            id = "bn_8",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.BOLLYWOOD,
            scriptText = "যে মানুষ ভালোবাসতে জানে, সে পৃথিবীর সবচেয়ে ধনী মানুষ।",
            transliteration = "Je manush bhalobashte jaane, shey prithibir shobcheye dhoni manush.",
            englishMeaning = "The person who knows how to love is the richest person in the world."
        ),
        IndianPhrase(
            id = "bn_9",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.WISDOM,
            scriptText = "পরিশ্রমই সৌভাগ্যের প্রসূতি। যে সৎ পথে চলে, ঈশ্বর তার সহায় হন।",
            transliteration = "Porishrom-i shoubhagger proshuti. Ishwar shot pother shohay hon.",
            englishMeaning = "Hard work is the mother of good fortune. God supports those on the path of truth."
        ),
        IndianPhrase(
            id = "bn_10",
            language = IndianLanguage.BENGALI,
            category = PhraseCategory.WISDOM,
            scriptText = "জ্ঞান হলো এমন এক আলো, যা মানুষের অন্তরের অন্ধকার দূর করে।",
            transliteration = "Gyaan holo emon ek aalo, jaa ontorer ondhokar door kore.",
            englishMeaning = "Knowledge is the light that dispels the darkness within the human soul."
        )
    )
}
