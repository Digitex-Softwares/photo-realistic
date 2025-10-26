require('dotenv').config();
const express = require('express');
const multer = require('multer');
const fs = require('fs');
const TelegramBot = require('node-telegram-bot-api');

const app = express();
const upload = multer({ dest: 'uploads/' });
const bot = new TelegramBot(process.env.BOT_TOKEN);

app.get('/', (req, res) => {
  res.send('🚀 Telegram Photo Bot Server is running successfully!');
});

// Endpoint that your Android app will POST to
app.post('/upload', upload.single('photo'), async (req, res) => {
  try {
    const filePath = req.file.path;
    const chatId = process.env.CHAT_ID;

    await bot.sendPhoto(chatId, fs.createReadStream(filePath), {
      caption: '📸 New photo received from your app!',
    });

    fs.unlinkSync(filePath); // delete uploaded temp file
    res.send({ success: true, message: 'Photo sent to Telegram successfully!' });
  } catch (err) {
    console.error(err);
    res.status(500).send({ success: false, error: err.message });
  }
});

app.listen(process.env.PORT || 10000, () =>
  console.log(`✅ Server running on port ${process.env.PORT || 10000}`)
);
