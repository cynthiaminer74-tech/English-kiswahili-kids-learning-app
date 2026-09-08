const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const HTML_FILE = path.join(__dirname, 'public/index.html');
const APK_PATH = path.join(__dirname, 'app/build/outputs/apk/debug/app-debug.apk');
const FALLBACK_APK_PATH = path.join(__dirname, '.build-outputs/app-debug.apk');

// Kenya CBC Grade 1 English-Kiswahili Vocabulary
const VOCABULARY = [
  // Greetings (Salamu)
  { id: 'g1', category: 'greetings', english: 'Hello', swahili: 'Jambo', phonetic: 'JAHM-boh', emoji: '👋', example: 'Jambo rafiki (Hello friend)' },
  { id: 'g2', category: 'greetings', english: 'How are you?', swahili: 'Habari yako?', phonetic: 'hah-BAH-ree YAH-koh', emoji: '😊', example: 'Habari za asubuhi (Good morning)' },
  { id: 'g3', category: 'greetings', english: 'Thank you', swahili: 'Asante', phonetic: 'ah-SAHN-teh', emoji: '🙏', example: 'Asante sana (Thank you very much)' },
  { id: 'g4', category: 'greetings', english: 'Goodbye', swahili: 'Kwaheri', phonetic: 'kwah-HEH-ree', emoji: '🙋‍♂️', example: 'Kwaheri ya kuonana (Goodbye)' },
  { id: 'g5', category: 'greetings', english: 'Yes', swahili: 'Ndiyo', phonetic: 'n-DEE-yoh', emoji: '👍', example: 'Ndiyo mwalimu (Yes teacher)' },
  { id: 'g6', category: 'greetings', english: 'No', swahili: 'Hapana', phonetic: 'hah-PAH-nah', emoji: '✋', example: 'Hapana asante (No thank you)' },
  { id: 'g7', category: 'greetings', english: 'Please', swahili: 'Tafadhali', phonetic: 'tah-fah-DHAH-lee', emoji: '✨', example: 'Kuja hapa tafadhali (Come here please)' },
  { id: 'g8', category: 'greetings', english: 'Peaceful / Fine', swahili: 'Salama', phonetic: 'sah-LAH-mah', emoji: '🕊️', example: 'Kila kitu ni salama (All is well)' },
  { id: 'g9', category: 'greetings', english: 'Elder Greeting', swahili: 'Shikamoo', phonetic: 'shee-kah-MOH', emoji: '🙇', example: 'Shikamoo mwalimu' },
  { id: 'g10', category: 'greetings', english: 'Elder Reply', swahili: 'Marahaba', phonetic: 'mah-rah-HAH-bah', emoji: '🌟', example: 'Marahaba mwanangu' },

  // Numbers (Nambari 1-10)
  { id: 'n1', category: 'numbers', english: 'One (1)', swahili: 'Moja', phonetic: 'MOH-jah', emoji: '1️⃣', example: 'Kitabu kimoja (One book)' },
  { id: 'n2', category: 'numbers', english: 'Two (2)', swahili: 'Mbili', phonetic: 'm-BEE-lee', emoji: '2️⃣', example: 'Penseli mbili (Two pencils)' },
  { id: 'n3', category: 'numbers', english: 'Three (3)', swahili: 'Tatu', phonetic: 'TAH-too', emoji: '3️⃣', example: 'Viti vitatu (Three chairs)' },
  { id: 'n4', category: 'numbers', english: 'Four (4)', swahili: 'Nne', phonetic: 'n-NEH', emoji: '4️⃣', example: 'Meza nne (Four tables)' },
  { id: 'n5', category: 'numbers', english: 'Five (5)', swahili: 'Tano', phonetic: 'TAH-noh', emoji: '5️⃣', example: 'Vidole vitano (Five fingers)' },
  { id: 'n6', category: 'numbers', english: 'Six (6)', swahili: 'Sita', phonetic: 'SEE-tah', emoji: '6️⃣', example: 'Machungwa sita (Six oranges)' },
  { id: 'n7', category: 'numbers', english: 'Seven (7)', swahili: 'Saba', phonetic: 'SAH-bah', emoji: '7️⃣', example: 'Siku saba (Seven days)' },
  { id: 'n8', category: 'numbers', english: 'Eight (8)', swahili: 'Nane', phonetic: 'NAH-neh', emoji: '8️⃣', example: 'Watoto wanane (Eight children)' },
  { id: 'n9', category: 'numbers', english: 'Nine (9)', swahili: 'Tisa', phonetic: 'TEE-sah', emoji: '9️⃣', example: 'Nyota tisa (Nine stars)' },
  { id: 'n10', category: 'numbers', english: 'Ten (10)', swahili: 'Kumi', phonetic: 'KOO-mee', emoji: '🔟', example: 'Vidole kumi (Ten toes)' },

  // Colors (Rangi)
  { id: 'c1', category: 'colors', english: 'Red', swahili: 'Nyekundu', phonetic: 'nyeh-KOON-doo', emoji: '🔴', example: 'Tofaa jekundu (Red apple)' },
  { id: 'c2', category: 'colors', english: 'Blue', swahili: 'Buluu', phonetic: 'boo-LOO', emoji: '🔵', example: 'Anga ya buluu (Blue sky)' },
  { id: 'c3', category: 'colors', english: 'Yellow', swahili: 'Manjano', phonetic: 'mahn-JAH-noh', emoji: '🟡', example: 'Jua la manjano (Yellow sun)' },
  { id: 'c4', category: 'colors', english: 'Green', swahili: 'Kijani', phonetic: 'kee-JAH-nee', emoji: '🟢', example: 'Majani ya kijani (Green leaves)' },
  { id: 'c5', category: 'colors', english: 'Orange', swahili: 'Machungwa', phonetic: 'mah-CHOON-gwah', emoji: '🟠', example: 'Rangi ya machungwa (Orange color)' },
  { id: 'c6', category: 'colors', english: 'Black', swahili: 'Nyeusi', phonetic: 'nyeh-OO-see', emoji: '⚫', example: 'Ubao mweusi (Blackboard)' },
  { id: 'c7', category: 'colors', english: 'White', swahili: 'Nyeupe', phonetic: 'nyeh-OO-peh', emoji: '⚪', example: 'Karatasi nyeupe (White paper)' },
  { id: 'c8', category: 'colors', english: 'Purple', swahili: 'Zambarau', phonetic: 'zahm-bah-RAH-oo', emoji: '🟣', example: 'Rangi ya zambarau (Purple color)' },

  // Animals (Wanyama)
  { id: 'a1', category: 'animals', english: 'Giraffe', swahili: 'Twiga', phonetic: 'TWEE-gah', emoji: '🦒', example: 'Twiga ana shingo ndefu (Giraffe has a long neck)' },
  { id: 'a2', category: 'animals', english: 'Dog', swahili: 'Mbwa', phonetic: 'm-BWAH', emoji: '🐶', example: 'Mbwa anabweka (The dog barks)' },
  { id: 'a3', category: 'animals', english: 'Cat', swahili: 'Paka', phonetic: 'PAH-kah', emoji: '🐱', example: 'Paka anakunywa maziwa (The cat drinks milk)' },
  { id: 'a4', category: 'animals', english: 'Elephant', swahili: 'Ndovu / Tembo', phonetic: 'n-DOH-voo', emoji: '🐘', example: 'Ndovu ni mkubwa sana (Elephant is very big)' },
  { id: 'a5', category: 'animals', english: 'Lion', swahili: 'Simba', phonetic: 'SEEM-bah', emoji: '🦁', example: 'Simba ni mfalme (The lion is the king)' },
  { id: 'a6', category: 'animals', english: 'Chicken', swahili: 'Kuku', phonetic: 'KOO-koo', emoji: '🐔', example: 'Kuku anataga mayai (Chicken lays eggs)' },
  { id: 'a7', category: 'animals', english: 'Goat', swahili: 'Mbuzi', phonetic: 'm-BOO-zee', emoji: '🐐', example: 'Mbuzi anakula nyasi (Goat eats grass)' },
  { id: 'a8', category: 'animals', english: 'Cow', swahili: 'Ng’ombe', phonetic: 'NG-om-beh', emoji: '🐮', example: 'Ng’ombe anatupa maziwa (Cow gives us milk)' },
  { id: 'a9', category: 'animals', english: 'Rabbit', swahili: 'Sungura', phonetic: 'soon-GOO-rah', emoji: '🐰', example: 'Sungura ni mwerevu (The rabbit is clever)' },

  // Family (Familia)
  { id: 'f1', category: 'family', english: 'Mother', swahili: 'Mama', phonetic: 'MAH-mah', emoji: '👩', example: 'Ninampenda mama yangu (I love my mother)' },
  { id: 'f2', category: 'family', english: 'Father', swahili: 'Baba', phonetic: 'BAH-bah', emoji: '👨', example: 'Baba anafanya kazi (Father is working)' },
  { id: 'f3', category: 'family', english: 'Child / Baby', swahili: 'Mtoto', phonetic: 'm-TOH-toh', emoji: '👶', example: 'Mtoto anacheka (The baby is laughing)' },
  { id: 'f4', category: 'family', english: 'Brother', swahili: 'Kaka', phonetic: 'KAH-kah', emoji: '👦', example: 'Kaka yangu anasoma (My brother is reading)' },
  { id: 'f5', category: 'family', english: 'Sister', swahili: 'Dada', phonetic: 'DAH-dah', emoji: '👧', example: 'Dada yangu anaimba (My sister is singing)' },
  { id: 'f6', category: 'family', english: 'Grandfather', swahili: 'Babu', phonetic: 'BAH-boo', emoji: '👴', example: 'Babu ananisimulia hadithi (Grandpa tells me stories)' },
  { id: 'f7', category: 'family', english: 'Grandmother', swahili: 'Nyanya / Bibi', phonetic: 'NYAH-nyah', emoji: '👵', example: 'Nyanya anapika chakula kitamu (Grandma cooks tasty food)' },

  // Body Parts (Sehemu za Mwili)
  { id: 'b1', category: 'body', english: 'Head', swahili: 'Kichwa', phonetic: 'KEE-chwah', emoji: '🗣️', example: 'Tikisa kichwa chako (Shake your head)' },
  { id: 'b2', category: 'body', english: 'Eyes', swahili: 'Macho', phonetic: 'MAH-choh', emoji: '👀', example: 'Ninaona kwa macho (I see with my eyes)' },
  { id: 'b3', category: 'body', english: 'Ears', swahili: 'Masikio', phonetic: 'mah-see-KEE-oh', emoji: '👂', example: 'Ninasikia kwa masikio (I hear with my ears)' },
  { id: 'b4', category: 'body', english: 'Nose', swahili: 'Pua', phonetic: 'POO-ah', emoji: '👃', example: 'Ninanusa kwa pua (I smell with my nose)' },
  { id: 'b5', category: 'body', english: 'Mouth', swahili: 'Mdomo', phonetic: 'm-DOH-moh', emoji: '👄', example: 'Ninazungumza kwa mdomo (I speak with my mouth)' },
  { id: 'b6', category: 'body', english: 'Hands', swahili: 'Mikono', phonetic: 'mee-KOH-noh', emoji: '👐', example: 'Piga makofi kwa mikono (Clap your hands)' },
  { id: 'b7', category: 'body', english: 'Feet / Legs', swahili: 'Miguu', phonetic: 'mee-GOO-oo', emoji: '🦵', example: 'Kimbia kwa miguu (Run with your legs)' },
  { id: 'b8', category: 'body', english: 'Teeth', swahili: 'Meno', phonetic: 'MEH-noh', emoji: '🦷', example: 'Piga mswaki meno yako (Brush your teeth)' }
];

const CATEGORIES = [
  { id: 'all', name: 'All Words', swahiliName: 'Maneno Yote', emoji: '🌈' },
  { id: 'greetings', name: 'Greetings', swahiliName: 'Salamu', emoji: '👋' },
  { id: 'numbers', name: 'Numbers', swahiliName: 'Nambari', emoji: '🔢' },
  { id: 'colors', name: 'Colors', swahiliName: 'Rangi', emoji: '🎨' },
  { id: 'animals', name: 'Animals', swahiliName: 'Wanyama', emoji: '🦒' },
  { id: 'family', name: 'Family', swahiliName: 'Familia', emoji: '👨‍👩‍👧' },
  { id: 'body', name: 'Body Parts', swahiliName: 'Sehemu za Mwili', emoji: '👀' }
];

const server = http.createServer((req, res) => {
  const parsedUrl = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  const pathname = parsedUrl.pathname;

  // Health
  if (pathname === '/health' || pathname === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'ok', uptime: process.uptime() }));
    return;
  }

  // API Words
  if (pathname === '/api/words') {
    res.writeHead(200, {
      'Content-Type': 'application/json; charset=utf-8',
      'Access-Control-Allow-Origin': '*'
    });
    res.end(JSON.stringify({ categories: CATEGORIES, vocabulary: VOCABULARY }));
    return;
  }

  // APK Download
  if (pathname === '/download/app-debug.apk' || pathname === '/app-debug.apk') {
    let target = null;
    if (fs.existsSync(APK_PATH)) target = APK_PATH;
    else if (fs.existsSync(FALLBACK_APK_PATH)) target = FALLBACK_APK_PATH;

    if (target) {
      const stat = fs.statSync(target);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="EnglishKiswahiliKids-debug.apk"'
      });
      fs.createReadStream(target).pipe(res);
      return;
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('APK file not found on server.');
      return;
    }
  }

  // Root or HTML
  if (pathname === '/' || pathname === '/index.html') {
    if (fs.existsSync(HTML_FILE)) {
      const html = fs.readFileSync(HTML_FILE, 'utf8');
      res.writeHead(200, {
        'Content-Type': 'text/html; charset=utf-8',
        'Cache-Control': 'no-cache'
      });
      res.end(html);
      return;
    }
  }

  // 404
  res.writeHead(404, { 'Content-Type': 'text/plain' });
  res.end('404 Not Found');
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[English Kiswahili Kids] Server running on http://0.0.0.0:${PORT}`);
});

process.on('SIGTERM', () => {
  console.log('SIGTERM received, shutting down gracefully');
  server.close(() => process.exit(0));
});
